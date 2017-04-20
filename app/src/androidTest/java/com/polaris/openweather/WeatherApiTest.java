package com.polaris.openweather;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import com.polaris.model.WeatherDetail;
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
}
