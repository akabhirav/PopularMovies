package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.android.popularmovies.data.MoviesContract.MovieEntry;

public class MovieProvider extends ContentProvider {

    private MovieDbHelper mDbHelper;
    private static final int CODE_MOVIE = 100;
    private static final int CODE_MOVIE_WITH_ID = 101;
    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIE, CODE_MOVIE);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                retCursor = db.query(MovieEntry.TABLE_NAME, null, null, null, null, null, null);
                break;
            case CODE_MOVIE_WITH_ID:
                selection = "tmdb_id=?";
                selectionArgs = new String[]{uri.getPathSegments().get(1)};
                retCursor = db.query(MovieEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri retUri;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                long id = db.insert(MovieEntry.TABLE_NAME, null, values);
                retUri = uri.buildUpon().appendPath(String.valueOf(id)).build();
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE_WITH_ID:
                selection = "tmdb_id=?";
                selectionArgs = new String[]{uri.getPathSegments().get(1)};
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
