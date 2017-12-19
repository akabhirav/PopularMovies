package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    TextView mMovieTitleTextView, mRatingTextView, mOverviewTextView, mReleaseYear;
    ImageView mMoviePosterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mMovieTitleTextView = findViewById(R.id.tv_movie_title);
        mMoviePosterImageView = findViewById(R.id.iv_detail_movie_poster);
        mRatingTextView = findViewById(R.id.tv_rating);
        mOverviewTextView = findViewById(R.id.tv_overview);
        mReleaseYear = findViewById(R.id.tv_year);
        Intent callerIntent = getIntent();
        if (callerIntent.hasExtra("movie")) {
            Movie movie = callerIntent.getParcelableExtra("movie");
            if (movie != null) {
                setMovieToView(movie);
            }
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
        mOverviewTextView.setText(movie.getOverview());
    }

}
