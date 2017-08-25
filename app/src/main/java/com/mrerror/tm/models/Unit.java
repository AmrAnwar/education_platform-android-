package com.mrerror.tm.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ahmed on 24/07/17.
 */

public class Unit implements Parcelable {
    private String title;
    private ArrayList<Part> parts;

    public Unit(String title, ArrayList<Part> parts) {
        this.title = title;
        this.parts = parts;
    }

    protected Unit(Parcel in) {
        title = in.readString();
        this.parts = in.readArrayList(null);
    }

    public static final Creator<Unit> CREATOR = new Creator<Unit>() {
        @Override
        public Unit createFromParcel(Parcel in) {
            return new Unit(in);
        }

        @Override
        public Unit[] newArray(int size) {
            return new Unit[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeList(this.parts);
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Part> getParts() {
        return parts;
    }
}
