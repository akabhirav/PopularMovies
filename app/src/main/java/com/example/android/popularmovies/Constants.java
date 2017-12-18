package com.example.android.popularmovies;

class Constants {
    static final String HTTPS = "https"; //Scheme that'll be used for all urls
    static final String BASE_URL = "api.themoviedb.org/3/movie"; //Base url for tmdb api requests
    /**
     * API key for the account in use, make a entry in gradle.properties called API_KEY
     * and fill your api key inside it
     */
    static final String API_KEY = BuildConfig.API_KEY;

}
