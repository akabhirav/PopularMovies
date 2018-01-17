package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {
    ArrayList<String> reviews;
    public ReviewsAdapter(ArrayList<String> reviews){
        this.reviews = reviews;
    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.review_list_layout, parent, false);
        return new ReviewsAdapter.ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder holder, int position) {
        String review = reviews.get(position);
        if(review.length() > 200){
            review = review.substring(0, 200) + "...";
        }
        holder.mReview.setText(review);
        if(position + 1 == reviews.size()) holder.separator.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if(reviews == null) return 0;
        return reviews.size();
    }

    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView mReview;
        View separator;

        ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            mReview = itemView.findViewById(R.id.tv_review);
            separator = itemView.findViewById(R.id.v_review_separator);
        }
    }
}
