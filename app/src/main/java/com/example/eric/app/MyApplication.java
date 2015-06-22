package com.example.eric.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.example.eric.bean.City;
import com.example.eric.db.CityDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Eric on 2015/3/27.
 */
public class MyApplication extends Application{
    private static final String TAG = "MyAPP";

    private static MyApplication mApplication;
    List<City> mCityList;
    private CityDB mCityDB;

    @Override
    public void onCreate() {
        super.onCreate();

        mApplication = this;
        Log.d(TAG,"MyApplication->onCreate()");

        mCityDB = openCityDB();
        initCityList();

    }

    public static MyApplication getInstance() {
        return mApplication;
    }

    private CityDB openCityDB() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases"
                + File.separator
                + CityDB.CITY_DB_NAME;

        File db = new File(path);
        Log.d(TAG, path);
        if(!db.exists()) {
            Log.d(TAG, "db is not exists");
            try {
                InputStream is = getAssets().open("city.db");
                db.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            }catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }

    private void initCityList() {
        mCityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    private boolean prepareCityList() {
        mCityList = mCityDB.getAllCity();
//        for (City city : mCityList) {
//            String cityName = city.getCity();
//            Log.d(TAG, cityName);
//        }
        return true;
    }
    public ArrayList<String> showCityList(){
        ArrayList<String> data;
        data = new ArrayList<String>();
        for(City city : mCityList) {
            String cityName = city.getProvince() + "-" + city.getCity();
            data.add(cityName);
        }
        return data;
    }

    public List<City> getAllCityList(){
        List<City> mAllCityList = new ArrayList<City>();
        mAllCityList = mCityDB.getAllCity();
        return mAllCityList;
    }

    public List<City> showCurCityList(String key){
        List<City> mCurCityList = new ArrayList<City>();
        mCurCityList = mCityDB.getCurCity(key);
        return mCurCityList;
    }

    public City getCityByString(String key) {
        List<City> mCurCityList = new ArrayList<City>();
        mCurCityList = mCityDB.getCurCity(key);
        City city = null;
        if(mCurCityList.size()>0)
             city = mCurCityList.get(0);
        return city;
    }
}
