package com.timen4.ronnny.sunshine.bean;

import android.content.Context;
import android.util.Log;

import com.timen4.ronnny.sunshine.R;

/**
 * Created by luore on 2016/6/26.
 */
public class WeatherData {

    /**
     * code_day : 4
     * code_night : 13
     * date : 2016-06-26
     * high : 26
     * low : 21
     * precip :
     * text_day : 多云
     * text_night : 小雨
     * wind_direction : 东南
     * wind_direction_degree : 135
     * wind_scale :
     * wind_speed :
     */

    private String code_day;
    private String code_night;
    private String date;
    private Double high;
    private Double low;
    private String precip;
    private String text_day;
    private String text_night;
    private String wind_direction;
    private String wind_direction_degree;
    private String wind_scale;
    private String wind_speed;

    public String getCode_day() {
        return code_day;
    }

    public void setCode_day(String code_day) {
        this.code_day = code_day;
    }

    public String getCode_night() {
        return code_night;
    }

    public void setCode_night(String code_night) {
        this.code_night = code_night;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public String getPrecip() {
        return precip;
    }

    public void setPrecip(String precip) {
        this.precip = precip;
    }

    public String getText_day() {
        return text_day;
    }

    public void setText_day(String text_day) {
        this.text_day = text_day;
    }

    public String getText_night() {
        return text_night;
    }

    public void setText_night(String text_night) {
        this.text_night = text_night;
    }

    public String getWind_direction() {
        return wind_direction;
    }

    public void setWind_direction(String wind_direction) {
        this.wind_direction = wind_direction;
    }

    public String getWind_direction_degree() {
        return wind_direction_degree;
    }

    public void setWind_direction_degree(String wind_direction_degree) {
        this.wind_direction_degree = wind_direction_degree;
    }

    public String getWind_scale() {
        return wind_scale;
    }

    public void setWind_scale(String wind_scale) {
        this.wind_scale = wind_scale;
    }

    public String getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(String wind_speed) {
        this.wind_speed = wind_speed;
    }

    public String toString(Context context,String unitType) {
        long mhigh = 0;
        long mlow = 0;
        if (unitType.equals(context.getString(R.string.pref_units_imperial))) {
            mhigh = Math.round((high * 1.8) + 32);
            mlow = Math.round((low * 1.8) + 32);
        } else if (unitType.equals(context.getString(R.string.pref_units_metric))) {
            mhigh=Math.round(high);
            mlow=Math.round(low);
        }
        return date+"-"+text_day+"-"+mhigh+"/"+mlow;
    }
}
