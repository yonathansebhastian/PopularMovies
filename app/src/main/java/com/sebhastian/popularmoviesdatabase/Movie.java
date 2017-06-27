package com.sebhastian.popularmoviesdatabase;

/**
 * Created by Yonathan Sebhastian on 6/25/2017.
 */

public class Movie {
    private static final String IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private String originalTitle;
    private String movieId;
    private String imageUrl;
    private String overview;
    private Double voteAvg;
    private String releaseDate;

    public Movie(){
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Double getVoteAvg() {
        return voteAvg;
    }

    public void setVoteAvg(Double voteAvg) {
        this.voteAvg = voteAvg;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDetailedVoteAverage() {
        return String.valueOf(getVoteAvg()) + "/10";
    }

    public String getImageUrlFull(){
        return IMAGE_URL+getImageUrl();
    }

}
