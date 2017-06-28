package com.sebhastian.popularmoviesdatabase;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yonathan Sebhastian on 6/25/2017.
 */

public class Movie implements Parcelable{
    private static final String IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private String originalTitle;
    private String imageUrl;
    private String overview;
    private String voteAvg;
    private String releaseDate;

    public Movie(){
    }

    protected Movie(Parcel in) {
        originalTitle = in.readString();
        imageUrl = in.readString();
        overview = in.readString();
        voteAvg = in.readString();
        releaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
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

    public String getVoteAvg() {
        return voteAvg+"/10";
    }

    public void setVoteAvg(String voteAvg) {
        this.voteAvg = voteAvg;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getImageUrlFull(){
        return IMAGE_URL+getImageUrl();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(originalTitle);
        parcel.writeString(imageUrl);
        parcel.writeString(overview);
        parcel.writeString(voteAvg);
        parcel.writeString(releaseDate);

    }
}
