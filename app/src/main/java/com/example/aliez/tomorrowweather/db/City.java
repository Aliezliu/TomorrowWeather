package com.example.aliez.tomorrowweather.db;
import org.litepal.crud.DataSupport;
/**
 * Created by Aliez on 2017/3/27.
 */

public class City extends DataSupport {
    private int id;
    private String cityName;
    private int cityCode;
    private int proviceId;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProviceId() {
        return proviceId;
    }

    public void setProviceId(int proviceId) {
        this.proviceId = proviceId;
    }
}
