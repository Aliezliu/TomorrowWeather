package com.example.aliez.tomorrowweather;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.aliez.tomorrowweather.db.City;
import com.example.aliez.tomorrowweather.db.County;
import com.example.aliez.tomorrowweather.db.Province;
import com.example.aliez.tomorrowweather.db.SearchResult;
import com.example.aliez.tomorrowweather.db.SearchResultAdapter;
import com.example.aliez.tomorrowweather.util.HttpUtil;
import com.example.aliez.tomorrowweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.widget.SearchView.OnQueryTextListener;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private Intent intent;
    private ProgressDialog progressDialog;
    private List<Button> hotCitybuttonlist = new ArrayList<Button>();
    private Map<String, SearchResult> hashMap = new HashMap<>();
    private List<SearchResult> searchresultArray;
    private LinearLayout searchNoResult;
    private SearchView searchView;
    private TableLayout tableLayout;
    private Button backward;
    private TextView cancel;
    private Button LocationButton;

    public LocationClient mLocationClient;

    class ThreadChild extends Thread
    {
        @Override
        public void run() {
            InitDatabase();
            closeProgressDialog();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_main);
        LocationButton = (Button) findViewById(R.id.id_location);

        Thread thread = new ThreadChild();
        thread.start();



        showProgressDailog();


        listView = (ListView)findViewById(R.id.id_search_result);
        searchNoResult = (LinearLayout)findViewById(R.id.id_search_no_result);
        tableLayout = (TableLayout)findViewById(R.id.bottom_part);
        searchView = (SearchView)findViewById(R.id.id_search_view);
        searchView.setOnQueryTextListener(searchViewRealListener);
        backward = (Button)findViewById(R.id.backward);
        cancel = (TextView)findViewById(R.id.cancel);

        InitHotCityButtonList();

        for(final Button b:hotCitybuttonlist) //热点城市点击事件
            b.setOnClickListener(hotButtonListener);

        backward.setOnClickListener(backwardListener);

        cancel.setOnClickListener(cancelListener);

        cancel.setVisibility(View.INVISIBLE);
        //searchView.setOnClickListener(searchListener);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }
    }


    private OnQueryTextListener searchViewRealListener = new OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            String input = newText.trim();
            if (!input.equals("")) {
                tableLayout.setVisibility(View.INVISIBLE);
                searchresultArray = new ArrayList<SearchResult>();
                Iterator iter = hashMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String countyN = (String) entry.getKey();
                    if (countyN.contains(input)) {
                        SearchResult pccnE = (SearchResult) entry.getValue();
                        searchresultArray.add(pccnE);
                    }
                }

                cancel.setVisibility(View.VISIBLE);
                if (searchresultArray.size() > 0) {
                    SearchResultAdapter searchresultadapter = new SearchResultAdapter(MainActivity.this, R.layout.search_result_item, searchresultArray);
                    listView.setAdapter(searchresultadapter);
                    listView.setVisibility(View.VISIBLE);
                    tableLayout.setVisibility(View.INVISIBLE);
                    searchNoResult.setVisibility(View.INVISIBLE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            SearchResult seR = searchresultArray.get(position);
                            intent = new Intent(MainActivity.this, WeatherActivity.class);
                            intent.putExtra("flag", 2);
                            intent.putExtra("county", seR.getCounty().getCountyName());
                            intent.putExtra("weather_id", seR.getCounty().getWeatherId());
                            startActivity(intent);
                        }
                    });
                } else {
                    searchNoResult.setVisibility(View.VISIBLE);
                    tableLayout.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                }
            }
            return false;
        }
    };

    private void InitHotCityButtonList()
    {
        hotCitybuttonlist.add(LocationButton);
        hotCitybuttonlist.add((Button) findViewById(R.id.id_shanghai));
        hotCitybuttonlist.add((Button) findViewById(R.id.id_guangzhou));
        hotCitybuttonlist.add((Button)findViewById(R.id.id_shenzhen));
        hotCitybuttonlist.add((Button)findViewById(R.id.id_tianjin));
        hotCitybuttonlist.add((Button)findViewById(R.id.id_chongqin));
        hotCitybuttonlist.add((Button)findViewById(R.id.id_chengdu));
        hotCitybuttonlist.add((Button)findViewById(R.id.id_wuhan));
        hotCitybuttonlist.add((Button)findViewById(R.id.id_changsha));
        hotCitybuttonlist.add((Button)findViewById(R.id.id_nanjing));
        hotCitybuttonlist.add((Button)findViewById(R.id.id_hangzhou));
        hotCitybuttonlist.add((Button)findViewById(R.id.id_xian));
        hotCitybuttonlist.add((Button)findViewById(R.id.id_hongkong));
        hotCitybuttonlist.add((Button)findViewById(R.id.id_macau));
        hotCitybuttonlist.add((Button)findViewById(R.id.id_taibei));
    }



    private void requestLocation() {
        Log.d("mLocationClient Started", "requestLocation: ");
        initLocation();
        mLocationClient.start();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    View.OnClickListener hotButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //List<County> cnlist = DataSupport.findAll(County.class);
            //Log.d("cnlist.size",String.valueOf(cnlist.size()));
            /*
            for (County cn : cnlist) {
                //Log.d("countyName",cn.getCountyName());
                if (cn.getCountyName().equals(((Button)v).getText())) {
                    intent = new Intent(MainActivity.this, WeatherActivity.class);
                    intent.putExtra("flag", 2);
                    intent.putExtra("county", cn.getCountyName());
                    intent.putExtra("weather_id", cn.getWeatherId());
                    startActivity(intent);
                }
            }
            */
            intent = new Intent(MainActivity.this, WeatherActivity.class);
            SearchResult hashResult = hashMap.get(((Button)v).getText());
            intent.putExtra("flag", 2);
            intent.putExtra("county",hashResult.getCounty().getCountyName());
            intent.putExtra("weather_id", hashResult.getCounty().getWeatherId());
            startActivity(intent);
        }
    };

    View.OnClickListener backwardListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            intent = new Intent(MainActivity.this, WeatherActivity.class);
            intent.putExtra("flag", 1);
            intent.putExtra("weather_id","");
            startActivity(intent);
        }
    };


    View.OnClickListener cancelListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            searchView.setQuery("",false);
            tableLayout.setVisibility(View.VISIBLE);
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.id_search_no_result);
            linearLayout.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.INVISIBLE);
            ((TextView)v).setVisibility(View.INVISIBLE);
        }
    };

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuilder mCounty = new StringBuilder(bdLocation.getDistrict());
            Log.d("mCounty:", mCounty.toString());
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LocationButton.setText(mCounty);
                }
            });
            List<County> cnlist = DataSupport.findAll(County.class);
            //Log.d("cnlist.size",String.valueOf(cnlist.size()));
            for (County cn : cnlist) {
                Log.d("countyName",cn.getCountyName());
                if (mCounty.contains(cn.getCountyName())) {
                    intent = new Intent(MainActivity.this, WeatherActivity.class);
                    intent.putExtra("flag", 2);
                    intent.putExtra("weather_id", cn.getWeatherId());
                    startActivity(intent);
                }
            }*/
            LocationButton.setText(mCounty);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    public void InitDatabase()
    {
        String address = "http://guolin.tech/api/china";
        List<Province> lp = DataSupport.findAll(Province.class);
        if (lp.size() == 0){
            queryFromServer(address,"province",null,null);
            lp = DataSupport.findAll((Province.class));
        }
        Log.d("lp.size",String.valueOf(lp.size()));
        for(Province p : lp) {
            List<City> lc = DataSupport.where("provinceId = ?", String.valueOf(p.getId())).find(City.class);
            if(lc.size() == 0) {
                queryFromServer(address+"/"+p.getProvinceCode(),"city",p,null);
                lc = DataSupport.where("provinceId = ?" ,String.valueOf(p.getId())).find(City.class);
            }
            for(City c:lc) {
                List<County> lcn = DataSupport.where("cityId = ?",String.valueOf(c.getId())).find(County.class);
                if(lcn.size() == 0) {
                    queryFromServer(address+"/"+p.getProvinceCode()+"/"+c.getCityCode(),"county",p,c);
                    lcn = DataSupport.where("cityId = ?",String.valueOf(c.getId())).find(County.class);
                }
                for(County county : lcn) {
                    SearchResult temp = new SearchResult();
                    temp.setProvince(p);
                    temp.setCity(c);
                    temp.setCounty(county);
                    hashMap.put(county.getCountyName(), temp);
                }
            }
        }
    }

    private void queryFromServer(String address ,final String type, final Province p, final City c)
    {
        showProgressDailog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(progressDialog.isShowing())
                    closeProgressDialog();
                //e.printStackTrace();
                //Toast.makeText(MainActivity.this, "cannot get data from serve", Toast.LENGTH_SHORT).show();
                //Log.d("get data from","fail");
                intent = new Intent(MainActivity.this, WeatherActivity.class);
                intent.putExtra("flag", 0);
                intent.putExtra("weather_id","");
                startActivity(intent);
                finish();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                //boolean result = false;
                if (type.equals("province")) {
                    Utility.handleProvinceResponse(responseText);
                } else if (type.equals("city")) {
                    Utility.handleCityResponse(responseText, p.getId());
                } else if (type.equals("county")) {
                    Utility.handleCountyResponse(responseText, c.getId());
                }
            }
        });
    }

    private void showProgressDailog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
