package com.example.android.popularmovies.utilities;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ReviewsLoader extends AsyncTaskLoader<ArrayList<String>> {
    private URL reviewUrl;
    public ReviewsLoader(Context context, int movieId) {
        super(context);
        this.reviewUrl = NetworkUtils.buildReviewUrl(movieId);
    }

    @Override
    public ArrayList<String> loadInBackground() {
        ArrayList<String> reviews = new ArrayList<>();
        try {
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(reviewUrl);
            reviews = NetworkUtils.extractReviewsJSONResponse(jsonResponse);
        } catch (IOException e) {
            Log.e("ReviewsLoader", "Error opening connection: " + e.getMessage());
        }
        return reviews;
    }

}
