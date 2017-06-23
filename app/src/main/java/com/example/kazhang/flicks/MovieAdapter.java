package com.example.kazhang.flicks;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kazhang.flicks.models.Config;
import com.example.kazhang.flicks.models.Movie;
import com.example.kazhang.flicks.models.MovieDetailsActivity;

import org.parceler.Parcels;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by kazhang on 6/21/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    // list of movies
    ArrayList<Movie> movies;

    // configuration for image URLs
    Config config;

    // context for rendering
    Context context;

    // initialize with list
    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    // creates and inflates a new view
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get context and create inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // create view using item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    // binds an inflated view to a new item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get movie data at the specified position
        Movie movie = movies.get(position);

        // populate view with movie data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());

        // determine which orientation to use
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        // build url for poster image
        String imageUrl = null;

        // load image depending on orientation
        if (isPortrait) {
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        }
        else {
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        }

        // find the correct placeholder and imageview for the orientation
        int placeholderId = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPortrait ? holder.ivPosterImage : holder.ivBackdropImage;

        // load image using Glide
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(imageView);

    }

    // returns the total number of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // create viewholder as a static inner class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        @BindView(R.id.ivPosterImage) ImageView ivPosterImage;
//        @Nullable
//        @BindView(R.id.ivBackdropImage) ImageView ivBackdropImage;
         ImageView ivPosterImage;
         ImageView ivBackdropImage;
        TextView tvTitle;
        TextView tvOverview;

        public ViewHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(this, itemView);

            // look up view objects by ID
             ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterImage);
             ivBackdropImage = (ImageView) itemView.findViewById(R.id.ivBackdropImage);
            tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            // get movie at that position in the list
            if (position != RecyclerView.NO_POSITION) {
                Movie movie = movies.get(position);
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));

                // store URL for backdrop image
                String backdrop_url = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
                intent.putExtra("BACKDROP_URL", backdrop_url);
                context.startActivity(intent);
            }
        }
    }
}
