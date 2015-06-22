package com.example.eric.myweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidgetInfo extends AppWidgetProvider {

    private boolean isFirst = true;

    //启动AppWidgetService服务对应的action
    private final Intent WIDGET_SERVICE_INTENT = new Intent("android.appwidget.action.APP_WIDGET_SERVICE");
    // 更新widget的广播对应的action
    private final String ACTION_UPDATE_WEATHER = "pku.eric.mywidget.UPDATE_WEATHER";
    // 保存widget的id的HashSet，每新建一个widget都会为该widget分配一个id。
    private static Set idsSet = new HashSet();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            idsSet.add(Integer.valueOf(appWidgetIds[i]));
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds){
        // 当 widget 被删除时，对应的删除set中保存的widget的id
        for (int appWidgetId : appWidgetIds) {
            idsSet.remove(Integer.valueOf(appWidgetId));
        }
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        // 在第一个 widget 被创建时，开启服务
        context.startService(WIDGET_SERVICE_INTENT);
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        // 在最后一个 widget 被删除时，终止服务
        context.stopService(WIDGET_SERVICE_INTENT);

        super.onDisabled(context);
    }

    //接收广播的回掉函数
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d("Widget", "OnReceive:Action: " + action);
        if("android.appwidget.action.APPWIDGET_UPDATE".equals(action) && isFirst) {
            isFirst = false;
            //开启服务
            //context.startService(WIDGET_SERVICE_INTENT);
        }
        if (ACTION_UPDATE_WEATHER.equals(action)) {
            // “更新”广播
            //updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet);
            Log.d("Widget","更新广播！！！！");
            String wendu = intent.getStringExtra("Temp");
            String cityName = intent.getStringExtra("City");
            String weatherType = intent.getStringExtra("Type");
            int typeId = intent.getIntExtra("TypeId", 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_info);
            views.setTextViewText(R.id.tempNowWidgetTV, wendu);
            views.setTextViewText(R.id.LocationWidgetTV, cityName);
            views.setTextViewText(R.id.weatherDesWidgetTV, weatherType);
            views.setImageViewResource(R.id.weatherImgWidgetIV,typeId);
            int appID;
            // 迭代器，用于遍历所有保存的widget的id
            Iterator it = idsSet.iterator();
            while (it.hasNext()) {
                appID = ((Integer) it.next()).intValue();
                AppWidgetManager.getInstance(context).updateAppWidget(appID, views);
            }

        }
        super.onReceive(context, intent);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_info);
        //views.setTextViewText(R.id.weatherDesWidgetTV, widgetText);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

        views.setOnClickPendingIntent(R.id.weatherImgWidgetIV,pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}


