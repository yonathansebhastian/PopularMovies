package com.sebhastian.popularmoviesdatabase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.sebhastian.popularmoviesdatabase.R;
import com.sebhastian.popularmoviesdatabase.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Yonathan Sebhastian on 6/25/2017.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {
    public MovieAdapter(Context context, List<Movie> movie) {
        super(context, 0, movie);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movies_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();

        Picasso.with(getContext()).load(movie.getImageUrlFull()).placeholder(R.drawable.placeholder).into(viewHolder.imageView);
        return convertView;
    }

    public static class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.movie_poster);
        }
    }
}
