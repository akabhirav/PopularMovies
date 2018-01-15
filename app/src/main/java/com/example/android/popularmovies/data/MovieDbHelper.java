package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.MoviesContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MovieEntry.TABLE_NAME + "(" +
                        MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieEntry.COLUMN_TMDB_ID + " INTEGER UNIQUE NOT NULL," +
                        MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                        MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                        MovieEntry.COLUMN_IMAGE + " TEXT NOT NULL," +
                        MovieEntry.COLUMN_RATING + " REAL NOT NULL," +
                        MovieEntry.COLUMN_RELEASE_DATE + " DATE NOT NULL," +
                        MovieEntry.COLUMN_FAVOURITE + " BOOLEAN NOT NULL DEFAULT 0" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
