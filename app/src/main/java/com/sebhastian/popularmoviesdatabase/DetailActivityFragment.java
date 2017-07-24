package com.sebhastian.popularmoviesdatabase;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sebhastian.popularmoviesdatabase.adapter.ReviewAdapter;
import com.sebhastian.popularmoviesdatabase.adapter.TrailerAdapter;
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

public class DetailActivityFragment extends Fragment implements ReviewAdapter.Callbacks, TrailerAdapter.Callbacks {
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

            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerViewTrailerList.setLayoutManager(layoutManager);
            mTrailerAdapter = new TrailerAdapter(new ArrayList<Trailer>(), this);
            mRecyclerViewTrailerList.setAdapter(mTrailerAdapter);
            mRecyclerViewTrailerList.setNestedScrollingEnabled(false);

            if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_TRAILERS)) {
                List<Trailer> trailers = savedInstanceState.getParcelableArrayList(SAVED_TRAILERS);
                mTrailerAdapter.add(trailers);
//                mButtonWatchTrailer.setEnabled(true);
            }
            else {
                fetchTrailers(mMovie.getId());
            }

            mReviewAdapter = new ReviewAdapter(new ArrayList<Review>(), this);
            mRecyclerViewReviewList.setAdapter(mReviewAdapter);
            if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_REVIEWS)) {
                List<Review> reviews = savedInstanceState.getParcelableArrayList(SAVED_REVIEWS);
                mReviewAdapter.add(reviews);
//                mButtonWatchTrailer.setEnabled(true);
            }
            else {
                fetchReviews(mMovie.getId());
            }

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
            Log.e(LOG_TAG, trailer.getTrailerUrl());
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie.getOriginalTitle()+" - "+ trailer.getTrailerName() + ": "
                    + trailer.getTrailerUrl());
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}
