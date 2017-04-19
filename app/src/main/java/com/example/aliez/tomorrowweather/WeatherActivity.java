package com.example.aliez.tomorrowweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aliez.tomorrowweather.gson.Forecast;
import com.example.aliez.tomorrowweather.gson.Weather;
import com.example.aliez.tomorrowweather.service.AutoUpdateService;
import com.example.aliez.tomorrowweather.util.HttpUtil;
import com.example.aliez.tomorrowweather.util.Utility;
import com.qiushui.blurredview.BlurredView;
import com.yalantis.phoenix.PullToRefreshView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WeatherActivity extends AppCompatActivity implements ScrollViewListener {
    private ObservableScrollView weatherLayout;
    private TextView titleCity;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    //private ImageView bingPicImg;
    private Toolbar mToolBar;
    public DrawerLayout drawerLayout;
    //public SwipeRefreshLayout swipeRefresh;
    public PullToRefreshView mPullToRefreshView;
    private BlurredView mBlurredView;
    private int mScrollerY;

    private String mWeatherId;
    private int mAlpha;
    private Button add_button;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mBlurredView = (BlurredView) findViewById(R.id.yahooweather_blurredview);
        //bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ObservableScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);

        mToolBar = (Toolbar) findViewById(R.id.main_toolbar);
        add_button = (Button) findViewById(R.id.add_button);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        weatherLayout.setScrollViewListener(this);
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
//        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
//        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);


        /*String weatherString = prefs.getString("weather", null);
        if(weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {*/
        mWeatherId = getIntent().getStringExtra("weather_id");
        if (mWeatherId.equals("")) {
            //Log.d("mWetherId", mWeatherId);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            mWeatherId = prefs.getString("weather_id", null);
        }
        weatherLayout.setVisibility(View.INVISIBLE);
        requestWeather(mWeatherId);

        /*swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });*/
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WeatherActivity.this, MainActivity.class));
            }
        });

        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 300);
            }
        });
//        String bingPic = prefs.getString("bing_pic", null);
//        if(bingPic != null) {
//            Glide.with(this).load(bingPic).into(bingPicImg);
//        } else {
//            loadBingPic();
//        }
    }

    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                weatherId + "&key=e7934780c26e463a846f57a84e6a89ee";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败",Toast.LENGTH_SHORT).show();
                        mPullToRefreshView.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            //editor.putString("weather", responseText);
                            editor.putString("weather_id", weatherId);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        mPullToRefreshView.setRefreshing(false);
                    }
                });
            }
        });
        //loadBingPic();
    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                final String bingPic = response.body().string();
//                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
//                editor.putString("bing_Pic", bingPic);
//                editor.apply();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
//                    }
//                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityname;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "°";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        
        if (weather != null && "ok".equals(weather.status)) {
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        } else {
            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
        }
        
        for(Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运行建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        WindowManager wm = (WindowManager) WeatherActivity.this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
// 获取到ImageView的高度
        int height = point.y;
        ViewGroup.LayoutParams params = mBlurredView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
// 将ImageView的高度增加100
        params.height = height + 100;

// 应用更改设置
        mBlurredView.requestLayout();
        mScrollerY += y - oldy;
        Log.d("mScrollerY", String.valueOf(mScrollerY));
        if (mScrollerY > 1000) {
            mBlurredView.setBlurredTop(100);
            mAlpha = 100;
        } else if (mScrollerY >= 0 && mScrollerY < 1000){
            mBlurredView.setBlurredTop(mScrollerY / 10);
            mAlpha = Math.abs(mScrollerY) / 10;
        } else {
            mBlurredView.setBlurredTop(0);
            mAlpha = 0;
        }
        mBlurredView.setBlurredLevel(mAlpha);
    }
}
