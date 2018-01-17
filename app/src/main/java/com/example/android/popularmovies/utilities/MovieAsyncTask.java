package com.example.android.popularmovies.utilities;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.data.Movie;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MovieAsyncTask extends AsyncTask<URL, Void, ArrayList<Movie>> {
    private MovieAsyncTaskActivityStates state;

    /**
     * Constructor for {@link MovieAsyncTask}
     *
     * @param state {@link MovieAsyncTaskActivityStates} methods that make activity side changes on
     *              changes in async task
     */
    public MovieAsyncTask(MovieAsyncTaskActivityStates state) {
        this.state = state;
    }

    /**
     * Interface for managing changes in activity during different steps
     * of the async task
     */
    public interface MovieAsyncTaskActivityStates {

        /**
         * Method to hide the error view and show the view that will contain the movie list
         */
        void showMovieList();

        /**
         * Method to hide the movie list and show the error message
         */
        void showErrorMessage();

        /**
         * Method to make changes in activity before loading data
         */
        void isLoading();

        /**
         * Method to make changes in activity when the data is loaded
         */
        void isLoaded();

        /**
         * Method to set movie list to the adapter
         *
         * @param data {@link ArrayList<Movie>} list of movies that were fetched
         */
        void setAdapterData(ArrayList<Movie> data);
    }

    @Override
    protected void onPreExecute() {
        state.isLoading();
        state.showMovieList();
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
        state.isLoaded();
        if (movies.size() != 0) {
            state.setAdapterData(movies);
        } else {
            state.showErrorMessage();
        }
    }
}