package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private MoviesAdapter mMoviesAdapter; //adapter used in recycler view
    RecyclerView mMoviesRecyclerView; //recycler view
    TextView mErrorTextView;// text view that displays error
    private int pageNo = 1; // last page fetched
    private boolean isLoading = false; // tells if there are any ongoing api requests
    private GridLayoutManager mLayoutManager = null; // layout manager used in recycler view
    ArrayList<Movie> movies = null; // list of movies
    int position = 0; // current scroll position of recycler view
    int spanCount; // number of columns in grid layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList("movies");
        }
        setContentView(R.layout.activity_main);
        mMoviesRecyclerView = findViewById(R.id.rv_movies);
        mErrorTextView = findViewById(R.id.tv_error_text);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            spanCount = 2;
        else
            spanCount = 4;
        mLayoutManager = new GridLayoutManager(this, spanCount, LinearLayoutManager.VERTICAL, false);
        mMoviesRecyclerView.setLayoutManager(mLayoutManager);
        mMoviesRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this);
        mMoviesRecyclerView.addOnScrollListener(createInfiniteScrollListener());
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);
        if (movies == null) {
            loadMovies(pageNo);
        } else {
            mMoviesAdapter.setOrAddMoviesData(movies);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", position);
        outState.putParcelableArrayList("movies", mMoviesAdapter.getMoviesData());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("position");
        mMoviesRecyclerView.smoothScrollToPosition(position - spanCount);
    }

    /**
     * Returns a new {@link RecyclerView.OnScrollListener}
     *
     * @return OnScrollListener
     * */
    private RecyclerView.OnScrollListener createInfiniteScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastItem = mLayoutManager.findLastVisibleItemPosition();
                int itemSize = mMoviesAdapter.getItemCount();
                if (lastItem != -1) {
                    position = lastItem;
                }
                if (dy > 0 && lastItem >= (itemSize - 1) && !isLoading) {
                    loadMovies(++pageNo);
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Loads movies for a specific page
     *
     * @param pageNo which page number to fetch data for
     * */
    void loadMovies(int pageNo) {
        isLoading = true;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy = sharedPrefs.getString(getString(R.string.pref_sort_type_key), getString(R.string.pref_sort_type_default_value));
        URL url = NetworkUtils.buildUrl(sortBy, pageNo);
        if (NetworkUtils.isOnline(this)) {
            new MovieAsyncTask().execute(url);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent details = new Intent(this, DetailActivity.class);
        details.putExtra("movie", movie);
        startActivity(details);
    }

    /**
     * Method to make the data visible and hide the error message
     */
    void showMovieList() {
        mMoviesRecyclerView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.GONE);
    }

    /**
     * Method to make the error visible and hide the recycler view
     */
    void showErrorMessage() {
        mMoviesRecyclerView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    private class MovieAsyncTask extends AsyncTask<URL, Void, ArrayList<Movie>> {
        ProgressBar mLoadingProgressBar;

        @Override
        protected void onPreExecute() {
            mLoadingProgressBar = findViewById(R.id.pb_loader);
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            showMovieList();
        }

        @Override
        protected ArrayList<Movie> doInBackground(URL... urls) {
            ArrayList<Movie> movieNames = new ArrayList<>();
            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(urls[0]);
                movieNames = NetworkUtils.extractJSONResponse(jsonResponse);
            } catch (IOException e) {
                Log.e("MovieAsyncTask", "Error opening connection: " + e.getMessage());
            }
            return movieNames;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            isLoading = false;
            mLoadingProgressBar.setVisibility(View.GONE);
            if (movies.size() != 0) {
                mMoviesAdapter.setOrAddMoviesData(movies);
            } else {
                showErrorMessage();
            }
        }
    }
}
