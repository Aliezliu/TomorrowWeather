package com.example.aliez.tomorrowweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aliez on 2017/4/4.
 */

public class Basic {
    @SerializedName("city")
    public String cityname;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
