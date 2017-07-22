package com.sebhastian.popularmoviesdatabase.network;

import com.sebhastian.popularmoviesdatabase.model.Movies;
import com.sebhastian.popularmoviesdatabase.model.Reviews;
import com.sebhastian.popularmoviesdatabase.model.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Yonathan Sebhastian on 7/22/2017.
 */

public interface NetworkService {
    @GET("discover/movie?")
    Call<Movies> getMovies(@Query("sort_by") String sortBy);

    @GET("movie/{id}/reviews?")
    Call<Reviews> getMovieReviews(@Path("id") String id);

    @GET("movie/{id}/videos?")
    Call<Trailers> getMovieTrailers(@Path("id") String id);
}
