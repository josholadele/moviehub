package com.josholadele.moviehub;

import org.json.JSONObject;

/**
 * Created by Oladele on 4/16/17.
 */
public class Movie {

    private String title;
    private String posterPath;
    private String overview;
    private String voteAverage;
    private String releaseDate;

    public Movie(String title, String posterPath, String overview, String voteAverage, String releaseDate) {
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public Movie(String title, int oda) {
        this.title = title;
    }

    public Movie(JSONObject movieJson) {
        this.title = movieJson.optString("");
        this.posterPath = movieJson.optString("");
        this.overview = movieJson.optString("");
        this.voteAverage = movieJson.optString("");
        this.releaseDate = movieJson.optString("");
    }
}
