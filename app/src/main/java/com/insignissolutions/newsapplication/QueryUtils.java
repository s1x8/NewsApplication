package com.insignissolutions.newsapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Create a private constructor because no one should ever create a {@link QueryUtils} object.
 * This class is only meant to hold static variables and methods, which can be accessed
 * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
 */
public class QueryUtils {
    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Query the Guardian news dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchEarthquakeData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        Log.v("Test2",url.toString());

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Earthquake}s
        return news;
    }
    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }
    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(25000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String NewsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(NewsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news articles to
        List<News> newses = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJson = new JSONObject(NewsJSON);

            JSONObject response = baseJson.getJSONObject( "response");

            // Extract the JSONArray associated with teh key called "results"
            // Which represents a list of news data
            JSONArray resultsArray = response.getJSONArray("results");

            // For each news in the resultsArray, create an {@link News} object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single news at position i within the list of resultArray
                JSONObject currentNews = resultsArray.getJSONObject(i);

                //String title = currentNews.getString("webTitle");
                String section = currentNews.getString("sectionName");

                //Get current publication date
                String currentDate = currentNews.getString("webPublicationDate");

                // This is the time format from guardian JSON "2017-10-29T06:00:20Z"
                // will be changed to 29-10-2017 format
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                try {
                    Date newDate = format.parse(currentDate);
                    format = new SimpleDateFormat("dd-MM-yyyy");
                    currentDate = format.format(newDate);
                }catch (ParseException e){
                    Log.e(LOG_TAG,"Problem with parsing the date format");

                }
                String articleUrl = currentNews.getString("webUrl");
                String NewsAuthor = " No author";
                JSONArray tagsArray = currentNews.getJSONArray("tags");
                if(tagsArray.length()!= 0){
                    JSONObject currentTags = tagsArray.getJSONObject(0);
                    NewsAuthor = currentTags.getString("webTitle");
                }

                JSONObject fieldsJSON = currentNews.getJSONObject("fields");
                String title = fieldsJSON.getString("headline");
                String thumbnailUrl = fieldsJSON.getString("thumbnail");

                Bitmap thumbnail = fetchingImage(thumbnailUrl);
                // Create a new {@link News} object with the given parameters,
                // and url from the JSON response.
                News news = new News(title, section, currentDate, articleUrl, NewsAuthor, thumbnail);

                // Add the new {@link Earthquake} to the list of earthquakes.
                newses.add(news);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the News JSON results", e);
        }

        // Return the list of earthquakes
        return newses;
    }
    private static Bitmap fetchingImage(String url){
        URL mUrl = createUrl(url);
        Bitmap mBitmap= null;
        try {
            mBitmap = makeHTTPConnection(mUrl);
        }catch (IOException e){
            Log.e(LOG_TAG,"Making connection for image",e);
        }
        return mBitmap;
    }
    /** Making a HTTP connection for thumbnails
     */
    private static Bitmap makeHTTPConnection(URL url)throws IOException{

        Bitmap mBitmap = null;

        //Creating Http Connection object and inputstream object
        HttpURLConnection urlConnection;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(2000);
            urlConnection.setConnectTimeout(2500);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                BufferedInputStream bInputStream =  new BufferedInputStream(inputStream);
                mBitmap = BitmapFactory.decodeStream(bInputStream);
                return mBitmap;
            } else {
                Log.e("LOG_TAG", "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e("LOG_TAG", "Problem retrieving results.", e);
        }

        return mBitmap;
    }
}
