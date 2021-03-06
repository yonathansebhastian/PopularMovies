package com.sebhastian.popularmoviesdatabase.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sebhastian.popularmoviesdatabase.R;
import com.sebhastian.popularmoviesdatabase.db.MovieContract;
import com.sebhastian.popularmoviesdatabase.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yonathan Sebhastian on 6/25/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final ArrayList<Movie> mMovies;
    private final Callbacks mCallbacks;

    public MovieAdapter(ArrayList<Movie> movies, Callbacks callbacks) {
        mMovies = movies;
        mCallbacks = callbacks;
    }


    public ArrayList<Movie> getMovies() {
        return mMovies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movies_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.clean();
    }

    @Override
    public void onBindViewHolder(final MovieAdapter.ViewHolder holder, int position) {
        final Movie movie = mMovies.get(position);
        final Context context = holder.mView.getContext();

        holder.mMovie = movie;

        String posterUrl = movie.getImageUrlFull();

        Picasso.with(context)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder)
                .into(holder.mMoviePoster,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                if (holder.mMovie.getId() != movie.getId()) {
                                    holder.clean();
                                } else {
                                    holder.mMoviePoster.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onError() {
                            }
                        }
                );

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.detail(movie, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void add(List<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    public void add(Cursor cursor) {
        mMovies.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(MovieContract.MovieEntry.COLUMN_MOVIE_ID_INDEX);
                String title = cursor.getString(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE_INDEX);
                String posterPath = cursor.getString(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH_INDEX);
                String overview = cursor.getString(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW_INDEX);
                String rating = cursor.getString(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE_INDEX);
                String releaseDate = cursor.getString(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE_INDEX);
                Movie movie = new Movie(id, title, posterPath, overview, rating, releaseDate);
                mMovies.add(movie);
            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }

    public interface Callbacks {
        void detail(Movie movie, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        @BindView(R.id.movie_poster)
        ImageView mMoviePoster;
        private Movie mMovie;

        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }

        private void clean() {
            final Context context = mView.getContext();
            Picasso.with(context).cancelRequest(mMoviePoster);
            mMoviePoster.setImageBitmap(null);
            mMoviePoster.setVisibility(View.INVISIBLE);
        }

    }


}
