package com.example.aliez.tomorrowweather.db;

import DataSupport;

/**
 * Created by Aliez on 2017/3/27.
 */

public class Country extends DataSupport {
    private int id;
    private String countryName;
    private String weatherId;
    private int cityId;
    public int getId() {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public  void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId (int cityId) {
        this.cityId = cityId;
    }
}
