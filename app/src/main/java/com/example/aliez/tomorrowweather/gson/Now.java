package com.example.aliez.tomorrowweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aliez on 2017/4/4.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
