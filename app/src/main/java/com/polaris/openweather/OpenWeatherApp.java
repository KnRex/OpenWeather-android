package com.polaris.openweather;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by kgopal on 4/19/17.
 * Application class
 */

public class OpenWeatherApp extends Application {

    public RequestQueue mRequestQueue;


    /**
     * method called once application is launched. Does initialization for the app.
     */
    @Override
    public void onCreate() {
        super.onCreate();

    }


    /**
     * get the api request
     * @return
     */

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * add server api request to the centralized queue which is provided by Volley
     * @param req
     * @param tag
     * @param <T>
     */

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? "" : tag);
        getRequestQueue().add(req);
    }

    /**
     * Cancel the api request that is submitted to the queue
     * @param tag
     */

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
