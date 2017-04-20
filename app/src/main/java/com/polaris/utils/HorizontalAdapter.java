package com.polaris.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.polaris.model.Forecast;
import com.polaris.openweather.OpenWeatherApp;
import com.polaris.openweather.R;

import java.util.List;

/**
 * Class for adapting weather forecast data
 * Created by kgopal on 4/20/17.
 */

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

    private final ImageLoader imageLoader;
    private List<com.polaris.model.List> horizontalList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dayLbl, desclbl;
        private NetworkImageView thumbNail;


        public MyViewHolder(View view) {
            super(view);
            dayLbl = (TextView) view.findViewById(R.id.forecastday);
            desclbl = (TextView) view.findViewById(R.id.forecastdescription);
            thumbNail = (NetworkImageView) view
                    .findViewById(R.id.forecastImg);

        }
    }


    public HorizontalAdapter(Context context, List<com.polaris.model.List> horizontalList) {
        this.horizontalList = horizontalList;

        OpenWeatherApp openWeatherApp = (OpenWeatherApp) context.getApplicationContext();
        imageLoader = openWeatherApp.getImageLoader();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast, parent, false);

        return new MyViewHolder(itemView);
    }

    /**
     * Bindig text, images to views
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        try {
            com.polaris.model.List list = horizontalList.get(position+1);
            holder.dayLbl.setText(CommonUtils.getWeekday(list.getDt()));
            holder.desclbl.setText(list.getWeather().get(0).getMain());
            holder.thumbNail.setImageUrl(Constants.WEATHER_ICON_URL + list.getWeather().get(0).getIcon() + ".png", imageLoader);


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.getClass().getSimpleName(), "Error in binding views");
        }


    }

    @Override
    public int getItemCount() {
        return horizontalList.size()-1;
    }
}
