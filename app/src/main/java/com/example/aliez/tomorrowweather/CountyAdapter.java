package com.example.aliez.tomorrowweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Aliez on 2017/4/19.
 */

public class CountyAdapter extends ArrayAdapter<String> {
    private int resourceId;
    public static boolean flag = false;

    public CountyAdapter(Context context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView countyName = (TextView) view.findViewById(R.id.county_name);
        Button countyButton = (Button) view.findViewById(R.id.county_button);
        countyButton.setTag(position);
        countyButton.setVisibility(View.INVISIBLE);
        if (flag) {
            countyButton.setVisibility(View.VISIBLE);
            countyButton.setFocusable(true);

            countyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CountyListFragment.dataList.remove(position);
                    CountyListFragment.adapter.notifyDataSetChanged();
                }
            });
        }
        countyName.setText(item);
        return view;
    }


}
