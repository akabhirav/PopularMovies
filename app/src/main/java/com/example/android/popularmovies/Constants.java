package com.example.android.popularmovies;

public class Constants {
    public static final String HTTPS = "https"; //Scheme that'll be used for all urls
    public static final String BASE_URL = "api.themoviedb.org/3/movie"; //Base url for tmdb api requests
    public static final String PATH_VIDEOS = "videos";
    public static final String PATH_REVIEWS = "reviews";
    /**
     * API key for the account in use, make a entry in gradle.properties called API_KEY
     * and fill your api key inside it
     */
     public static final String API_KEY = BuildConfig.API_KEY;

}
