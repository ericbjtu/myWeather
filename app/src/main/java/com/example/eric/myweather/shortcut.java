package com.example.eric.myweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;


/**
 * Implementation of App Widget functionality.
 */
public class shortcut extends AppWidgetProvider {
    private static AppWidgetManager widgetManager;
    private static int id;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String wendu=intent.getStringExtra("wendu");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.shortcut);
        if(wendu!=null)
            views.setTextViewText(R.id.appwidget_text, wendu+"\u2103");
        if(widgetManager!=null)
            widgetManager.updateAppWidget(id,views);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        CharSequence widgetText = context.getString(R.string.appwidget_text);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.shortcut);
//        views.setTextViewText(R.id.appwidget_text, widgetText);

        Intent intent=new Intent();
        intent.setClass(context,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0);
        views.setOnClickPendingIntent(R.id.shortcut,pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
        widgetManager=appWidgetManager;
        id=appWidgetId;
    }
}


