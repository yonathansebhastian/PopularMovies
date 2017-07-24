package com.sebhastian.popularmoviesdatabase.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.sebhastian.popularmoviesdatabase.Constants;

/**
 * Created by Yonathan Sebhastian on 7/22/2017.
 */

public class Trailer implements Parcelable {
    @SerializedName("id")
    private String trailerId;
    @SerializedName("key")
    private String trailerKey;
    @SerializedName("name")
    private String trailerName;
    @SerializedName("site")
    private String trailerSite;
    @SerializedName("size")
    private String trailerSize;

    protected Trailer(Parcel in) {
        trailerId = in.readString();
        trailerKey = in.readString();
        trailerName = in.readString();
        trailerSite = in.readString();
        trailerSize = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public String getTrailerUrl() {
        return Constants.YOUTUBE_PREFIX + trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public void setTrailerName(String trailerName) {
        this.trailerName = trailerName;
    }

    public String getTrailerSite() {
        return trailerSite;
    }

    public void setTrailerSite(String trailerSite) {
        this.trailerSite = trailerSite;
    }

    public String getTrailerSize() {
        return trailerSize;
    }

    public void setTrailerSize(String trailerSize) {
        this.trailerSize = trailerSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(trailerId);
        parcel.writeString(trailerKey);
        parcel.writeString(trailerName);
        parcel.writeString(trailerSite);
        parcel.writeString(trailerSize);

    }
}
