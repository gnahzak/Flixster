package com.example.kazhang.flicks.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kazhang on 6/21/17.
 */

public class Config {

    String imageBaseUrl;
    String posterSize;
    String backdropSize;

    public Config(JSONObject object) throws JSONException {
        // get image base URL and poster size from response
        JSONObject images = object.getJSONObject("images");
        imageBaseUrl = images.getString("secure_base_url");
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");

        // use option at index 3 as a default
        posterSize = posterSizeOptions.optString(3, "w342");

        // parse backdrop sizes and use the first option, or a fallback
        JSONArray backdropSizeOptions = images.getJSONArray("backdrop_sizes");
        backdropSize = backdropSizeOptions.optString(1, "w780");
    }

    // helper method for creating URLs
    public String getImageUrl(String size, String path) {
        return String.format("%s%s%s", imageBaseUrl, size, path);
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }

    public String getBackdropSize() {
        return backdropSize;
    }
}
