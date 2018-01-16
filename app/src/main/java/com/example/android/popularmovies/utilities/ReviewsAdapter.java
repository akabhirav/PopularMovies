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
        View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ReviewsAdapter.ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder holder, int position) {
        String review = reviews.get(position);
        if(review.length() > 200){
            review = review.substring(0, 200) + "...";
        }
        holder.mText.setText(review);
    }

    @Override
    public int getItemCount() {
        if(reviews == null) return 0;
        return reviews.size();
    }

    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView mText;

        ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            mText = itemView.findViewById(android.R.id.text1);
        }
    }
}
