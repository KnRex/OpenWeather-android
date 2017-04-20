package com.polaris.openweather;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import com.polaris.model.Forecast;
import com.polaris.model.WeatherDetail;
import com.polaris.service.ForecastService;
import com.polaris.service.WeatherService;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by kgopal on 4/20/17.
 */

public class WeatherApiTest extends AndroidTestCase {

    @Test
    public void testWeatherApi() {

        WeatherService weatherService = new WeatherService();
        Context context = getContext();


        weatherService.getWeatherDetailForCity(context, "london", new WeatherService.WeatherServiceCallbacks() {
            @Override
            public void onSuccessResponse(WeatherDetail weatherDetail) {
                assertNotNull(weatherDetail);
            }

            @Override
            public void onFailure(String error) {

            }
        });


    }


    @Test
    public void testForecastApi() {
        ForecastService forecastService = new ForecastService();
        forecastService.getWeatherForecastForCity(getContext(), "london", new ForecastService.ForecastServiceCallback() {
            @Override
            public void onSuccessResponse(Forecast forecast) {
                assertNotNull(forecast);
            }

            @Override
            public void onFailure() {

            }
        });
    }
}
