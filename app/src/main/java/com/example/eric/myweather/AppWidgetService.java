package com.example.eric.myweather;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.example.eric.util.NetUtil;
import com.example.eric.util.PinYinUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.zip.GZIPInputStream;

public class AppWidgetService extends Service {

    // 更新 widget 的广播对应的action
    private final String ACTION_UPDATE_WEATHER = "pku.eric.mywidget.UPDATE_WEATHER";
    // 周期性更新 widget 的周期
    private static final int UPDATE_TIME = 3000;
    // 周期性更新 widget 的线程
    private UpdateThread mUpdateThread;
    private Context mContext;
    // 更新周期的计数
    private int count = 0;

    private String wendu,cityName,weatherType;
    private int typeId = -1;
    private boolean result;

    @Override
    public void onCreate() {
        // 创建并开启线程UpdateThread
        mUpdateThread = new UpdateThread();
        mUpdateThread.start();

        result = false;

        mContext = this.getApplicationContext();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class UpdateThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                count = 0;
                while (true) {
                    Log.d("Widget", "run ... count:" + count);
                    count++;

                    updateWeatherInfo(AppWidgetService.this);

                    if(result == true) {
                        result = false;
                        Intent updateIntent = new Intent(ACTION_UPDATE_WEATHER);
                        updateIntent.putExtra("Temp", wendu + " ℃");
                        updateIntent.putExtra("City",cityName);
                        updateIntent.putExtra("Type",weatherType);
                        updateIntent.putExtra("TypeId",typeId);
                        mContext.sendBroadcast(updateIntent);
                    }

                    Thread.sleep(UPDATE_TIME);
                }
            } catch (InterruptedException e) {
                // 将 InterruptedException 定义在while循环之外，意味着抛出 InterruptedException 异常时，终止线程。
                e.printStackTrace();
            }
        }
    }

    private void updateWeatherInfo(Context context){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String cityName = sharedPreferences.getString("main_city_name","北京");//缺省值为北京
        String cityCode= sharedPreferences.getString("main_city_code","101010100");

        if(NetUtil.getNetworkState(context) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            //xml
            final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpGet httpget = new HttpGet(address);
                        HttpResponse httpResponse = httpclient.execute(httpget);
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            HttpEntity entity = httpResponse.getEntity();

                            InputStream responseStream = entity.getContent();
                            responseStream = new GZIPInputStream(responseStream);

                            BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
                            StringBuilder response = new StringBuilder();
                            String str;
                            while ((str = reader.readLine()) != null) {
                                response.append(str);
                            }
                            String responseStr = response.toString();
                            Log.d("myWeather", responseStr);

                            parseXML(responseStr);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    private void parseXML(String xmlData) {
        int typeCount=0;
        try{
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            Log.d("XML","parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    //判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("city")) {
                            eventType = xmlPullParser.next();
                            cityName = xmlPullParser.getText();
                            result = true;
                        }else if(xmlPullParser.getName().equals("error")){
                            return;
                        }else if(xmlPullParser.getName().equals("wendu")) {
                            eventType = xmlPullParser.next();
                            wendu = xmlPullParser.getText();
                        }else if(xmlPullParser.getName().equals("type")) {
                            eventType = xmlPullParser.next();
                            if(typeCount==0) {
                                weatherType = xmlPullParser.getText();
                                //update the weather picture
                                String typeImg = "biz_plugin_weather_" + PinYinUtil.converterToSpell(weatherType);
                                Class aClass = R.drawable.class;
                                try {
                                    Field field = aClass.getField(typeImg);
                                    Object value = field.get(Integer.valueOf(0));
                                    typeId = (int) value;
                                }catch (NoSuchFieldException e) {
                                    if(-1 == typeId)
                                        e.getMessage();
                                    typeId = R.drawable.biz_plugin_weather_qing;
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                            typeCount++;
                        }
                        break;

                    //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                eventType = xmlPullParser.next();
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
    }

}
