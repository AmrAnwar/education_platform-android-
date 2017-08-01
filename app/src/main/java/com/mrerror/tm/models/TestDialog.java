package com.mrerror.tm.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahmed on 30/07/17.
 */

public class TestDialog implements Parcelable {
    private String dialog;
    private String fSpeaker;
    private String sSpeaker;
    private String location;

    public TestDialog(String dialog, String fSpeaker, String sSpeaker, String location) {
        this.dialog = dialog;
        this.fSpeaker = fSpeaker;
        this.sSpeaker = sSpeaker;
        this.location = location;
    }

    protected TestDialog(Parcel in) {
        dialog = in.readString();
        fSpeaker = in.readString();
        sSpeaker = in.readString();
        location = in.readString();
    }

    public static final Creator<TestDialog> CREATOR = new Creator<TestDialog>() {
        @Override
        public TestDialog createFromParcel(Parcel in) {
            return new TestDialog(in);
        }

        @Override
        public TestDialog[] newArray(int size) {
            return new TestDialog[size];
        }
    };

    public String getDialog() {
        return dialog;
    }

    public String getfSpeaker() {
        return fSpeaker;
    }

    public String getsSpeaker() {
        return sSpeaker;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dialog);
        dest.writeString(fSpeaker);
        dest.writeString(sSpeaker);
        dest.writeString(location);
    }
}
