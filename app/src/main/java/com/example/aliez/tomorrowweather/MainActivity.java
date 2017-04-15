package com.example.aliez.tomorrowweather;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.SearchView;

import com.example.aliez.tomorrowweather.db.*;
import com.example.aliez.tomorrowweather.util.*;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.*;

import okhttp3.*;

import static android.widget.SearchView.*;
import static com.baidu.location.h.j.v;

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
    class ThreadChild extends Thread
    {
        @Override
        public void run() {
            InitDatabase();
            closeProgressDialog();
        }
    }

    View.OnClickListener hotButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            List<County> cnlist = DataSupport.findAll(County.class);
            //Log.d("cnlist.size",String.valueOf(cnlist.size()));
            for (County cn : cnlist) {
                //Log.d("countyName",cn.getCountyName());
                if (cn.getCountyName().equals(((Button)v).getText())) {
                    intent = new Intent(MainActivity.this, WeatherActivity.class);
                    intent.putExtra("flag",2);
                    intent.putExtra("weather_id", cn.getWeatherId());
                    startActivity(intent);
                }
            }
        }
    };
    View.OnClickListener backwardListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            intent = new Intent(MainActivity.this, WeatherActivity.class);
            intent.putExtra("flag",1);
            intent.putExtra("weather_id","");
            startActivity(intent);
        }
    };
    private TextView cancel;
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
                            intent.putExtra("flag",2);
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
        hotCitybuttonlist.add((Button) findViewById(R.id.id_beijing));
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showProgressDailog();
        Thread thread = new ThreadChild();
        thread.start();
        listView = (ListView)findViewById(R.id.id_search_result);
        searchNoResult = (LinearLayout)findViewById(R.id.id_search_no_result);
        tableLayout = (TableLayout)findViewById(R.id.bottom_part);
        searchView = (SearchView)findViewById(R.id.id_search_view);
        searchView.setOnQueryTextListener(searchViewRealListener);
        backward = (Button)findViewById(R.id.backward);
        InitHotCityButtonList();
        for(final Button b:hotCitybuttonlist)b.setOnClickListener(hotButtonListener);
        backward.setOnClickListener(backwardListener);
        cancel = (TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(cancelListener);
        cancel.setVisibility(View.INVISIBLE);
        //searchView.setOnClickListener(searchListener);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void InitDatabase()
    {
        String address = "http://guolin.tech/api/china";
        List<Province> lp = DataSupport.findAll(Province.class);
        if(lp.size() == 0){
            queryFromServer(address,"province",null,null);
            lp = DataSupport.findAll((Province.class));
        }
        //Log.d("lp.size",String.valueOf(lp.size()));
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
                    hashMap.put(county.getCountyName(),temp);
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
                e.printStackTrace();
                //Toast.makeText(MainActivity.this, "cannot get data from serve", Toast.LENGTH_SHORT).show();
                //Log.d("get data from","fail");
                intent = new Intent(MainActivity.this, WeatherActivity.class);
                intent.putExtra("flag",0);
                intent.putExtra("weather_id","");
                startActivity(intent);
                finish();
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
