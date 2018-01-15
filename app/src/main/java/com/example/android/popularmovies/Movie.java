package com.example.android.popularmovies;


import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private int id;
    private String title, releaseDate, imageUrl, overview;
    private double voteAverage;

    /**
     * Initialises a movie object
     *
     * @param id id of the movie from tmdb
     * @param title title of the movie
     * @param imageUrl Url of the movie poster
     * @param releaseDate release date of the movie
     * @param overview synopsis of the movie
     * @param voteAverage rating of the movie*
     * */
    public Movie(int id, String title, String imageUrl, String releaseDate, String overview, double voteAverage) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.id = id;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.voteAverage = voteAverage;
    }

    /**
     * Makes movie object from parcel
     *
     * @param in parcel which stores the movie object
     * */
    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        releaseDate = in.readString();
        imageUrl = in.readString();
        overview = in.readString();
        voteAverage = in.readDouble();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /**
     * @return unique image url to append
     * */
    String getImageUrl() {
        return imageUrl;
    }

    /**
     * @return unique image url to append
     * */
    String getTitle() {
        return title;
    }

    /**
     * @return rating for the movie
     * */
    Double getVoteAverage() {
        return voteAverage;
    }

    /**
     * @return synopsis for the movie
     * */
    String getOverview() {
        return overview;
    }

    /**
     * @return release date for the movie
     * */
    String getReleaseDate() {
        return releaseDate;
    }

    /**
     * @return get tmdb id for the movie
     * */
    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(imageUrl);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
    }
}
