package com.josholadele.moviehub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    ImageView movieIcon;
    TextView averageRating;
    TextView releaseDate;
    TextView movieOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent parentIntent = getIntent();

        Movie movie = null;
        if (parentIntent != null) {
            movie = parentIntent.getParcelableExtra("Movie");
        }

        movieIcon = (ImageView) findViewById(R.id.movie_icon);
        averageRating = (TextView) findViewById(R.id.average_rating);
        releaseDate = (TextView) findViewById(R.id.release_date);
        movieOverview = (TextView) findViewById(R.id.overview);
        if (movie != null) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(movie.title);
            Picasso.with(this)
                    .load(MovieAdapter.PosterBaseUrl + MovieAdapter.PosterSize + movie.posterPath)
                    .into(movieIcon);
            String rating = movie.voteAverage + getString(R.string.rating_score);
            averageRating.setText(rating);
            releaseDate.setText(movie.releaseDate);
            movieOverview.setText(movie.overview);
        }
    }
}
