package com.example.aliez.tomorrowweather.gson;

/**
 * Created by Aliez on 2017/4/4.
 */

public class AQI {
    public AQICity city;
    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
