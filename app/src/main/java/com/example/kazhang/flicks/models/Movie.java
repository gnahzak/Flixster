package com.example.kazhang.flicks.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kazhang on 6/21/17.
 */

public class Movie {

    // values from the API
    private String title;
    private String overview;
    private String posterPath;

    // initialize object from JSON data
    public Movie(JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }
}
