package com.polaris.service;

/**
 * Created by kgopal on 4/20/17.
 */

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.polaris.model.Forecast;
import com.polaris.model.WeatherDetail;
import com.polaris.openweather.OpenWeatherApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

import static com.polaris.openweather.R.layout.forecast;

/**
 * Created by kgopal on 4/19/17.
 */

public class ForecastService {

    /**
     * Callback for weather detail apo service
     */
    public interface ForecastServiceCallback {
        public void onSuccessResponse(Forecast forecast);

        public void onFailure();
    }

    public static final String TAG = WeatherService.class.getName();

    public static final String OPEN_WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";


    public ForecastServiceCallback forecastServiceCallback;

    /**
     * Calls open weather api and fetches weather forecast inforamtion for five days
     * Response wired through callbacks
     *
     * @param context
     * @param cityName
     * @param forecastServiceCallback
     */
    public void getWeatherForecastForCity(Context context, String cityName, final ForecastServiceCallback forecastServiceCallback) {

        Log.i(TAG, "Weather Forecast API request");


        String requestURL = "";

        try {
            requestURL = OPEN_WEATHER_BASE_URL + "?APPID=" + Constants.API_KEY + "&q=" + URLEncoder.encode(cityName, "UTF-8") + "&cnt=6";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ;

        Log.i(TAG, "URL" + requestURL);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, requestURL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            if (response != null) {
                                Log.i(TAG, "Forecast Response==>" + response.toString());
                                Gson gson = new GsonBuilder().create();
                                Forecast forecast = gson.fromJson(response.toString(), Forecast.class);
                                forecastServiceCallback.onSuccessResponse(forecast);

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            forecastServiceCallback.onFailure();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Weather API request failed");
                        if (error != null) {
                            Log.e(TAG, "Weather Forecast API Error==>", error);

                        }
                        forecastServiceCallback.onFailure();

                    }
                });

        OpenWeatherApp app = (OpenWeatherApp) context.getApplicationContext();
        app.addToRequestQueue(jsObjRequest, "WEATHER_FORECAST_API_REQUEST");

    }


}
