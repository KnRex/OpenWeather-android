package com.polaris.openweather;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.polaris.model.WeatherDetail;
import com.polaris.service.WeatherService;

public class WeatherMainActivity extends AppCompatActivity {


    /**
     * Activity view gets loaded,
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);
        // Weather api call
        WeatherService weatherService = new WeatherService();
        weatherService.getWeatherDetailForCity(this, "london", new WeatherService.WeatherServiceCallbacks() {
            @Override
            public void onSuccessResponse(WeatherDetail weatherDetail) {

            }

            @Override
            public void onFailure() {



            }
        });
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
