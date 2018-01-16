package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MoviesContract;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.ReviewsAdapter;
import com.example.android.popularmovies.utilities.ReviewsLoader;
import com.example.android.popularmovies.utilities.TrailerAdapter;
import com.example.android.popularmovies.utilities.TrailersLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>> {

    TextView mMovieTitleTextView, mRatingTextView, mOverviewTextView, mReleaseYear;
    ImageView mMoviePosterImageView;
    Button mMarkFavourite;
    Boolean isFavourite;
    RecyclerView mTrailersRecyclerView, mReviewsRecyclerView;
    TrailerAdapter mTrailersAdapter;
    ReviewsAdapter mReviewsAdapter;
    static final int TRAILER_LOADER_ID = 1;
    static final int REVIEWS_LOADER_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mMovieTitleTextView = findViewById(R.id.tv_movie_title);
        mMoviePosterImageView = findViewById(R.id.iv_detail_movie_poster);
        mRatingTextView = findViewById(R.id.tv_rating);
        mOverviewTextView = findViewById(R.id.tv_overview);
        mReleaseYear = findViewById(R.id.tv_year);
        mMarkFavourite = findViewById(R.id.b_favourite);
        mTrailersRecyclerView = findViewById(R.id.rv_trailers);
        mReviewsRecyclerView = findViewById(R.id.rv_reviews);
        Intent callerIntent = getIntent();
        if (callerIntent.hasExtra("movie")) {
            final Movie movie = callerIntent.getParcelableExtra("movie");
            if (movie != null) {
                setMovieToView(movie);
                final Uri movieUri = MoviesContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.getId())).build();
                Cursor cursor = getContentResolver().query(movieUri, null, null, null, null);
                isFavourite = cursor != null && cursor.getCount() == 1;
                if (isFavourite) {
                    mMarkFavourite.setBackgroundResource(android.R.color.black);
                    mMarkFavourite.setTextColor(getResources().getColor(android.R.color.white));
                } else {
                    mMarkFavourite.setBackgroundResource(android.R.color.darker_gray);
                    mMarkFavourite.setTextColor(getResources().getColor(android.R.color.black));
                }
                mMarkFavourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleFavourite(movie);
                    }
                });
                Bundle args = new Bundle();
                args.putInt("movie_id", movie.getId());
                getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, args, this).forceLoad();
                getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, args, this).forceLoad();
            }
        }
    }

    private void toggleFavourite(Movie movie) {
        final Uri movieUri = MoviesContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.getId())).build();
        Cursor cursor = getContentResolver().query(movieUri, null, null, null, null);
        isFavourite = cursor != null && cursor.getCount() == 1;
        if (!isFavourite) {
            ContentValues values = new ContentValues();
            values.put(MoviesContract.MovieEntry.COLUMN_TMDB_ID, movie.getId());
            getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, values);
            isFavourite = true;
            mMarkFavourite.setBackgroundResource(android.R.color.black);
            mMarkFavourite.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            getContentResolver().delete(movieUri, null, null);
            isFavourite = false;
            mMarkFavourite.setBackgroundResource(android.R.color.darker_gray);
            mMarkFavourite.setTextColor(getResources().getColor(android.R.color.black));
        }
    }


    /**
     * Set data from {@link Movie} to View
     *
     * @param movie a {@link Movie} object which contains data about a movie
     */
    void setMovieToView(Movie movie) {
        String imageUrl = NetworkUtils.buildImageURL(movie.getImageUrl());
        Picasso.with(this).load(imageUrl).into(mMoviePosterImageView);
        mMovieTitleTextView.setText(movie.getTitle());
        String[] releaseDates = movie.getReleaseDate().split("-");
        String releaseYear = releaseDates[0];
        mReleaseYear.setText(releaseYear);
        String rating = Double.toString(movie.getVoteAverage()) + " / 10";
        mRatingTextView.setText(rating);
        mOverviewTextView.setText(String.valueOf(movie.getOverview()));
    }

    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case TRAILER_LOADER_ID:
                return new TrailersLoader(this, args.getInt("movie_id"));
            case REVIEWS_LOADER_ID:
                return new ReviewsLoader(this, args.getInt("movie_id"));
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        if(loader instanceof TrailersLoader){
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mTrailersRecyclerView.setLayoutManager(layoutManager);
            mTrailersRecyclerView.setHasFixedSize(true);
            mTrailersAdapter = new TrailerAdapter(data);
            mTrailersRecyclerView.setAdapter(mTrailersAdapter);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mReviewsRecyclerView.setLayoutManager(layoutManager);
            mReviewsRecyclerView.setHasFixedSize(true);
            mReviewsAdapter = new ReviewsAdapter(data);
            mReviewsRecyclerView.setAdapter(mReviewsAdapter);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {

    }
}
