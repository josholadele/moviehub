package com.josholadele.moviehub;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Oladele on 4/16/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private List<Movie> theList;
    private Context context;
    private MovieClickListener movieClickListener;

    public static String PosterBaseUrl = "http://image.tmdb.org/t/p/";
    public static String PosterSize = "w185";


    public TrailerAdapter(MovieClickListener clickListener) {
        this.movieClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(context)
                .load(PosterBaseUrl + PosterSize + theList.get(position).posterPath)
                .into(holder.movieIcon);
    }

    public void setMovieData(List<Movie> movies) {
        this.theList = movies;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (theList == null) return 0;
        return theList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView movieIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            movieIcon = (ImageView) itemView.findViewById(R.id.movie_icon);
            movieIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    movieClickListener.onMovieClick(theList.get(getAdapterPosition()));
                }
            });
        }
    }

    interface MovieClickListener {
        void onMovieClick(Movie movie);
    }
}
