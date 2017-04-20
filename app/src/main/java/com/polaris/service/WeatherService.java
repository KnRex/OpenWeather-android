package com.polaris.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.polaris.openweather.OpenWeatherApp;

import org.json.JSONObject;

/**
 * Created by kgopal on 4/19/17.
 */

public class WeatherService {

    public static final String TAG = WeatherService.class.getName();

    public static final String OPEN_WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

    public static final String API_KEY = "5fad988ce5de1a276142e4ea733ebedc";

    /**
     * Calls open weather api and fetches weather inforamtion for the given city
     *
     * @param cityName
     */
    public void getWeatherDetailForCity(Context context, String cityName) {

        Log.i(TAG, "Weather Detail API request started");

        final String requestURL = OPEN_WEATHER_BASE_URL + "?APPID=" + API_KEY + "&q=" + cityName;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, requestURL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i(TAG, "Response==>" + response.toString());

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Weather API request failed");
                        if (error != null) {
                            Log.e(TAG, "Weather API Error", error);
                        }
                    }
                });

        OpenWeatherApp app = (OpenWeatherApp) context.getApplicationContext();
        app.addToRequestQueue(jsObjRequest,"WEATHER_API_REQUEST");

    }

}
