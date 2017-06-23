package com.example.kazhang.flicks.models;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kazhang.flicks.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
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

import static com.example.kazhang.flicks.MovieListActivity.API_BASE_URL;
import static com.example.kazhang.flicks.MovieListActivity.API_KEY_PARAM;

/**
 * Created by kazhang on 6/21/17.
 */

public class MovieDetailsActivity extends YouTubeBaseActivity {

    Movie movie;

    // instance fields
    AsyncHttpClient client;
    String videoId;

    // numeric code to identify current movie
    public final static int VIEW_TRAILER_CODE = 20;

    // view objects
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvOverview) TextView tvOverview;
    @BindView(R.id.rbVoteAverage) RatingBar rbVoteAverage;
    @BindView(R.id.player) YouTubePlayerView playerView;
//    @BindView(R.id.posterButton) ImageButton posterButton;

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

        // set button image to poster, loading using Glide
//        String imageUrl = getIntent().getStringExtra("BACKDROP_URL");
//        int placeholderId = R.drawable.flicks_backdrop_placeholder;

//        Glide.with(this)
//                .load(imageUrl)
//                .placeholder(placeholderId)
//                .error(placeholderId)
//                .into(posterButton);

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
                        setupVideoPlayer();
//                        setupViewListener();
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

    // set up Youtube player
    private void setupVideoPlayer() {
        // resolve the player view from the layout
        // YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.player);

        // initialize with API key stored in secrets.xml
        playerView.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                // do any work here to cue video, play video, etc.
                youTubePlayer.cueVideo(videoId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {
                // log the error
                Log.e("MovieTrailerActivity", "Error initializing YouTube player");
            }
        });
    }

    // listen for the movie poster being clicked and redirect afterward
//    private void setupViewListener() {
//        Log.i(TAG, "Setting up listener on movie poster");
//
//        posterButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                // create a new activity
//                Intent i = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
//
//                // pass the data being edited
//                i.putExtra(VIDEO_ID, videoId);
//
//                // display the activity
//                startActivityForResult(i, VIEW_TRAILER_CODE);
//            }
//        });
//
//    }

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
