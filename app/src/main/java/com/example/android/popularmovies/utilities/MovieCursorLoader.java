package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.example.android.popularmovies.data.MoviesContract;

public class MovieCursorLoader extends CursorLoader {
    public MovieCursorLoader(Context context) {
        super(context);
    }

    @Override
    public Cursor loadInBackground() {
        return getContext().getContentResolver().query
                (MoviesContract.MovieEntry.CONTENT_URI,
                null,null, null,
                MoviesContract.MovieEntry.COLUMN_CREATED_AT);
    }
}
