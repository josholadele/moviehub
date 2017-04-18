package com.josholadele.moviehub;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);
        mErrorTextView = (TextView) findViewById(R.id.error_loading);
        mContentLoading = (ProgressBar) findViewById(R.id.content_loading);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        fetchMovies(mMovieAdapter, "popular");

        mRecyclerView.setAdapter(mMovieAdapter);
    }

    private void fetchMovies(MovieAdapter adapter, String sortString) {
        new FetchMovieTask(adapter).execute(sortString);
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
            fetchMovies(mMovieAdapter, "popular");
            return true;
        }
        if (id == R.id.sort_top_rated) {
            fetchMovies(mMovieAdapter, "top_rated");
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
    public void onMovieClick(Movie movie) {
        startActivity(new Intent(this, MovieDetailActivity.class).putExtra("Movie", movie));
    }

    class FetchMovieTask extends AsyncTask<String, Void, String> {


        final String BASE_URL = "https://api.themoviedb.org/3/movie/";
        final String API_KEY = "api_key";
        final MovieAdapter movieAdapter;

        HttpURLConnection urlConnection = null;
        String movieJsonString = null;
        BufferedReader reader = null;

        public FetchMovieTask(MovieAdapter adapter){
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
                List<Movie> movieList = buildfromResult(result);
                if(movieList!= null && movieList.size() > 0){
                    showMovieDataView();
                    movieAdapter.setMovieData(movieList);
                }else {
                    showErrorMessage();
                }
            }else {
                showErrorMessage();
            }
        }

        private List<Movie> buildfromResult(String result) {
            final String ORIGINAL_TITLE = "original_title";
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";

            if (result == null || "".equals(result)) {
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
                        object.optString(VOTE_AVERAGE),
                        object.optString(RELEASE_DATE)));
            }
            return movieList;
        }
    }
}
