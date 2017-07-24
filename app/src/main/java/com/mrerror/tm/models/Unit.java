package com.mrerror.tm.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ahmed on 24/07/17.
 */

public class Unit implements Parcelable{
    private String title;
    private HashMap<String,ArrayList<Word>> parts ;

    public Unit(String title ,HashMap<String,ArrayList<Word>> parts ){
        this.title = title;
        this.parts = parts;
    }
    protected Unit(Parcel in) {
        title = in.readString();
        this.parts = (HashMap<String, ArrayList<Word>>) in.readSerializable();
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
        dest.writeSerializable(this.parts);
    }

    public String getTitle() {
        return title;
    }

    public HashMap<String, ArrayList<Word>> getParts() {
        return parts;
    }
}
