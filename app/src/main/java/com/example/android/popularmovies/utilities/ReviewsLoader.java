package com.example.android.popularmovies.utilities;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class ReviewsLoader extends AsyncTaskLoader<String[]> {
    public ReviewsLoader(Context context) {
        super(context);
    }

    @Override
    public String[] loadInBackground() {
        return new String[0];
    }
}
