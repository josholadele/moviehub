package com.josholadele.moviehub;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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

        mMovieAdapter = new MovieAdapter(getAllItemList());

        mRecyclerView.setAdapter(mMovieAdapter);
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
            Toast.makeText(this, "Most Popular Movies", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.sort_top_rated) {
            Toast.makeText(this, "Top Rated Movies", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Movie> getAllItemList() {

        List<Movie> allItems = new ArrayList<Movie>();
        allItems.add(new Movie("United States", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("Canada", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("United Kingdom", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("Germany", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("Sweden", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("United Kingdom", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("Germany", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("Sweden", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("United States", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("Canada", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("United Kingdom", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("Germany", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("Sweden", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("United Kingdom", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("Germany", android.R.drawable.alert_dark_frame));
        allItems.add(new Movie("Sweden", android.R.drawable.alert_dark_frame));

        return allItems;
    }
}
