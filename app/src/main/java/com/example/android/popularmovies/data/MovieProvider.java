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
                retCursor = db.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_MOVIE_WITH_ID:
                selection = "_id=?";
                String id = uri.getPathSegments().get(1);
                selectionArgs = new String[]{id};
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

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                db.beginTransaction();
                for (ContentValues value : values) {
                    db.insert(MovieEntry.TABLE_NAME, null, value);
                }
                rowsInserted = values.length;
                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsInserted;
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
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CODE_MOVIE_WITH_ID:
                selection = "_id=?";
                String id = uri.getPathSegments().get(1);
                selectionArgs = new String[]{id};
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
