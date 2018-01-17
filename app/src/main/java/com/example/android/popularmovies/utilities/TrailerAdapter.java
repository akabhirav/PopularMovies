package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder>  {
    private ArrayList<String> videoIds;

    public TrailerAdapter(ArrayList<String> urls) {
        videoIds = urls;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.trailer_list_layout, parent, false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        holder.mTrailerTextView.setText(String.format("Trailer %s", position + 1));
        holder.mTrailerTextView.setTag(position);
        if(position + 1 == videoIds.size()) holder.separator.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if (videoIds == null) return 0;
        return videoIds.size();
    }

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTrailerTextView;
        private View separator;
        Context context;

        TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
            mTrailerTextView = itemView.findViewById(R.id.tv_trailer);
            separator = itemView.findViewById(R.id.v_trailer_separator);
        }

        @Override
        public void onClick(View v) {
            int position = (int) mTrailerTextView.getTag();
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + videoIds.get(position)));
            if(webIntent.resolveActivity(context.getPackageManager()) != null){
                context.startActivity(webIntent);
            }
        }
    }
}
