package com.polaris.utils;

/**
 * Created by kgopal on 4/20/17.
 */

public class CommonUtils {


    public static String convertKelvinToFarenheit(Double value) {

        Double d = (int) (9 / 5) * (value - 273) + 32;
        return String.valueOf(d.intValue());
    }


    public static String convertKelvinToCelsius(Double value){
        Double d = value - 275;
        return String.valueOf(d);
    }



}
