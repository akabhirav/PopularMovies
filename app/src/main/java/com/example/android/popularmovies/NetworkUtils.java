package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    /**
     * Method that returns the url for fetching a list of movies from tmdb
     *
     * @param sortType type of sorting with which to make the URL
     * @return final url
     */
    static URL buildUrl(String sortType, int pageNo) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Constants.HTTPS).path(Constants.BASE_URL).appendPath(sortType).appendQueryParameter("api_key", Constants.API_KEY).appendQueryParameter("page", String.valueOf(pageNo));
        Uri uri = builder.build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURL: " + e.toString());
        }
        return url;
    }

    /**
     * Build full image url from the image code that we get from tmdb
     *
     * @param imageCode unique image code from tmdb
     * @return full image url
     */
    static String buildImageURL(String imageCode) {
        return "https://image.tmdb.org/t/p/w500/" + imageCode;
    }

    /**
     * Method that fetches response from url and returns the response
     *
     * @param url url from which data is to be fetched
     * @return json response from the http request on url
     * @throws IOException exception thrown when error opening connection
     */
    @Nullable
    static String getResponseFromHttpUrl(URL url) throws IOException {
        String jsonResponse = null;
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            if (urlConnection.getResponseCode() == 200) {
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                jsonResponse = result.toString();
            } else {
                Log.e(TAG, "Bad Response: " + urlConnection.getResponseCode());
            }

        } catch (Exception e) {
            Log.e(TAG, "Some Exception: " + e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return jsonResponse;
    }

    /**
     * Method to extract a list of movies with only id, title, and image url from json
     *
     * @param jsonResponse json string which contains the data
     * @return returns an {@link ArrayList<Movie>}
     */
    static ArrayList<Movie> extractJSONResponse(String jsonResponse) {
        ArrayList<Movie> movieNames = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(jsonResponse);
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                int id = movie.getInt("id");
                String title = movie.getString("title");
                String imageUrl = movie.getString("poster_path");
                String overview = movie.getString("overview");
                double voteAverage = movie.getDouble("vote_average");
                String releaseDate = movie.getString("release_date");
                movieNames.add(new Movie(id, title, imageUrl, releaseDate, overview, voteAverage));
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error: " + e.toString());
        }
        return movieNames;
    }

    /**
     * @param context context from where it is called
     * @return whether the device is online or not
     */
    static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }
}
