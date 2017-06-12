package com.example.android.weatherwithpreferences.data;

/**
 * Created by hessro on 5/10/17.
 */

public class WeatherPreferences {

    private static final String DEFAULT_FORECAST_LOCATION = "Corvallis, OR";
    private static final String DEFAULT_TEMPERATURE_UNITS = "imperial";
    private static final String DEFAULT_TEMPERATURE_UNITS_ABBR = "F";
    private static final String DEFAULT_DATE_FORMAT = "EEE, MMM d, h:mm a";

    public static String getDefaultForecastLocation() {
        return DEFAULT_FORECAST_LOCATION;
    }

    public static String getDefaultTemperatureUnits() {
        return DEFAULT_TEMPERATURE_UNITS;
    }

    public static String getDefaultTemperatureUnitsAbbr() {
        return DEFAULT_TEMPERATURE_UNITS_ABBR;
    }

    public static String getDefaultDateFormat() {
        return DEFAULT_DATE_FORMAT;
    }
}
