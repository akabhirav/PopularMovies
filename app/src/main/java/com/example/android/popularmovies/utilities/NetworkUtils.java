package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.Constants;
import com.example.android.popularmovies.data.Movie;

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

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    private static final String BAD_RESPONSE = "BAD_RESPONSE";

    /**
     * Method that returns the url for fetching a list of movies from tmdb
     *
     * @param sortType type of sorting with which to make the URL
     * @return final url
     */
    public static URL buildUrl(String sortType, int pageNo) {
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
    public static String buildImageURL(String imageCode) {
        return "https://image.tmdb.org/t/p/w500/" + imageCode;
    }

    /**
     * Build url for trailers of a movie
     *
     * @param movieId id of the movie
     * @return {@link URL} url for trailers of a movie
     */
    static URL buildTrailerUrl(int movieId) {
        Uri.Builder builder = new Uri.Builder();
        builder
                .scheme(Constants.HTTPS)
                .path(Constants.BASE_URL)
                .appendPath(String.valueOf(movieId))
                .appendPath(Constants.PATH_VIDEOS)
                .appendQueryParameter("api_key", Constants.API_KEY);
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
     * Build url for reviews of a movie
     *
     * @param movieId id of the movie
     * @return {@link URL} url for reviews of a movie
     */
    static URL buildReviewUrl(int movieId) {
        Uri.Builder builder = new Uri.Builder();
        builder
                .scheme(Constants.HTTPS)
                .path(Constants.BASE_URL)
                .appendPath(String.valueOf(movieId))
                .appendPath(Constants.PATH_REVIEWS)
                .appendQueryParameter("api_key", Constants.API_KEY);
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
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(2000);
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
                jsonResponse = BAD_RESPONSE;
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
     * Method to extract youtube video ids from trailers url
     *
     * @param jsonResponse json response from http request
     * @return returns an {@link ArrayList<String>} list of video ids
     */
    static ArrayList<String> extractVideosJSONResponse(String jsonResponse) {
        ArrayList<String> videoKeys = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(jsonResponse);
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = (JSONObject) results.get(i);

                String site = result.getString("site");
                switch (site) {
                    case "YouTube":
                        videoKeys.add(result.getString("key"));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error: " + e.getMessage());
        }
        return videoKeys;
    }

    /**
     * Method for extracting reviews from json response
     *
     * @param jsonResponse json response from http request
     * @return {@link ArrayList<String>} reviews for a movie
     */
    static ArrayList<String> extractReviewsJSONResponse(String jsonResponse) {
        ArrayList<String> reviews = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(jsonResponse);
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = (JSONObject) results.get(i);
                reviews.add(result.getString("content"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error: " + e.getMessage());
        }
        return reviews;
    }

    /**
     * @param context context from where it is called
     * @return whether the device is online or not
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }
}
