package com.sebhastian.popularmoviesdatabase.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sebhastian.popularmoviesdatabase.R;
import com.sebhastian.popularmoviesdatabase.model.Review;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Yonathan Sebhastian on 7/24/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final ArrayList<Review> mReviews;
    private final Callbacks mCallbacks;

    public ReviewAdapter(ArrayList<Review> reviews, Callbacks callbacks) {
        mReviews = reviews;
        mCallbacks = callbacks;
    }

    public ArrayList<Review> getReviews() {
        return mReviews;
    }

    public interface Callbacks {
        void read(Review review, int position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Review review = mReviews.get(position);

        holder.mReview = review;
        holder.mContentView.setText(review.getReviewContent());
        holder.mAuthorView.setText(review.getReviewAuthor());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.read(review, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public void add(List<Review> reviews) {
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.review_content)
        TextView mContentView;
        @BindView(R.id.review_author)
        TextView mAuthorView;
        public Review mReview;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }
}
