package com.example.aliez.tomorrowweather;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aliez.tomorrowweather.db.County;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static com.example.aliez.tomorrowweather.CountyAdapter.flag;


/**
 * Created by Aliez on 2017/4/3.
 */

public class CountyListFragment extends Fragment {
    private ListView county_listview;
    private TextView editButton;
    private TextView finishButton;
    private County selectedCounty;
    public static CountyAdapter adapter;
    private Button countyButton;

    private static List<County> countyList = new ArrayList<>();
    public  static List<String> dataList = new ArrayList<>();
    public List<View> viewlist = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.county_list, container, false);
        county_listview = (ListView) view.findViewById(R.id.county_listview);
        adapter = new CountyAdapter(getContext(), R.layout.county_item, dataList);
        editButton = (TextView) view.findViewById(R.id.edit_button);
        finishButton = (TextView) view.findViewById(R.id.finish_button);
        viewlist.clear();
        initCountyList();
//        for (int i = 0; i < dataList.size(); i++) {
//            View view1 = adapter.getView(i, null, container);
//            viewlist.add(view1);
//        }
//
//        for (int i = 0; i < viewlist.size(); i++) {
//            countyButton = (Button) viewlist.get(i).findViewById(R.id.county_button);
//            countyButton.setFocusable(false);
//            countyButton.setVisibility(View.INVISIBLE);
//            Log.i("done", String.valueOf(countyButton.getTag()));
//        }
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButton.setVisibility(View.INVISIBLE);
                /*for (int i = 0; i < viewlist.size(); i++) {
                    //countyButton = (Button)viewlist.get(i).findViewById(R.id.county_button);
                    countyButton.setFocusable(true);
                    countyButton.setVisibility(View.VISIBLE);
                    
                    countyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                            dataList.remove(countyButton.getTag());
                            adapter.notifyDataSetChanged();
                            county_listview.setSelection(0);
                        }
                    });
                }*/
                flag = true;
                finishButton.setVisibility(View.VISIBLE);
            }
        });

        county_listview.setAdapter(adapter);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editButton.setVisibility(View.VISIBLE);
        finishButton.setVisibility(View.INVISIBLE);

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
//        initCountyList();
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
