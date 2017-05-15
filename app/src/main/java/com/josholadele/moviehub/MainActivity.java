package com.josholadele.moviehub;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.josholadele.moviehub.data.MovieContract;

import java.util.ArrayList;
import java.util.List;

import static com.josholadele.moviehub.R.menu.movie;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener {

    private static RecyclerView mRecyclerView;
    private static TextView mErrorTextView;
    private ProgressBar mContentLoading;
    private MovieAdapter mMovieAdapter;
    private static String sortString;
    public static final String SORT_FAVORITE = "favorite";
    public static final String SORT_TOP_RATED = "top_rated";
    public static final String SORT_POPULAR = "popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sortString = SORT_POPULAR;
        if (savedInstanceState != null) {
            sortString = savedInstanceState.getString("sort_string");
        }
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);
        mErrorTextView = (TextView) findViewById(R.id.error_loading);
        mContentLoading = (ProgressBar) findViewById(R.id.content_loading);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        fetchMovies(mMovieAdapter, sortString);

        mRecyclerView.setAdapter(mMovieAdapter);
    }

    private void fetchMovies(MovieAdapter adapter, String sortString) {
        if (sortString.equalsIgnoreCase(SORT_FAVORITE)) {
            List<Movie> movieList = loadFavorites();
            if (movieList != null) {
                if (movieList.size() > 0) {
                    showMovieDataView();
                    adapter.setMovieData(movieList);
                } else {
                    Toast.makeText(this, "You currently do not have any favorite movie", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            new FetchMovieTask(adapter, mContentLoading, movieList).execute(sortString);
        }
    }

    private List<Movie> loadFavorites() {
        Cursor cursor = new CursorLoader(this,
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null).loadInBackground();

        List<Movie> movieList = null;
        if (cursor != null) {
            movieList = new ArrayList<>();

            while (cursor.moveToNext()) {

                String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)),
                        poster = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)),
                        overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)),
                        voteAverage = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)),
                        trailerJson = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TRAILERS)),
                        releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
                int movieId = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));

                movieList.add(new Movie(title, poster, overview, movieId, voteAverage, releaseDate, trailerJson));
            }
            cursor.close();
        }
        return movieList;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(movie, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_most_popular) {
            sortString = SORT_POPULAR;
            fetchMovies(mMovieAdapter, SORT_POPULAR);
            return true;
        }
        if (id == R.id.sort_top_rated) {
            sortString = SORT_TOP_RATED;
            fetchMovies(mMovieAdapter, SORT_TOP_RATED);
            return true;
        }
        if (id == R.id.sort_favorites) {
            sortString = SORT_FAVORITE;
            fetchMovies(mMovieAdapter, SORT_FAVORITE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void showMovieDataView() {
        mErrorTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public static void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("sort_string", sortString);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    public List<Movie> movieList;

    @Override
    public void onMovieClick(Movie movie) {
        startActivity(new Intent(this, MovieDetailActivity.class).putExtra("Movie", movie));
    }

}
