package com.josholadele.moviehub;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Oladele on 5/12/17.
 */

public class FetchTrailerTask extends AsyncTask<String, Void, String> {
    final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    final String API_KEY = "api_key";
    final Movie movie;

    HttpURLConnection urlConnection = null;

    public FetchTrailerTask(Movie movie) {
        this.movie = movie;
    }


    @Override
    protected String doInBackground(String... strings) {

        if (strings.length == 0) {
            return null;
        }

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(String.valueOf(movie.movieId))
                .appendEncodedPath("videos")
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
//        buildTrailerResults(result);
    }
}
