package com.example.aliez.tomorrowweather.db;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.aliez.tomorrowweather.R;

import java.util.List;

/**
 * Created by Administrator on 2017/4/9.
 */

public class SearchResultAdapter extends ArrayAdapter<SearchResult> {
    private int resourceId;
    public SearchResultAdapter(Context context, int textViewResourceId, List<SearchResult> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchResult src = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView tvp = (TextView)view.findViewById(R.id.id_text_province);
        TextView tvc = (TextView)view.findViewById(R.id.id_text_city);
        TextView tvcn = (TextView)view.findViewById(R.id.id_text_county);
        tvp.setText("  " + src.getProvince().getProvinceName()+", ");
        tvc.setText(src.getCity().getCityName()+", ");
        tvcn.setText(src.getCounty().getCountyName());
        return view;
    }


}
