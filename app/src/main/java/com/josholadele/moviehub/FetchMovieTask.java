package com.josholadele.moviehub;

import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.josholadele.moviehub.MainActivity.showErrorMessage;
import static com.josholadele.moviehub.MainActivity.showMovieDataView;

class FetchMovieTask extends AsyncTask<String, Void, String> {

    final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    final String API_KEY = "api_key";
    final MovieAdapter movieAdapter;
    ProgressBar mContentLoading;
    List<Movie> movieList;

    HttpURLConnection urlConnection = null;

    public FetchMovieTask(MovieAdapter adapter, ProgressBar mContentLoading, List<Movie> movieList) {
        movieAdapter = adapter;
        this.mContentLoading = mContentLoading;
        this.movieList = movieList;
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
            movieList = buildFromResult(result);
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