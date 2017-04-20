package com.polaris.openweather;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.polaris.model.WeatherDetail;
import com.polaris.model.WeatherList;
import com.polaris.service.ImageLoaderService;
import com.polaris.service.WeatherService;
import com.polaris.utils.WeatherAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class WeatherMainActivity extends AppCompatActivity {

    private ListView weatherDetailListView;

    private ArrayList<WeatherList> weatherListData;

    private TextView cityLbl;

    private TextView tempLbl;

    private TextView descriptionLbl;

    private ImageView weatherIconView;

    private static final String WEATHER_ICON_URL = "http://openweathermap.org/img/w/";

    WeatherAdapter weatherAdapter;


    /**
     * Activity view gets loaded,
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);
        weatherDetailListView = (ListView) findViewById(R.id.weatherDetailList);
        LayoutInflater myinflater = getLayoutInflater();
        ViewGroup myHeader = (ViewGroup) myinflater.inflate(R.layout.weather_detail_header, null);
        weatherDetailListView.addHeaderView(myHeader);
        weatherListData = new ArrayList<>();

        cityLbl = (TextView) myHeader.findViewById(R.id.cityLbl);
        tempLbl = (TextView) myHeader.findViewById(R.id.weatherDegree);
        descriptionLbl = (TextView) myHeader.findViewById(R.id.weatherDescription);
        weatherIconView = (ImageView) myHeader.findViewById(R.id.weatherIconView);


        // Weather api call
        WeatherService weatherService = new WeatherService();
        weatherService.getWeatherDetailForCity(this, "london", new WeatherService.WeatherServiceCallbacks() {
            @Override
            public void onSuccessResponse(WeatherDetail weatherDetail) {

                loadWeatherDetail(weatherDetail);
            }

            @Override
            public void onFailure() {


            }
        });
    }


    /**
     * loads weather detail into the listview
     *
     * @param weatherDetail
     */
    private void loadWeatherDetail(WeatherDetail weatherDetail) {

        if (weatherDetail != null) {

            if (weatherDetail.getMain().getHumidity() != null) {
                WeatherList humidity = new WeatherList();
                humidity.setWeatherKey("Humidity :");
                humidity.setWeatherInfo(weatherDetail.getMain().getHumidity().toString() + " %");
                weatherListData.add(humidity);
            }

            if (weatherDetail.getMain().getPressure() != null) {
                WeatherList pressure = new WeatherList();
                pressure.setWeatherKey("Pressure :");
                pressure.setWeatherInfo(weatherDetail.getMain().getPressure().toString() + " hPa");
                weatherListData.add(pressure);
            }

            if (weatherDetail.getWind().getSpeed() != null) {
                WeatherList speed = new WeatherList();
                speed.setWeatherKey("Speed :");
                speed.setWeatherInfo(weatherDetail.getWind().getSpeed().toString() + " km/hr");
                weatherListData.add(speed);
            }

            if (weatherDetail.getVisibility() != null) {
                WeatherList visibility = new WeatherList();
                visibility.setWeatherKey("Visibility :");
                visibility.setWeatherInfo(weatherDetail.getVisibility().toString() + " km");
                weatherListData.add(visibility);
            }

            cityLbl.setText(weatherDetail.getName() + ", " + weatherDetail.getSys().getCountry());
            descriptionLbl.setText(weatherDetail.getWeather().get(0).getDescription());

            String iconUrl = WEATHER_ICON_URL + weatherDetail.getWeather().get(0).getIcon() + ".png";

            ImageLoaderService imageLoaderService = new ImageLoaderService();
            imageLoaderService.loadImage(this, iconUrl, weatherIconView);

            if (weatherAdapter == null) {
                weatherAdapter = new WeatherAdapter(this, R.layout.weather_list_row, weatherListData);
                weatherDetailListView.setAdapter(weatherAdapter);
            }

        }

    }


    /**
     * Inflating action bar search menu
     *
     * @param menu
     * @return
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_view, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        // searchView.setOnQueryTextListener(this);
        return true;
    }


}

