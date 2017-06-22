package com.example.kazhang.flicks.models;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kazhang.flicks.MovieTrailerActivity;
import com.example.kazhang.flicks.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static android.provider.MediaStore.Video.Thumbnails.VIDEO_ID;
import static com.example.kazhang.flicks.MovieListActivity.API_BASE_URL;
import static com.example.kazhang.flicks.MovieListActivity.API_KEY_PARAM;

/**
 * Created by kazhang on 6/21/17.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;

    // instance fields
    AsyncHttpClient client;
    String videoId;
    ImageButton posterButton;

    // numeric code to identify current movie
    public final static int VIEW_TRAILER_CODE = 20;

    // view objects
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvOverview) TextView tvOverview;
    @BindView(R.id.rbVoteAverage) RatingBar rbVoteAverage;

    // tag for all logging from this activity
    public final static String TAG = "MovieDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        // unwrap movie passed in via intent
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set title and overview from the movie
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        // initialize client
        client = new AsyncHttpClient();

        getVideoList();

        // set button image to poster
        //imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());)

    }

    // get list of videos related to the current movie
    private void getVideoList(){
        String url = API_BASE_URL + String.format("/movie/%s/videos", movie.getId());

        // set request parameters, including API key
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // load result into videoId; leave as empty string if there are no videos
                try {
                    JSONArray results = response.getJSONArray("results");

                    // determine whether there are videos or not
                    if (results.length() > 0) {
                        // return the key of the first video
                        videoId = results.getJSONObject(0).getString("key");
                        Log.i(TAG, String.format("The first video link is %s", videoId));

                        // start listening for clicks on the poster
                        posterButton = (ImageButton) findViewById(R.id.posterButton);
                        setupViewListener();
                    } else {
                        // leave key as empty and log a message
                        videoId = "";
                        Log.i(TAG, "No associated videos were found");
                    }
                } catch (JSONException e) {
                    logError("Failed to parse now playing movies", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now_playing endpoint", throwable, true);
            }
        });
    }

    // listen for the movie poster being clicked and redirect afterward
    private void setupViewListener() {
        Log.i(TAG, "Setting up listener on movie poster");

        posterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // create a new activity
                Intent i = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);

                // pass the data being edited
                i.putExtra(VIDEO_ID, videoId);

                // display the activity
                startActivityForResult(i, VIEW_TRAILER_CODE);
            }
        });

    }

    // handle errors, log, and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        // always log the error
        Log.e(TAG, message, error);

        // alert the user to avoid silent errors
        if (alertUser) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
