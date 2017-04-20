package com.polaris.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.polaris.model.WeatherDetail;
import com.polaris.openweather.OpenWeatherApp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kgopal on 4/19/17.
 */

public class WeatherService {

    /**
     * Callback for weather detail apo service
     */
    public interface WeatherServiceCallbacks {
        public void onSuccessResponse(WeatherDetail weatherDetail);

        public void onFailure();
    }

    public static final String TAG = WeatherService.class.getName();

    public static final String OPEN_WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

    public static final String API_KEY = "5fad988ce5de1a276142e4ea733ebedc";

    public WeatherServiceCallbacks weatherServiceCallbacks;

    /**
     * Calls open weather api and fetches weather inforamtion for the given city
     * Response wired through callbacks
     *
     * @param context
     * @param cityName
     * @param weatherServiceCallbacks
     */
    public void getWeatherDetailForCity(Context context, String cityName, final WeatherServiceCallbacks weatherServiceCallbacks) {

        Log.i(TAG, "Sending Weather Detail API request");

        final String requestURL = OPEN_WEATHER_BASE_URL + "?APPID=" + API_KEY + "&q=" + cityName;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, requestURL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            if (response != null) {
                                if (response.get("cod") instanceof Integer) {
                                    Log.i(TAG, "Response==>" + "Weather API request succeded");
                                    Gson gson = new GsonBuilder().create();
                                    WeatherDetail weatherDetail = gson.fromJson(response.toString(), WeatherDetail.class);
                                    weatherServiceCallbacks.onSuccessResponse(weatherDetail);
                                }
                                else{
                                    Log.e(TAG, "City not found");
                                    weatherServiceCallbacks.onFailure();

                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            weatherServiceCallbacks.onFailure();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Weather API request failed");
                        if (error != null) {
                            Log.e(TAG, "Weather API Error==>", error);
                        }
                        weatherServiceCallbacks.onFailure();
                    }
                });

        OpenWeatherApp app = (OpenWeatherApp) context.getApplicationContext();
        app.addToRequestQueue(jsObjRequest, "WEATHER_API_REQUEST");

    }

}
