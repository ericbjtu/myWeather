package com.example.eric.myweather;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.eric.app.MyApplication;
import com.example.eric.bean.City;
import com.example.eric.bean.TodayWeather;
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
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import cn.jpush.android.api.JPushInterface;


public class MainActivity extends ActionBarActivity implements View.OnClickListener, ViewPager.OnPageChangeListener{

    private ImageView mUpdatebtn, mOpenDrawerbtn,mLocationbtn;
    private TextView mChooseCitybtn;
    private String currentCityCode;

    private ProgressBar mUpdateProBar;
    private TodayWeather todayWeather;

    // for LBS
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private LocationClientOption.LocationMode tempMode = LocationClientOption.LocationMode.Hight_Accuracy;
    private String tempcoor="gcj02";

    private MyApplication m_myApplication;

    private static final int UPDATE_TODAY_WEATHER = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private TextView titleCityNameTv, timeTv, humidityTv,  pmDataTv, pmQualityTv, temperatureHighTv,temperatureLowTv,
    climateTv, windTv;
    private ImageView weatherImg;

    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;
    private ImageView[] dots;
    private int[] ids = {R.id.iv1, R.id.iv2};
    // future weather
    private TextView dateTv0,degreeTv0,typeTv0,windTv0;
    private ImageView weatherImg0;

    private TextView dateTv1,degreeTv1,typeTv1,windTv1;
    private ImageView weatherImg1;

    private TextView dateTv2,degreeTv2,typeTv2,windTv2;
    private ImageView weatherImg2;

    private TextView dateTv3,degreeTv3,typeTv3,windTv3;
    private ImageView weatherImg3;

    private TextView dateTv4,degreeTv4,typeTv4,windTv4;
    private ImageView weatherImg4,weatherImg5;

    private DrawerLayout mDrawerLayout = null;
    private ListView mDrawerListView = null;
    private List<String> drawerCityName;
    private List<String> drawerCityNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        Log.d("MyAPP","MainActivity->onCreate()");
        m_myApplication = MyApplication.getInstance();

        currentCityCode = null;

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        mLocationClient = new LocationClient(getApplicationContext()); //声明LocationClient类
        mLocationClient.registerLocationListener(myListener); //注册监听函数

        mUpdatebtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdatebtn.setOnClickListener(this);

        mChooseCitybtn = (TextView) findViewById(R.id.drawer_add_city);
        mChooseCitybtn.setOnClickListener(this);

        mOpenDrawerbtn = (ImageView) findViewById(R.id.title_city_manager);
        mOpenDrawerbtn.setOnClickListener(this);

        mUpdateProBar = (ProgressBar) findViewById(R.id.title_update_progressBar);
        mUpdateProBar.setVisibility(View.INVISIBLE);

        mLocationbtn = (ImageView) findViewById(R.id.title_location);
        mLocationbtn.setOnClickListener(this);

        titleCityNameTv = (TextView) findViewById(R.id.title_city_name);

        initView();
        initDots();
        initComponents();

        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String cityName = sharedPreferences.getString("main_city_name","北京");//缺省值为北京
        String cityCode= sharedPreferences.getString("main_city_code","101010100");
        titleCityNameTv.setText(cityName + "天气");
        queryWeatherCode(cityCode);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        });
    }

    private void initComponents() {
        dateTv0 =(TextView) views.get(0).findViewById(R.id.week_day0);
        degreeTv0 = (TextView) views.get(0).findViewById(R.id.degree_day0);
        typeTv0 = (TextView) views.get(0).findViewById(R.id.climate_day0);
        windTv0 = (TextView) views.get(0).findViewById(R.id.wind_day0);
        weatherImg0 = (ImageView) views.get(0).findViewById(R.id.weatherPic_day0);

        dateTv1 =(TextView) views.get(0).findViewById(R.id.week_day1);
        degreeTv1 = (TextView) views.get(0).findViewById(R.id.degree_day1);
        typeTv1 = (TextView) views.get(0).findViewById(R.id.climate_day1);
        windTv1 = (TextView) views.get(0).findViewById(R.id.wind_day1);
        weatherImg1 = (ImageView) views.get(0).findViewById(R.id.weatherPic_day1);

        dateTv2 =(TextView) views.get(0).findViewById(R.id.week_day2);
        degreeTv2 = (TextView) views.get(0).findViewById(R.id.degree_day2);
        typeTv2 = (TextView) views.get(0).findViewById(R.id.climate_day2);
        windTv2 = (TextView) views.get(0).findViewById(R.id.wind_day2);
        weatherImg2 = (ImageView) views.get(0).findViewById(R.id.weatherPic_day2);

        dateTv3 =(TextView) views.get(1).findViewById(R.id.week_day3);
        degreeTv3 = (TextView) views.get(1).findViewById(R.id.degree_day3);
        typeTv3 = (TextView) views.get(1).findViewById(R.id.climate_day3);
        windTv3 = (TextView) views.get(1).findViewById(R.id.wind_day3);
        weatherImg3 = (ImageView) views.get(1).findViewById(R.id.weatherPic_day3);

        dateTv4 =(TextView) views.get(1).findViewById(R.id.week_day4);
        degreeTv4 = (TextView) views.get(1).findViewById(R.id.degree_day4);
        typeTv4 = (TextView) views.get(1).findViewById(R.id.climate_day4);
        windTv4 = (TextView) views.get(1).findViewById(R.id.wind_day4);
        weatherImg4 = (ImageView) views.get(1).findViewById(R.id.weatherPic_day4);

        weatherImg5 = (ImageView) views.get(1).findViewById(R.id.weatherPic_day5);
        weatherImg5.setVisibility(View.INVISIBLE);

        drawerCityName = new ArrayList<String>();
        drawerCityNum = new ArrayList<String>();
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        int drawerSize = sharedPreferences.getInt("DrawerSize",0);//缺省值0
        for(int i=0;i<drawerSize;i++){
            String cityCode= sharedPreferences.getString("DrawerCode"+i,"101010100");
            String cityName= sharedPreferences.getString("DrawerName"+i,"北京");
            drawerCityNum.add(cityCode);
            drawerCityName.add(cityName);
        }
        mDrawerListView = (ListView) findViewById(R.id.drawer_list_view);
        updateDrawerList();
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cityNum = drawerCityNum.get(position);
                queryWeatherCode(cityNum);
                mDrawerLayout.closeDrawers();
            }
        });
        mDrawerListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("确认移除该城市吗？");
                builder.setTitle("提示：");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //删除城市
                        drawerCityNum.remove(pos);
                        drawerCityName.remove(pos);

                        updateSharedPreferenceDrawerList();
                        updateDrawerList();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    private void updateSharedPreferenceDrawerList(){
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int drawerSize = drawerCityName.size();
        editor.putInt("DrawerSize",drawerSize);
        for(int i=0;i<drawerSize;i++){
            editor.putString("DrawerCode"+i, drawerCityNum.get(i));
            editor.putString("DrawerName"+i, drawerCityName.get(i));
        }
        editor.commit();
    }
    private void updateDrawerList(){
        String[] data = new String[drawerCityNum.size()];
        for(int i=0,j=drawerCityNum.size();i<j;i++){
            data[i]= drawerCityName.get(i);
        }
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_expandable_list_item_1,data);
        mDrawerListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.title_update_btn) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");//缺省值为北京
            Log.d("myWeather", cityCode);
            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                mUpdateProBar.setVisibility(View.VISIBLE);
                mUpdatebtn.setVisibility(View.INVISIBLE);
                if(currentCityCode != null)
                    cityCode = currentCityCode;
                queryWeatherCode(cityCode);
            }else {
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了！", Toast.LENGTH_LONG).show();
            }
        }else if(view.getId() == R.id.drawer_add_city) {
            Intent intent = new Intent("android.intent.action.ChooseCity");
            startActivityForResult(intent,1);
        }
        else if (view.getId() == R.id.title_city_manager){
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }else if(view.getId() == R.id.title_location){
            InitLocation();
            mLocationClient.start();
            //openGPSSettings();
        }
    }

    private void queryWeatherCode(String cityCode) {
            currentCityCode = cityCode;
        //xml
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet(address);
                    HttpResponse httpResponse = httpclient.execute(httpget);
                    if(httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();

                        InputStream responseStream = entity.getContent();
                        responseStream = new GZIPInputStream(responseStream);

                        BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
                        StringBuilder response = new StringBuilder();
                        String str;
                        while((str=reader.readLine()) != null){
                            response.append(str);
                        }
                        String responseStr = response.toString();
                        Log.d("myWeather", responseStr);

                        todayWeather = parseXML(responseStr);
                        if(todayWeather != null) {
                            Log.d("todayWeather", todayWeather.toString());
                            Message msg = new Message();
                            msg.what = UPDATE_TODAY_WEATHER;
                            msg.obj = todayWeather;
                            mHandler.sendMessage(msg);
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
//        updateBackground();
    }

    private TodayWeather parseXML(String xmlData) {
        TodayWeather todayWeather = null;
        int fengxiangCount=0,fengliCount=0,dateCount=0,typeCount=0,highCount=0,lowCount=0;
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
                        if(xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if(todayWeather != null) {
                            if(xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("error")){
                                todayWeather = null;
                                return todayWeather;
                            }else if(xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            }else if(xmlPullParser.getName().equals("fengli")) {
                                eventType = xmlPullParser.next();
                                if(fengliCount%2 == 1)
                                    todayWeather.setFengli(xmlPullParser.getText(),fengliCount/2);
                                fengliCount++;
                            }else if(xmlPullParser.getName().equals("date")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText(),dateCount);
                                dateCount++;
                            }else if(xmlPullParser.getName().equals("high")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2),highCount);
                                highCount++;
                            }else if(xmlPullParser.getName().equals("low")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2),lowCount);
                                lowCount++;
                            }else if(xmlPullParser.getName().equals("type")) {
                                eventType = xmlPullParser.next();
                                if(typeCount%2==0)
                                    todayWeather.setType(xmlPullParser.getText(),typeCount/2);
                                typeCount++;
                            }else if(xmlPullParser.getName().equals("date_1")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setYest_date(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("high_1")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setYest_high(xmlPullParser.getText().substring(2));
                            }else if(xmlPullParser.getName().equals("low_1")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setYest_low(xmlPullParser.getText().substring(2));
                            }else if(xmlPullParser.getName().equals("type_1")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setYest_type(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("fl_1")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setYest_fengli(xmlPullParser.getText());
                            }
                        }
                        break;

                    //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                eventType = xmlPullParser.next();
            }

        }catch( Exception e) {
            Toast.makeText(MainActivity.this,"没有该城市信息",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return todayWeather;
    }

    void updateTodayWeather(TodayWeather todayWeather) {
        //update yesterday info
        dateTv0.setText(todayWeather.getYest_date());
        degreeTv0.setText(todayWeather.getYest_low().trim()+"~"+todayWeather.getYest_high().trim());
        typeTv0.setText(todayWeather.getYest_type());
        windTv0.setText(todayWeather.getYest_fengli());

        int typeId0 = todayWeather.getTypeId(todayWeather.getYest_type());
        Drawable drawable = getResources().getDrawable(typeId0);
        weatherImg0.setImageDrawable(drawable);

        //update day1 info
        dateTv1.setText(todayWeather.getDate(1));
        degreeTv1.setText(todayWeather.getLow(1).trim()+"~"+todayWeather.getHigh(1).trim());
        typeTv1.setText(todayWeather.getType(1));
        windTv1.setText(todayWeather.getFengli(1));

        int typeId1 = todayWeather.getTypeId(todayWeather.getType(1));
        drawable = getResources().getDrawable(typeId1);
        weatherImg1.setImageDrawable(drawable);

        //update day2 info
        dateTv2.setText(todayWeather.getDate(2));
        degreeTv2.setText(todayWeather.getLow(2).trim()+"~"+todayWeather.getHigh(2).trim());
        typeTv2.setText(todayWeather.getType(2));
        windTv2.setText(todayWeather.getFengli(2));

        int typeId2 = todayWeather.getTypeId(todayWeather.getType(2));
        drawable = getResources().getDrawable(typeId2);
        weatherImg2.setImageDrawable(drawable);

        //update day3 info
        dateTv3.setText(todayWeather.getDate(3));
        degreeTv3.setText(todayWeather.getLow(3).trim()+"~"+todayWeather.getHigh(3).trim());
        typeTv3.setText(todayWeather.getType(3));
        windTv3.setText(todayWeather.getFengli(3));

        int typeId3 = todayWeather.getTypeId(todayWeather.getType(3));
        drawable = getResources().getDrawable(typeId3);
        weatherImg3.setImageDrawable(drawable);

        //update day4 info
        dateTv4.setText(todayWeather.getDate(4));
        degreeTv4.setText(todayWeather.getLow(4).trim()+"~"+todayWeather.getHigh(4).trim());
        typeTv4.setText(todayWeather.getType(4));
        windTv4.setText(todayWeather.getFengli(4));

        int typeId4 = todayWeather.getTypeId(todayWeather.getType(4));
        drawable = getResources().getDrawable(typeId4);
        weatherImg4.setImageDrawable(drawable);

        //update today info
        titleCityNameTv.setText(todayWeather.getCity()+"天气");
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText(todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        String highTemp = todayWeather.getHigh(0).trim();
        String lowTemp = todayWeather.getLow(0).trim();
        temperatureHighTv.setText(todayWeather.getWendu()+"℃");
        temperatureLowTv.setText(lowTemp+"~"+highTemp);
        climateTv.setText(todayWeather.getType(0));
        windTv.setText(todayWeather.getFengli(0));


        //update the weather picture

//        String typeImg = "biz_plugin_weather_" + PinYinUtil.converterToSpell(todayWeather.getType(0));
//        Class aClass = R.drawable.class;
        int typeId = -1;
        typeId = todayWeather.getTypeId(todayWeather.getType(0));
//        try {
//            Field field = aClass.getField(typeImg);
//            Object value = field.get(Integer.valueOf(0));
//            typeId = (int) value;
//        }catch (NoSuchFieldException e) {
//            if(-1 == typeId)
//                e.getMessage();
//                typeId = R.drawable.biz_plugin_weather_qing;
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

        drawable = getResources().getDrawable(typeId);
        weatherImg.setImageDrawable(drawable);

        mUpdateProBar.setVisibility(View.INVISIBLE);
        mUpdatebtn.setVisibility(View.VISIBLE);
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        timeTv = (TextView) findViewById(R.id.content_time);
        humidityTv = (TextView) findViewById(R.id.content_humidity);
        pmDataTv = (TextView) findViewById(R.id.content_pm_value);
        pmQualityTv = (TextView) findViewById(R.id.content_pm_description);

        temperatureHighTv = (TextView) findViewById(R.id.content_degree_high);
        temperatureLowTv = (TextView) findViewById(R.id.content_degree_low);
        climateTv = (TextView) findViewById(R.id.content_climate);
        windTv = (TextView) findViewById(R.id.content_wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);

        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        temperatureHighTv.setText("N/A");
        temperatureLowTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");

        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.weather_page_1,null));
        views.add(inflater.inflate(R.layout.weather_page_2,null));
        vpAdapter = new ViewPagerAdapter(views, this);
        vp = (ViewPager) findViewById(R.id.weatherInfoViewPager);
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(this);
    }

    private void initDots() {
        dots = new ImageView[views.size()];
        for(int i=0;i<views.size();i++)
        {
            dots[i] = (ImageView) findViewById(ids[i]);
        }
    }

    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) {
            String cityNumber = data.getStringExtra("CityNumber");
            String cityName = data.getStringExtra("CityName");
            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                queryWeatherCode(cityNumber);
                mDrawerLayout.closeDrawers();   //close the drawer
                if(!drawerCityNum.contains(cityNumber)){
                    drawerCityNum.add(cityNumber);
                    drawerCityName.add(cityName);

                    updateSharedPreferenceDrawerList();
                    updateDrawerList();
                }
            }
        }
    }
    protected  void updateBackground(){
        //添加城市的北京图片
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String cityName=sharedPreferences.getString("main_city_name","北京");
        LinearLayout lly=(LinearLayout)findViewById(R.id.content);
        Resources resources = getBaseContext().getResources();
        Drawable d=resources.getDrawable(R.drawable.city_beijing);
        switch (cityName){
            case "北京":
                d=resources.getDrawable(R.drawable.city_beijing);
                break;
            case "香港":
                d=resources.getDrawable(R.drawable.city_hongkong);
                break;
            case "南京":
                d=resources.getDrawable(R.drawable.city_nanjing);
                break;
            case "上海":
                d=resources.getDrawable(R.drawable.city_shanghai);
                break;
            case "天津":
                d=resources.getDrawable(R.drawable.city_tianjing);
                break;
            case "武汉":
                d=resources.getDrawable(R.drawable.city_wuhan);
                break;
            case "深圳":
                d=resources.getDrawable(R.drawable.biz_plugin_weather_shenzhen_bg);
                break;
        }
        lly.setBackgroundDrawable(d);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        for(int i=0;i<views.size();i++)
        {
            if(i==position){
                dots[i].setImageResource(R.drawable.page_indicator_focused);
            }else{
                dots[i].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void openGPSSettings() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this,"GPS模块正常",Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this,"请开启GPS！",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        startActivityForResult(intent,0);
    }

    private void InitLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
        option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
        int span=5000;
        option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation == null)
                return;
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(bdLocation.getTime());
            sb.append("\nerror code : ");
            sb.append(bdLocation.getLocType());
//            dateTv0.setText(""+bdLocation.getLocType());
//            dateTv1.setText("" +bdLocation.getCity());
//            dateTv2.setText(""+bdLocation.getCountry());
//            typeTv0.setText(""+bdLocation.getLatitude());
//            typeTv1.setText(""+bdLocation.getLongitude());
            sb.append("\nlatitude : ");
            sb.append(bdLocation.getLatitude());
            sb.append("\nlongtitude : ");
            sb.append(bdLocation.getLongitude());
            sb.append("\nradius : ");
            sb.append(bdLocation.getRadius());
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(bdLocation.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(bdLocation.getSatelliteNumber());
            }else if(bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(bdLocation.getAddrStr());
            }
            String cityName = bdLocation.getCity();
            if(cityName!=null) {
                cityName = cityName.substring(0, cityName.length()-1);
                sb.append("\ncityName : ");
                sb.append(cityName);
                mLocationClient.stop();
                City city = m_myApplication.getCityByString(cityName);
                if(city != null) {
                    String returnCityNumber = city.getNumber();
                    String returnCityName = city.getCity();

                    //点击后切换到指定城市
                    SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("main_city_code", returnCityNumber);
                    editor.putString("main_city_name", returnCityName);
                    editor.commit();

                    queryWeatherCode(returnCityNumber);
                }
            }
            Log.d("MAP",sb.toString());
        }
    }


}
