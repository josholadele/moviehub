package com.josholadele.moviehub;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.josholadele.moviehub.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class MovieDetailActivity extends AppCompatActivity {

    ImageView movieIconBackground, movieIcon;
    ScrollView synopsisView;
    LinearLayout trailerLayout;
    LinearLayout reviewLayout;
    TextView averageRating;
    TextView releaseDate;
    TextView movieOverview;
    Movie movie;
    public String PosterBaseUrl = "http://image.tmdb.org/t/p/";
    public String PosterSize = "w342";
    JSONArray trailerArray;
    JSONArray reviewArray;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent parentIntent = getIntent();

        movie = null;
        if (parentIntent != null) {
            movie = parentIntent.getParcelableExtra("Movie");
        }

        trailerLayout = (LinearLayout) findViewById(R.id.trailer_frame);
        reviewLayout = (LinearLayout) findViewById(R.id.review);

        if (!isFavorite(movie)) {
            new FetchMovieDetails(movie).execute("videos");
        } else {
            try {
                trailerArray = new JSONArray(movie.trailerJson);
                showTrailers(trailerArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        new FetchMovieDetails(movie).execute("reviews");
        findViewById(R.id.toggle_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reviewLayout.getVisibility() == View.GONE || reviewLayout.getVisibility() == View.INVISIBLE) {
                    reviewLayout.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.toggle_review)).setText(getString(R.string.hide_review));
                } else {
                    reviewLayout.setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.toggle_review)).setText(getString(R.string.show_review));
                }
            }
        });
        movieIconBackground = (ImageView) findViewById(R.id.movie_icon);
        movieIcon = (ImageView) findViewById(R.id.movie_icon_small);
        synopsisView = (ScrollView) findViewById(R.id.synopsis);

        movieIconBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (synopsisView.getVisibility() == View.GONE || synopsisView.getVisibility() == View.INVISIBLE) {
                    synopsisView.setVisibility(View.VISIBLE);
                } else {
                    synopsisView.setVisibility(View.GONE);
                }
            }
        });
        averageRating = (TextView) findViewById(R.id.average_rating);
        releaseDate = (TextView) findViewById(R.id.release_date);
        movieOverview = (TextView) findViewById(R.id.overview);
        if (movie != null) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(movie.title);
            Picasso.with(this)
                    .load(PosterBaseUrl + PosterSize + movie.posterPath)
                    .into(movieIconBackground);
            Picasso.with(this)
                    .load(PosterBaseUrl + PosterSize + movie.posterPath)
                    .into(movieIcon);
            String rating = movie.voteAverage + getString(R.string.rating_score);
            averageRating.setText(rating);
            releaseDate.setText(movie.releaseDate);
            movieOverview.setText(movie.overview);
        }
    }

    private void showTrailers(final JSONArray trailerArray) {
        int noOfTrailers = trailerArray.length();
        findViewById(R.id.trailer_loading).setVisibility(View.GONE);
        for (int i = 0; i < noOfTrailers; i++) {
            View newView = getLayoutInflater().inflate(R.layout.trailer_button, trailerLayout, false);
            Button trailerButton = (Button) newView.findViewById(R.id.button);
            trailerButton.setText(trailerArray.optJSONObject(i).optString("name"));
            final String key = trailerArray.optJSONObject(i).optString("key");
            trailerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=" + key));
                    PackageManager manager = getPackageManager();
                    List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
                    if (infos.size() > 0) {
                        startActivity(intent);
                    }
                }
            });
            trailerLayout.addView(newView);
        }
    }

    private void showReviews(final JSONArray reviewArray) {
        int noOfReviews = reviewArray.length();
        View newView;
        for (int i = 0; i < noOfReviews; i++) {
            newView = getLayoutInflater().inflate(R.layout.review_item, reviewLayout, false);
            TextView author = (TextView) newView.findViewById(R.id.author);
            TextView content = (TextView) newView.findViewById(R.id.content);
            author.setText(reviewArray.optJSONObject(i).optString("author") + " :");
            content.setText(reviewArray.optJSONObject(i).optString("content"));
            reviewLayout.addView(newView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        mMenu = menu;

        if (isFavorite(movie)) {
            toggleFavorite(true, menu);
        } else {
            toggleFavorite(false, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mark_favorite) {
            if (trailerArray != null) {
                movie.trailerJson = trailerArray.toString();
            }
            ContentValues contentValues = buildContentValues(movie);

            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

            // Display the URI that's returned with a Toast
            // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
            if (uri != null) {
                Toast.makeText(getBaseContext(), "Added to favorites", Toast.LENGTH_LONG).show();
                toggleFavorite(true, mMenu);
            }
        } else if (id == R.id.unmark_favorite) {
            String where = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?";
            String[] whereArgs = new String[]{Integer.toString(movie.movieId)};
            int deletedRow = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, where, whereArgs);
            if (deletedRow > 0) {
                Toast.makeText(getBaseContext(), "Removed from favorites", Toast.LENGTH_LONG).show();
                toggleFavorite(false, mMenu);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private ContentValues buildContentValues(Movie movie) {
        ContentValues values = new ContentValues();

        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.movieId);
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.overview);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.posterPath);
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.releaseDate);
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.title);
        values.put(MovieContract.MovieEntry.COLUMN_TRAILERS, movie.trailerJson);
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.voteAverage);
        return values;
    }

    class FetchMovieDetails extends AsyncTask<String, Void, String> {

        final static String BASE_URL = "https://api.themoviedb.org/3/movie/";
        final static String API_KEY = "api_key";
        final Movie movie;
        String fetchOption;

        HttpURLConnection urlConnection = null;

        FetchMovieDetails(Movie movie) {
            this.movie = movie;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            if (strings.length == 0) {
                return null;
            }
            fetchOption = strings[0];
            Uri uri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath(String.valueOf(movie.movieId))
                    .appendEncodedPath(strings[0])
                    .appendQueryParameter(API_KEY, API.API_KEY)
                    .build();
            URL url;

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
            buildDetailResults(result, fetchOption);
        }
    }

    void buildDetailResults(String result, String fetchOption) {
        try {
            JSONObject trailerObject = new JSONObject(result);
            if ("videos".equals(fetchOption)) {
                trailerArray = trailerObject.optJSONArray("results");
                showTrailers(trailerArray);
            } else if ("reviews".equals(fetchOption)) {
                reviewArray = trailerObject.optJSONArray("results");
                showReviews(reviewArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFavorite(boolean add, Menu menu) {
        if (add) {
            menu.findItem(R.id.unmark_favorite).setVisible(true);
            menu.findItem(R.id.mark_favorite).setVisible(false);
        } else {
            menu.findItem(R.id.unmark_favorite).setVisible(false);
            menu.findItem(R.id.mark_favorite).setVisible(true);
        }
    }

    private boolean isFavorite(Movie movie) {
        String where = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?";
        String[] whereArgs = new String[]{Integer.toString(movie.movieId)};
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, where, whereArgs, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                movie.trailerJson = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TRAILERS));
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
}
