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
import com.polaris.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by kgopal on 4/19/17.
 */

public class WeatherService {

    /**
     * Callback for weather detail apo service
     */
    public interface WeatherServiceCallbacks {
        public void onSuccessResponse(WeatherDetail weatherDetail);

        public void onFailure(String error);
    }

    public static final String TAG = WeatherService.class.getName();

    public static final String OPEN_WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";


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


        String requestURL = "";
        try {
            requestURL = OPEN_WEATHER_BASE_URL + "?APPID=" + Constants.API_KEY + "&q=" + URLEncoder.encode(cityName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ;


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, requestURL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            if (response != null) {
                                if (response.get("cod") instanceof Integer) {
                                    Log.d(TAG, "Response==>" + response.toString());
                                    Gson gson = new GsonBuilder().create();
                                    WeatherDetail weatherDetail = gson.fromJson(response.toString(), WeatherDetail.class);
                                    weatherServiceCallbacks.onSuccessResponse(weatherDetail);
                                } else {
                                    Log.e(TAG, "City not found");
                                    weatherServiceCallbacks.onFailure("City not found");

                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            weatherServiceCallbacks.onFailure("Server Error. Please check your internet connection");
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Weather API request failed");
                        if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 404) {
                            Log.e(TAG, "Weather API Error==>", error);
                            weatherServiceCallbacks.onFailure("City not found");
                        } else {
                            weatherServiceCallbacks.onFailure("Server Error. Please check your internet connection");
                        }
                    }
                });

        OpenWeatherApp app = (OpenWeatherApp) context.getApplicationContext();
        app.addToRequestQueue(jsObjRequest, "WEATHER_API_REQUEST");

    }


}
