package com.ynov.laurent.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by laurent on 10/11/2016.
 */

public class WeatherFetcher extends AsyncTask<String,Void,String[]> {

    private final String LOG_TAG = String.valueOf(this.getClass());

    WeatherFetcherListener mListner;

    public WeatherFetcher(WeatherFetcherListener listener) {
        this.mListner= listener;
    }

    private String format = "json";
    private String units = "metric";
    private int numDays = 7;



    @Override
    protected String[] doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;
        String location = params[0];

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast

            final String FORECAST_BASE_URL =
                    "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "q";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";
            final String appIp = "APPID";
            final String apiKey = BuildConfig.OPEN_WEATHER_MAP_API_KEY;

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM,location)
                    .appendQueryParameter(FORMAT_PARAM,format)
                    .appendQueryParameter(UNITS_PARAM,units)
                    .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays))
                    .appendQueryParameter(appIp,apiKey)
                    .build();

            URL url = new URL(builtUri.toString());


            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
            try {
                return JsonParser.getWeatherDataFromJson(forecastJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e(LOG_TAG,forecastJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }


        return null;
    }


    @Override
    protected void onPostExecute(String[] result) {
        mListner.didGetData(result);
    }
}
