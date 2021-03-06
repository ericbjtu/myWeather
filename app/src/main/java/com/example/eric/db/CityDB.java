package com.example.eric.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.eric.bean.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 2015/3/27.
 */
public class CityDB {
    public static final String CITY_DB_NAME = "city2.db";
    private static final String CITY_TABLE_NAME = "city";
    private SQLiteDatabase db;

    public CityDB(Context context, String path) {
        db = context.openOrCreateDatabase(CITY_DB_NAME, Context.MODE_PRIVATE, null);
    }

    public List<City> getAllCity() {
        List<City> list = new ArrayList<City>();
        Cursor c = db.rawQuery("SELECT * from " + CITY_TABLE_NAME, null);
        while(c.moveToNext()) {
            String province = c.getString(c.getColumnIndex("province"));
            String city = c.getString(c.getColumnIndex("city"));
            String number = c.getString(c.getColumnIndex("number"));
            String allPY = c.getString(c.getColumnIndex("allpy"));
            String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY = c.getString(c.getColumnIndex("firstpy"));
            City item = new City(province,city,number, firstPY, allPY, allFirstPY);
            list.add(item);
        }
        c.close(); //关闭游标，释放资源
        return list;
    }
    public List<City> getCurCity(String key){
        Cursor c = db.rawQuery("SELECT * from " + CITY_TABLE_NAME + " WHERE city like '%"+key+"%'"+"or allpy like '%"+key+"%'", null);
        List<City> list = new ArrayList<City>();

        while (c.moveToNext()) {
            String province = c.getString(c.getColumnIndex("province"));
            String city = c.getString(c.getColumnIndex("city"));
            String number = c.getString(c.getColumnIndex("number"));
            String allPY = c.getString(c.getColumnIndex("allpy"));
            String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY = c.getString(c.getColumnIndex("firstpy"));
            City item = new City(province, city, number, firstPY, allPY, allFirstPY);
            list.add(item);
        }
        c.close();//关闭游标，释放资源
        return list;
    }
}
