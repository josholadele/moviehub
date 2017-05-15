package com.josholadele.moviehub;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener {

    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private ProgressBar mContentLoading;
    private MovieAdapter mMovieAdapter;
    private static String sortString;
    public static final String SORT_FAVORITE = "favorite";
    public static final String SORT_TOP_RATED = "top_rated";
    public static final String SORT_POPULAR = "popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        sortString = SORT_POPULAR;
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
            new FetchMovieTask(adapter).execute(sortString);
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
        fetchMovies(mMovieAdapter, sortString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie, menu);
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

    private void showMovieDataView() {
        mErrorTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("sort_string", sortString);
    }

    @Override
    public void onMovieClick(Movie movie) {
        startActivity(new Intent(this, MovieDetailActivity.class).putExtra("Movie", movie));
    }

    class FetchMovieTask extends AsyncTask<String, Void, String> {

        final String BASE_URL = "https://api.themoviedb.org/3/movie/";
        final String API_KEY = "api_key";
        final MovieAdapter movieAdapter;

        HttpURLConnection urlConnection = null;

        public FetchMovieTask(MovieAdapter adapter) {
            movieAdapter = adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mContentLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            if (strings.length == 0) {
                return null;
            }

            Uri uri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath(strings[0])
                    .appendQueryParameter(API_KEY, API.API_KEY)
                    .build();
            URL url = null;

            try {
                url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            } finally {
                urlConnection.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mContentLoading.setVisibility(View.GONE);
            if (result != null && !result.equals("")) {
                List<Movie> movieList = buildFromResult(result);
                if (movieList != null && movieList.size() > 0) {
                    showMovieDataView();
                    movieAdapter.setMovieData(movieList);
                } else {
                    showErrorMessage();
                }
            } else {
                showErrorMessage();
            }
        }

        private List<Movie> buildFromResult(String result) {
            final String ORIGINAL_TITLE = "original_title";
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String ID = "id";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";
//            final String RELEASE_DATE = "release_date";

            if (result == null || result.equals("")) {
                return null;
            }

            JSONObject movieJSONObject;
            JSONArray movieJSONArray = null;
            try {
                movieJSONObject = new JSONObject(result);
                movieJSONArray = movieJSONObject.optJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            List<Movie> movieList = new ArrayList<>();


            for (int i = 0; i < movieJSONArray.length(); i++) {
                JSONObject object = movieJSONArray.optJSONObject(i);

                movieList.add(new Movie(object.optString(ORIGINAL_TITLE),
                        object.optString(POSTER_PATH),
                        object.optString(OVERVIEW),
                        object.optInt(ID),
                        object.optString(VOTE_AVERAGE),
                        object.optString(RELEASE_DATE), ""));
            }
            return movieList;
        }
    }
}
