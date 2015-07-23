package com.example.android.project1.app;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by Admin-HHE on 7/19/2015.
 */
public class MovieDetails implements Parcelable {
    String[] mDetails;

    public MovieDetails(String[] mDetails) {
        this.mDetails = mDetails.clone();
    }

    private MovieDetails(Parcel in) {
        mDetails = in.createStringArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(mDetails);
    }

    public static final Parcelable.Creator<MovieDetails> CREATOR = new Parcelable.Creator<MovieDetails>() {
        @Override
        public MovieDetails createFromParcel(Parcel source) {
            return new MovieDetails(source);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    @Override
    public String toString() {
        return Arrays.toString(mDetails);
    }
}