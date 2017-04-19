package com.example.aliez.tomorrowweather;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.aliez.tomorrowweather.db.County;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Aliez on 2017/4/3.
 */

public class CountyListFragment extends Fragment {
    private ListView county_listview;
    private County selectedCounty;
    private CountyAdapter adapter;
    private static List<County> countyList = new ArrayList<>();
    private  static List<String> dataList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.county_list, container, false);

        county_listview = (ListView) view.findViewById(R.id.county_listview);
        adapter = new CountyAdapter(getContext(), R.layout.county_item, dataList);
        county_listview.setAdapter(adapter);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        county_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCounty = countyList.get(position);
                String weatherId = selectedCounty.getWeatherId();
                WeatherActivity activity = (WeatherActivity)getActivity();
                activity.drawerLayout.closeDrawers();
                activity.mPullToRefreshView.setRefreshing(true);
                activity.requestWeather(weatherId);
                }
        });
        initCountyList();
    }

    private void initCountyList() {
        String countyName = getActivity().getIntent().getStringExtra("county");
        List<County> allCounties = new ArrayList<>();
        allCounties = DataSupport.findAll(County.class);
        if (countyName == null)
            return;
        for (County county: allCounties) {
            if (countyName.equals(county.getCountyName())) {
                countyList.add(county);
                if(!dataList.contains(countyName))
                    dataList.add(countyName);
            }
        }
        adapter.notifyDataSetChanged();
        county_listview.setSelection(0);
    }

}
