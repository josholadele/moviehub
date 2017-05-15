package com.josholadele.moviehub;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Oladele on 4/16/17.
 */
public class Movie implements Parcelable {

    public String title;
    public String posterPath;
    public String overview;
    public int movieId;
    public String voteAverage;
    public String releaseDate;
    public String trailerJson;

    protected Movie(Parcel in) {
        title = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        movieId = in.readInt();
        voteAverage = in.readString();
        releaseDate = in.readString();
        trailerJson = in.readString();
    }

    public Movie() {

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

    public Movie(String title, String posterPath, String overview, int movieId, String voteAverage, String releaseDate, String trailerJson) {
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.movieId = movieId;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.trailerJson = trailerJson;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeInt(movieId);
        parcel.writeString(voteAverage);
        parcel.writeString(releaseDate);
        parcel.writeString(trailerJson);
    }
}
