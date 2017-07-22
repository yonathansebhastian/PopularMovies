package com.sebhastian.popularmoviesdatabase.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yonathan Sebhastian on 7/22/2017.
 */
public class Movies {

    @SerializedName("results")
    private List<Movie> movies;

    public List<Movie> getMovies() {
        return movies;
    }
}
