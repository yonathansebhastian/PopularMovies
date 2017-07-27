package com.sebhastian.popularmoviesdatabase;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sebhastian.popularmoviesdatabase.adapter.ReviewAdapter;
import com.sebhastian.popularmoviesdatabase.adapter.TrailerAdapter;
import com.sebhastian.popularmoviesdatabase.db.MovieContract;
import com.sebhastian.popularmoviesdatabase.model.Movie;
import com.sebhastian.popularmoviesdatabase.model.Review;
import com.sebhastian.popularmoviesdatabase.model.Reviews;
import com.sebhastian.popularmoviesdatabase.model.Trailer;
import com.sebhastian.popularmoviesdatabase.model.Trailers;
import com.sebhastian.popularmoviesdatabase.network.NetworkService;
import com.sebhastian.popularmoviesdatabase.network.ServiceGenerator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yonathan Sebhastian on 6/27/2017.
 */

public class DetailActivityFragment extends Fragment implements ReviewAdapter.Callbacks, TrailerAdapter.Callbacks{
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public static final String SAVED_TRAILERS = "SAVED_TRAILERS";
    public static final String SAVED_REVIEWS = "SAVED_REVIEWS";

    public Movie mMovie;
    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;
    private ShareActionProvider mShareActionProvider;
    private NetworkService networkService;

    @BindView(R.id.movie_title)
    TextView mTitleTextView;
    @BindView(R.id.movie_desc)
    TextView mOverviewTextView;
    @BindView(R.id.movie_rating)
    TextView mRatingTextView;
    @BindView(R.id.movie_release_date)
    TextView mReleaseDateTextView;
    @BindView(R.id.movie_poster)
    ImageView mPosterImageView;
    @BindView(R.id.trailer_list)
    RecyclerView mRecyclerViewTrailerList;
    @BindView(R.id.review_list)
    RecyclerView mRecyclerViewReviewList;
    @BindView(R.id.button_watch_trailer)
    Button mButtonWatchTrailer;
    @BindView(R.id.button_mark_as_favorite)
    Button mButtonMarkAsFavorite;
    @BindView(R.id.button_remove_from_favorites)
    Button mButtonRemoveFavorite;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Trailer> trailers = mTrailerAdapter.getTrailers();
        if (trailers != null && !trailers.isEmpty()) {
            outState.putParcelableArrayList(SAVED_TRAILERS, trailers);
        }

        ArrayList<Review> reviews = mReviewAdapter.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            outState.putParcelableArrayList(SAVED_REVIEWS, reviews);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        mMovie = getActivity().getIntent().getExtras().getParcelable("MOVIE_OBJ");
        networkService = ServiceGenerator.createService(NetworkService.class);
        if (mMovie != null){
            mTitleTextView.setText(mMovie.getOriginalTitle());
            mOverviewTextView.setText(mMovie.getOverview());
            mRatingTextView.setText(mMovie.getVoteAvg());
            mReleaseDateTextView.setText(mMovie.getReleaseDate());
            Picasso.with(getActivity()).load(mMovie.getImageUrlFull()).placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder).into(mPosterImageView);

            mTrailerAdapter = new TrailerAdapter(new ArrayList<Trailer>(), this);
            mRecyclerViewTrailerList.setAdapter(mTrailerAdapter);
            mRecyclerViewTrailerList.setNestedScrollingEnabled(false);

            if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_TRAILERS)) {
                List<Trailer> trailers = savedInstanceState.getParcelableArrayList(SAVED_TRAILERS);
                mTrailerAdapter.add(trailers);
                mButtonWatchTrailer.setEnabled(true);
            }
            else {
                fetchTrailers(mMovie.getId());
            }

            mReviewAdapter = new ReviewAdapter(new ArrayList<Review>(), this);
            mRecyclerViewReviewList.setAdapter(mReviewAdapter);
            if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_REVIEWS)) {
                List<Review> reviews = savedInstanceState.getParcelableArrayList(SAVED_REVIEWS);
                mReviewAdapter.add(reviews);
            }
            else {
                fetchReviews(mMovie.getId());
            }

            mButtonWatchTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mTrailerAdapter.getItemCount() > 0) {
                        watch(mTrailerAdapter.getTrailers().get(0), 0);
                    }
                }
            });

            mButtonMarkAsFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    markAsFavorite();
                }
            });

            mButtonRemoveFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeFavorite();
                }
            });

            updateFavoriteButtons();

        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);
        MenuItem shareTrailerMenuItem = menu.findItem(R.id.share_trailer);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareTrailerMenuItem);
    }

    private void fetchReviews(String id) {
        Call<Reviews> reviewsCall = networkService.getMovieReviews(id);

        reviewsCall.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                List<Review> reviewList = response.body().getReviews();
                mReviewAdapter.add(reviewList);
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                Toast.makeText(getActivity(), getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchTrailers(String id){
        Call<Trailers> trailersCall = networkService.getMovieTrailers(id);
        trailersCall.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                List<Trailer> trailerList = response.body().getTrailers();
                mTrailerAdapter.add(trailerList);
                updateShareActionProvider();
                mButtonWatchTrailer.setEnabled(true);
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                Toast.makeText(getActivity(), getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void read(Review review, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(review.getReviewUrl())));
    }

    @Override
    public void watch(Trailer trailer, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getTrailerUrl())));
    }

    private void updateShareActionProvider() {
        if (mTrailerAdapter.getItemCount()>0){
            Trailer trailer = mTrailerAdapter.getTrailers().get(0);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie.getOriginalTitle()+" - "+ trailer.getTrailerName() + ": "
                    + trailer.getTrailerUrl());
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    public void markAsFavorite(){

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getActivity(), R.string.added_to_favorites_message, Toast.LENGTH_SHORT).show();
                updateFavoriteButtons();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                if (!isFavorite()) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                            mMovie.getId());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                            mMovie.getOriginalTitle());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
                            mMovie.getImageUrl());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                            mMovie.getOverview());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                            mMovie.getVoteAvg());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                            mMovie.getReleaseDate());
                    getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean isFavorite() {
        Cursor movieCursor = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId(),
                null,
                null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            return false;
        }
    }

    private void removeFavorite() {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                if (isFavorite()) {
                    getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId(), null);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getActivity(), getString(R.string.remove_from_favorites_message), Toast.LENGTH_SHORT).show();
                updateFavoriteButtons();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void updateFavoriteButtons() {
        if (isFavorite()) {
            mButtonRemoveFavorite.setVisibility(View.VISIBLE);
            mButtonMarkAsFavorite.setVisibility(View.GONE);
        } else {
            mButtonMarkAsFavorite.setVisibility(View.VISIBLE);
            mButtonRemoveFavorite.setVisibility(View.GONE);
        }
    }
}
