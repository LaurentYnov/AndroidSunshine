package com.ynov.laurent.sunshine;

import android.text.format.Time;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by laurent on 02/12/2016.
 */

public class JsonParser {

    final String LOG_TAG = JsonParser.class.getSimpleName();

        static String[] getWeatherDataFromJson(String forecast) throws JSONException {

        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJSON = new JSONObject(forecast);
        JSONArray weatherArray = forecastJSON.getJSONArray(OWM_LIST);

        Time dayTime = new Time();
        dayTime.setToNow();
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
        dayTime = new Time();


        String[] resultStrs = new String[weatherArray.length()];
        for (int i = 0; i < weatherArray.length(); i++) {

            String day;
            String description;
            String highAndLow;

            //JSON object to get the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);
            long dateTime;
            dateTime = dayTime.setJulianDay(julianStartDay+i);
            day = getReadableDateString(dateTime);

            //get the description
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);


            //get the temps
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high,low);

            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        return resultStrs;
    }



    static private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    static private String formatHighLows(double high, double low) {
        long roundedHigh = Math.round(high);
        long roundedlow = Math.round(low);
        return roundedHigh + "/" + roundedlow;
    }
}
