package com.mrerror.tm.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahmed on 29/07/17.
 */

public class Part implements Parcelable {
    private String title;
    private String wordsUrl;
    private String testUrl;

    public Part(String title, String wordsUrl, String testUrl) {
        this.title = title;
        this.wordsUrl = wordsUrl;
        this.testUrl = testUrl;
    }

    protected Part(Parcel in) {
        title = in.readString();
        wordsUrl = in.readString();
        testUrl = in.readString();
    }

    public static final Creator<Part> CREATOR = new Creator<Part>() {
        @Override
        public Part createFromParcel(Parcel in) {
            return new Part(in);
        }

        @Override
        public Part[] newArray(int size) {
            return new Part[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getWordsUrl() {
        return wordsUrl;
    }

    public String getTestUrl() {
        return testUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(wordsUrl);
        dest.writeString(testUrl);
    }
}
