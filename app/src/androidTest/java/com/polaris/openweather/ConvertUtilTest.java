package com.polaris.openweather;

import com.polaris.utils.CommonUtils;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * Created by kgopal on 4/20/17.
 */

public class ConvertUtilTest {

    @Test
    public void testConvertKelvinToFarenheit() {
        String actual = CommonUtils.convertKelvinToFarenheit(283.0);
        String expected = "49";
        assertTrue(actual.equals(expected));
    }

    @Test
    public void testConvertKelvinToCelsius() {
        String actual = CommonUtils.convertKelvinToCelsius(283.0);
        String expected = "9";
        assertTrue(actual.equals(expected));
    }

}
