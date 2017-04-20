package com.polaris.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
        Double d = 9d / 5 * (value - 273.15) + 32;
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

    /**
     * Convert unix time to current time
     */

    public static String getStandardTime(Integer unixTime) {
        long unixSeconds = unixTime;
        Date date = new Date(unixSeconds * 1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("EST")); // give a timezone reference for formating (see comment at the bottom
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    /**
     * get week day for the given unix time
     */

    public static String getWeekday(Integer unixTime) {
        long unixSeconds = unixTime;
        Date date = new Date(unixSeconds * 1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault()); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("EST")); // give a timezone reference for formating (see comment at the bottom
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

}
