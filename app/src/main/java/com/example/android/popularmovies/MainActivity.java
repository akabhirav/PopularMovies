package com.example.android.popularmovies;

import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MoviesContract;
import com.example.android.popularmovies.utilities.MovieAsyncTask;
import com.example.android.popularmovies.utilities.MovieCursorLoader;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        MoviesAdapter.MoviesAdapterOnClickHandler,
        MovieAsyncTask.MovieAsyncTaskActivityStates,
        LoaderManager.LoaderCallbacks<Cursor> {

    private MoviesAdapter mMoviesAdapter; //adapter used in recycler view
    RecyclerView mMoviesRecyclerView; //recycler view
    TextView mErrorTextView;// text view that displays error
    private int pageNo = 1; // last page fetched
    private boolean isLoading = false; // tells if there are any ongoing api requests
    private GridLayoutManager mLayoutManager = null; // layout manager used in recycler view
    ArrayList<Movie> movies = null; // list of movies
    int position = 0; // current scroll position of recycler view
    int spanCount; // number of columns in grid layout
    private ProgressBar mLoadingIndicator; // loading indicator that is visible when data is loading
    private final static int FAVOURITE_LOADER_ID = 3;
    private static String sortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList("movies");
        }
        setContentView(R.layout.activity_main);
        mMoviesRecyclerView = findViewById(R.id.rv_movies);
        mErrorTextView = findViewById(R.id.tv_error_text);
        mLoadingIndicator = findViewById(R.id.pb_loader);
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
        int finalPosition = position - spanCount;
        mMoviesRecyclerView.smoothScrollToPosition(finalPosition < 0 ? 0 : finalPosition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String newSort = sharedPrefs.getString(getString(R.string.pref_sort_type_key), getString(R.string.pref_sort_type_default_value));
        if (!sortBy.equals(newSort)) {
            mMoviesAdapter.flushData();
            loadMovies(1);
        }
    }

    /**
     * Returns a new {@link RecyclerView.OnScrollListener}
     *
     * @return OnScrollListener
     */
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
     */
    void loadMovies(int pageNo) {
        isLoading = true;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sortBy = sharedPrefs.getString(getString(R.string.pref_sort_type_key), getString(R.string.pref_sort_type_default_value));
        URL url = NetworkUtils.buildUrl(sortBy, pageNo);
        if (NetworkUtils.isOnline(this) && !sortBy.equals(getString(R.string.favourites))) {
            hideOfflineMessage();
            new MovieAsyncTask(this).execute(url);
        } else if (sortBy.equals(getString(R.string.favourites))) {
            getSupportLoaderManager().initLoader(FAVOURITE_LOADER_ID, null, this);
        } else {
            if (pageNo == 1) {
                showOfflineMessage();
                getSupportLoaderManager().initLoader(FAVOURITE_LOADER_ID, null, this);
            }
        }
    }

    void hideOfflineMessage() {
        TextView offlineMessage = findViewById(R.id.tv_offline);
        offlineMessage.setVisibility(View.GONE);
        mMoviesRecyclerView.setPadding(0, 0, 0, 0);
    }

    void showOfflineMessage() {
        TextView offlineMessage = findViewById(R.id.tv_offline);
        offlineMessage.setVisibility(View.VISIBLE);
        mMoviesRecyclerView.setPadding(0, 100, 0, 0);
    }

    @Override
    public void onClick(Movie movie) {
        Intent details = new Intent(this, DetailActivity.class);
        details.putExtra("movie", movie);
        startActivity(details);
    }

    @Override
    public void showMovieList() {
        mMoviesRecyclerView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.GONE);
    }

    @Override
    public void showErrorMessage() {
        mMoviesRecyclerView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void isLoading() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void isLoaded() {
        mLoadingIndicator.setVisibility(View.GONE);
        isLoading = false;
    }

    @Override
    public void setAdapterData(ArrayList<Movie> data) {
        mMoviesAdapter.setOrAddMoviesData(data);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MovieCursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Movie> arrayList = new ArrayList<>();
        int length = data.getCount();
        for (int i = 0; i < length; i++) {
            data.moveToPosition(i);
            int id = data.getInt(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TMDB_ID));
            String title = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE));
            String url = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_IMAGE));
            String releaseDate = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE));
            String overview = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW));
            double rating = data.getDouble(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RATING));
            Movie movie = new Movie(id, title, url, releaseDate, overview, rating);
            arrayList.add(movie);
        }
        mMoviesAdapter.flushData();
        mMoviesAdapter.setOrAddMoviesData(arrayList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
