package com.example.aliez.tomorrowweather;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    private Intent intent;
    public void actionStart(Context context)
    {
        Intent intent = new Intent(context, MainActivity.class);
        startActivityForResult(intent,1);
    }

    private ProgressDialog progressDialog;
    private List<Button> hotCitybuttonlist = new ArrayList<Button>();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showProgressDailog();
        InitDatabase();
        closeProgressDialog();
        listView = (ListView)findViewById(R.id.id_search_result);
        Button button0 = (Button) findViewById(R.id.id_beijing);
        Button button1 = (Button) findViewById(R.id.id_shanghai);
        Button button2 = (Button) findViewById(R.id.id_guangzhou);
        Button button3 = (Button)findViewById(R.id.id_shenzhen);
        Button button4 = (Button)findViewById(R.id.id_tianjin);
        Button button5 = (Button)findViewById(R.id.id_chongqin);
        Button button6 = (Button)findViewById(R.id.id_chengdu);
        Button button7 = (Button)findViewById(R.id.id_wuhan);
        Button button8 = (Button)findViewById(R.id.id_changsha);
        Button button9 = (Button)findViewById(R.id.id_nanjing);
        Button button10 = (Button)findViewById(R.id.id_hangzhou);
        Button button11 = (Button)findViewById(R.id.id_xian);
        Button button12 = (Button)findViewById(R.id.id_hongkong);
        Button button13 = (Button)findViewById(R.id.id_macau);
        Button button14 = (Button)findViewById(R.id.id_taibei);
        hotCitybuttonlist.add(button0);
        hotCitybuttonlist.add(button1);
        hotCitybuttonlist.add(button2);
        hotCitybuttonlist.add(button3);
        hotCitybuttonlist.add(button4);
        hotCitybuttonlist.add(button5);
        hotCitybuttonlist.add(button6);
        hotCitybuttonlist.add(button7);
        hotCitybuttonlist.add(button8);
        hotCitybuttonlist.add(button9);
        hotCitybuttonlist.add(button10);
        hotCitybuttonlist.add(button11);
        hotCitybuttonlist.add(button12);
        hotCitybuttonlist.add(button13);
        hotCitybuttonlist.add(button14);
        for(final Button b:hotCitybuttonlist)b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Province> plist = DataSupport.findAll(Province.class);
                for(Province p : plist)
                {
                    //Log.d("province",p.getProvinceName());
                    List<City> clist = DataSupport.where("provinceId = ?", String.valueOf(p.getId())).find(City.class);
                    for(City c: clist)
                    {
                        //Log.d("city", c.getCityName());
                        List<County> cnlist = DataSupport.where("cityId = ?",String.valueOf(c.getId())).find(County.class);
                        for(County cn : cnlist)
                        {

                            if(cn.getCountyName().equals(b.getText()))
                            {
                                intent = new Intent(MainActivity.this, WeatherActivity.class);
                                intent.putExtra("weather_id",cn.getWeatherId());
                                //Log.d("weather_id", cn.getWeatherId());
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }
            }
        });

        Button backward = (Button)findViewById(R.id.backward);
        backward.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, WeatherActivity.class);
                intent.putExtra("weather_id","");
                startActivity(intent);
                finish();
            }
        });
        final TextView cancel = (TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SearchView searchView = (SearchView)findViewById(R.id.id_search_view);
                searchView.setQuery("",false);
                TableLayout tableLayout = (TableLayout)findViewById(R.id.bottom_part);
                tableLayout.setVisibility(View.VISIBLE);
                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.id_search_no_result);
                linearLayout.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);
            }
        });

        cancel.setVisibility(View.INVISIBLE);
        SearchView sv = (SearchView)findViewById(R.id.id_search_view);
        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchView seaView = (SearchView) v;
                LinearLayout snr = (LinearLayout)findViewById(R.id.id_search_no_result);
                TableLayout tl = (TableLayout) findViewById(R.id.bottom_part);
                String input = seaView.getQuery().toString().trim();
                if (input.equals(""))
                {
                    Toast.makeText(MainActivity.this, "please input correct name", Toast.LENGTH_SHORT).show();
                }else
                {
                    tl.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, input, Toast.LENGTH_SHORT).show();
                    List<Province> plist = DataSupport.findAll(Province.class);
                    searchresultArray = new ArrayList<SearchResult>();
                    for(Province p : plist)
                    {
                        List<City> citylist = DataSupport.where("provinceId = ?", String.valueOf(p.getId())).find(City.class);
                        for(City c : citylist)
                        {
                            List<County> countylist = DataSupport.where("cityId = ?", String.valueOf(c.getId())).find(County.class);
                            for(County county : countylist)
                            {
                                if(county.getCountyName().contains(input))
                                {
                                    SearchResult searchresultclass = new SearchResult();
                                    searchresultclass.setCity(c);
                                    searchresultclass.setCounty(county);
                                    searchresultclass.setProvince(p);
                                    //Log.d("province",p.getProvinceName());
                                    //Log.d("city",c.getCityName());
                                    //Log.d("county",county.getCountyName());
                                    searchresultArray.add(searchresultclass);
                                }
                            }
                        }
                    }
                    cancel.setVisibility(View.VISIBLE);
                    if(searchresultArray.size()>0)
                    {
                        SearchResultAdapter searchresultadapter = new SearchResultAdapter(MainActivity.this,R.layout.search_result_item,searchresultArray);
                        listView.setAdapter(searchresultadapter);
                        listView.setVisibility(View.VISIBLE);
                        tl.setVisibility(View.INVISIBLE);
                        snr.setVisibility(View.INVISIBLE);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                SearchResult seR = searchresultArray.get(position);
                                intent = new Intent(MainActivity.this, WeatherActivity.class);
                                intent.putExtra("weather_id", seR.getCounty().getWeatherId());
                                startActivity(intent);
                                finish();
                            }
                        });

                    }else {
                        snr.setVisibility(View.VISIBLE);
                        tl.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

    }

    private List<SearchResult> searchresultArray;
    public void InitDatabase()
    {
        //DataSupport.deleteAll(Province.class);
        //DataSupport.deleteAll(City.class);
        //DataSupport.deleteAll(County.class);
        //LitePal.getDatabase();
        //Log.d("tag", "somethign");
        String address = "http://guolin.tech/api/china";
        List<Province> lp = DataSupport.findAll(Province.class);
        if(lp.size() == 0) {
           // Log.d("lp.size",String.valueOf(lp.size()));
            queryFromServer(address,"province",null,null);
            lp = DataSupport.findAll((Province.class));
        }

        for(Province p : lp) {
            List<City> lc = DataSupport.where("provinceId = ?", String.valueOf(p.getId())).find(City.class);
            if(lc.size() == 0)
            {
                queryFromServer(address+"/"+p.getProvinceCode(),"city",p,null);
                lc = DataSupport.where("provinceId = ?" ,String.valueOf(p.getId())).find(City.class);
            }

            for(City c:lc)
            {
                List<County> lcn = DataSupport.where("cityId = ?",String.valueOf(c.getId())).find(County.class);
                if(lcn.size() == 0)
                {
                    queryFromServer(address+"/"+p.getProvinceCode()+"/"+c.getCityCode(),"county",p,c);
                    lcn = DataSupport.where("cityId = ?",String.valueOf(c.getId())).find(County.class);
                }
            }
        }
    }

    private void queryFromServer(String address ,final String type, final Province p, final City c)
    {
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(progressDialog.isShowing()) {closeProgressDialog();}
                Toast.makeText(MainActivity.this, "cannot get data from server, please check your network connection", Toast.LENGTH_SHORT).show();
                //intent = new Intent();
                //intent.putExtra("wather_id","");
                //startActivity(intent);
                //finish();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if(type.equals("province")){
                    Utility.handleProvinceResponse(responseText);
                }else if(type.equals("city")) {
                    Utility.handleCityResponse(responseText,p.getId());
                }else if(type.equals("county")) {
                    Utility.handleCountyResponse(responseText,c.getId());
                }
            }
        });
    }
}
