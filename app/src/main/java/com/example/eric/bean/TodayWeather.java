package com.example.eric.bean;

import com.example.eric.myweather.R;
import com.example.eric.util.PinYinUtil;

import java.lang.reflect.Field;
import java.util.Random;

/**
 * Created by Eric on 2015/3/23.
 */
public class TodayWeather {
    private String yest_date;
    private String yest_high;
    private String yest_low;
    private String yest_type;
    private String yest_fengli;

    private String city;
    private String updatetime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private String fengxiang;
    private String[] fengli = new String[5];
    private String[] date = new String[5];
    private String[] high = new String[5];
    private String[] low = new String[5];
    private String[] type = new String[5];

    public TodayWeather() {
        Random random = new Random();

        int pm = random.nextInt(50);
        this.pm25 = ""+pm;
        this.quality="优";
        for(int i=0;i<type.length;i++)
            type[i] = "晴";
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public String getFengli(int pos) {
        return fengli[pos];
    }

    public void setFengli(String fengli,int pos) {
        this.fengli[pos] = fengli;
    }

    public String getDate(int pos) {
        return date[pos];
    }

    public void setDate(String date, int pos) {
        this.date[pos] = date;
    }

    public String getHigh(int pos) {
        return high[pos];
    }

    public void setHigh(String high,int pos) {
        this.high[pos] = high;
    }

    public String getType(int pos) {
        return type[pos];
    }

    public void setType(String type,int pos) {
        this.type[pos] = type;
    }

    public String getLow(int pos) {
        return low[pos];
    }

    public void setLow(String low,int pos) {
        this.low[pos] = low;
    }
    public String toString() {
        return "TodayWeather{" + '\'' +
                "city='" + city + '\'' +
                ", updatetime='" + updatetime +'\'' +
                ", wendu='" + wendu + '\'' +
                ", shidu='" + shidu + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", quality='" + quality + '\'' +
                ", fengxiang='" + fengxiang + '\'' +
                ", fengli='" + fengli[0] + '\'' +
                ", date='" + date[0] + '\'' +
                ", high='" + high[0] + '\'' +
                ", low='" + low[0] + '\'' +
                ", type='" + type[0] + '\'' +
                '}';
    }

    public String getYest_date() {
        return yest_date;
    }

    public void setYest_date(String yest_date) {
        this.yest_date = yest_date;
    }

    public String getYest_high() {
        return yest_high;
    }

    public void setYest_high(String yest_high) {
        this.yest_high = yest_high;
    }

    public String getYest_low() {
        return yest_low;
    }

    public void setYest_low(String yest_low) {
        this.yest_low = yest_low;
    }

    public String getYest_type() {
        return yest_type;
    }

    public void setYest_type(String yest_type) {
        this.yest_type = yest_type;
    }

    public String getYest_fengli() {
        return yest_fengli;
    }

    public void setYest_fengli(String yest_fengli) {
        this.yest_fengli = yest_fengli;
    }

    public int getTypeId(String type) {
        String typeImg = "biz_plugin_weather_" + PinYinUtil.converterToSpell(type);
        Class aClass = R.drawable.class;
        int typeId = -1;
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

        return typeId;
    }
}
