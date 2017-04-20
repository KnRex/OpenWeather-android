package com.polaris.utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.polaris.model.WeatherList;
import com.polaris.openweather.R;

import java.util.ArrayList;

/**
 * Created by kgopal on 4/19/17.
 */

public class WeatherAdapter extends ArrayAdapter<WeatherList> {

    LayoutInflater inflater;

    public WeatherAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<WeatherList> weatherLists) {
        super(context, resource, weatherLists);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = inflater.inflate(R.layout.weather_list_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.weatherKeyLbl = (TextView) view.findViewById(R.id.weatherKeyLbl);
            viewHolder.weatherInfoLbl = (TextView) view.findViewById(R.id.weatherInfoLbl);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        WeatherList weatherList = getItem(position);
        viewHolder.weatherKeyLbl.setText(weatherList.getWeatherKey());
        viewHolder.weatherInfoLbl.setText(weatherList.getWeatherInfo());

        return view;
    }

    private class ViewHolder {

        TextView weatherKeyLbl;
        TextView weatherInfoLbl;
    }
}
