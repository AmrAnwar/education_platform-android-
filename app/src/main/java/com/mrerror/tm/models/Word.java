package com.mrerror.tm.models;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by ahmed on 24/07/17.
 */

public class Word implements Parcelable {
    private String word;
    private String translation;
    public JSONArray mUsers;

    SharedPreferences sp;
   private int mWordId;


    public int getWordId() {
        return mWordId;
    }

    public boolean ismHasFav() {
for (int i=0;i<mUsers.length();i++){
    int id=0;
    try {
      id  = (int) mUsers.get(i);
    } catch (JSONException e) {
        e.printStackTrace();
    }
    if(sp.getInt("id",0)==id){

     return  true;
    }
}


        return false;
    }

    public Word(String word, String translation , JSONArray users,int wordId,SharedPreferences sharedprefrences) {
        this.word = word;
        this.translation = translation;
        mUsers=users;
        mWordId=wordId;
        sp=sharedprefrences;
    }

    public String getWord() {
        return word;
    }

    public String getTranslation() {
        return translation;
    }

    protected Word(Parcel in) {
        word = in.readString();
        translation = in.readString();
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(word);
        dest.writeString(translation);
    }
}
