package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * {@link MoviesAdapter} exposes a list of {@link Movie} to a {@link RecyclerView}
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    /**
     * ArrayList for movies
     */
    private ArrayList<Movie> mMoviesData;
    private Context context;
    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private MoviesAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    interface MoviesAdapterOnClickHandler {
        /**
         * Method that we call when a view is clicked inside the {@link RecyclerView}
         *
         * @param movie {@link Movie} object that contains all the information about the movie
         *              required to display in the view
         */
        void onClick(Movie movie);
    }

    /**
     * Creates a {@link MoviesAdapter}.
     *
     * @param mClickHandler The on-click handler for this adapter. This single handler is called
     *                      when an item is clicked.
     */
    MoviesAdapter(MoviesAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    /**
     * This method is used to set or add movies data if we have already initialized the {@link MoviesAdapter}
     *
     * @param mMoviesData ArrayList of movie data
     * */
    void setOrAddMoviesData(ArrayList<Movie> mMoviesData) {
        if(this.mMoviesData == null){
            this.mMoviesData = mMoviesData;
        } else {
            this.mMoviesData.addAll(mMoviesData);
        }
        notifyDataSetChanged();
    }

    /**
     * Gets movies that are there in adapter
     *
     * @return {@link ArrayList<Movie>} moviesData
     * */
    ArrayList<Movie> getMoviesData() {
        return mMoviesData;
    }


    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_list_layout, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        String imageUrl = NetworkUtils.buildImageURL(mMoviesData.get(position).getImageUrl());
        Picasso.with(context)
                .load(imageUrl)
                .error(R.drawable.ic_broken_image_black)
                .placeholder(R.drawable.ic_image_black)
                .into(holder.mMoviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null) return 0;
        return mMoviesData.size();
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * Variable that stores the imageView in the item where we display the movie poster
         */
        private ImageView mMoviePosterImageView;

        /**
         * Initialises {@link MoviesAdapterViewHolder} with an item from our movies ArrayList
         *
         * @param itemView view for a movie item
         */
        MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mMoviePosterImageView = itemView.findViewById(R.id.iv_movie_poster);
        }

        /**
         * Method that gets called when a view in {@link RecyclerView} is clicked on
         *
         * @param v View that is clicked upon
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mMoviesData.get(adapterPosition));
        }
    }
}
