package com.sebhastian.popularmoviesdatabase.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.sebhastian.popularmoviesdatabase.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Yonathan Sebhastian on 6/25/2017.
 */

public class Movie implements Parcelable{
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @SerializedName("id")
    private String id;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("poster_path")
    private String imageUrl;
    @SerializedName("overview")
    private String overview;
    @SerializedName("vote_average")
    private String voteAvg;
    @SerializedName("release_date")
    private String releaseDate;

    protected Movie(Parcel in) {
        id = in.readString();
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


    public String getImageUrl() {
        return imageUrl;
    }


    public String getOverview() {
        return overview;
    }

    public String getVoteAvg() {
        return voteAvg+"/"+Constants.RATING_MAX;
    }


    public String getReleaseDate() {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String date;
        try {
            Date newDate = format.parse(releaseDate);
            // from string to date
            format = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            // back to string again
            date = format.format(newDate);
        } catch (ParseException e) {
            return "TBD";
        }
        return date;
    }

    public String getImageUrlFull(){
        return Constants.IMAGE_BASE_URL+Constants.IMAGE_SMALL_SIZE+getImageUrl();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(originalTitle);
        parcel.writeString(imageUrl);
        parcel.writeString(overview);
        parcel.writeString(voteAvg);
        parcel.writeString(releaseDate);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
