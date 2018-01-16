package com.example.android.popularmovies.utilities;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class TrailersLoader extends AsyncTaskLoader<ArrayList<String>> {
    private URL videoUrl;

    public TrailersLoader(Context context, int movieId) {
        super(context);
        this.videoUrl = NetworkUtils.buildTrailerUrl(movieId);
    }

    @Override
    public ArrayList<String> loadInBackground() {
        ArrayList<String> videoUrls = new ArrayList<>();
        try {
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(videoUrl);
            videoUrls = NetworkUtils.extractVideosJSONResponse(jsonResponse);
        } catch (IOException e) {
            Log.e("TrailersLoader", "Error opening connection: " + e.getMessage());
        }
        return videoUrls;
    }
}
