package com.polaris.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DecimalFormat;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by kgopal on 4/20/17.
 */

public class CommonUtils {


    private static final String PREFS_NAME = "Prefs";

    /**
     * convert kelvin to farenheit
     *
     * @param value
     * @return
     */
    public static String convertKelvinToFarenheit(Double value) {

        Double d = (int) (9 / 5) * (value - 273.15) + 32;
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return String.valueOf(decimalFormat.format(Math.floor(d)));
    }


    /**
     * convert kelvin to celsius
     *
     * @param value
     * @return
     */

    public static String convertKelvinToCelsius(Double value) {
        Double d = value - 273.15;
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return String.valueOf(decimalFormat.format(Math.floor(d)));
    }


    /**
     * save current city to local pref
     *
     * @param ctx
     * @param city
     */
    public static void saveCity(Context ctx, String city) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("CITY", city);
        editor.commit();
    }


    /**
     * get saved city
     *
     * @param ctx
     * @return
     */
    public static String getCity(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return pref.getString("CITY", null);

    }
}
