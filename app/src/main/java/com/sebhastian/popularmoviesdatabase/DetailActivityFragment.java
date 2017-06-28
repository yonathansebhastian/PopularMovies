package com.sebhastian.popularmoviesdatabase;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Yonathan Sebhastian on 6/27/2017.
 */

public class DetailActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    public Movie movie;
    public TextView mTitleTextView,mOverviewTextView,mRatingTextView,mReleaseDateTextView;
    public ImageView mPosterImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mTitleTextView = (TextView)rootView.findViewById(R.id.movie_title);
        mOverviewTextView = (TextView) rootView.findViewById(R.id.movie_desc);
        mRatingTextView = (TextView) rootView.findViewById(R.id.movie_rating);
        mReleaseDateTextView = (TextView) rootView.findViewById(R.id.movie_release_date);
        mPosterImageView = (ImageView) rootView.findViewById(R.id.movie_poster);

        movie = getActivity().getIntent().getExtras().getParcelable("MOVIE_OBJ");
        Log.d(LOG_TAG, "movie"+movie.toString());
        if (movie != null){
            mTitleTextView.setText(movie.getOriginalTitle());
            mOverviewTextView.setText(movie.getOverview());
            mRatingTextView.setText(movie.getVoteAvg());
            mReleaseDateTextView.setText(movie.getReleaseDate());
            Picasso.with(getActivity()).load(movie.getImageUrlFull()).placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder).into(mPosterImageView);
        }
        return rootView;
    }

}
