package com.polaris.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.polaris.openweather.OpenWeatherApp;

/**
 * Created by kgopal on 4/20/17.
 */

public class ImageLoaderService {

    /**
     * load icon into the ImageView using Image request API
     */

    public void loadImage(final Context ctx, String url, final ImageView imageView) {

        // Initialize a new ImageRequest
        ImageRequest imageRequest = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() { // Bitmap listener
                    @Override
                    public void onResponse(Bitmap response) {

                        imageView.setImageBitmap(response);
                    }
                },
                0,
                0,
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() { // Error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        // Add ImageRequest to the RequestQueue
        OpenWeatherApp app = (OpenWeatherApp) ctx.getApplicationContext();
        app.addToRequestQueue(imageRequest, "ICON_REQUEST");


    }
}
