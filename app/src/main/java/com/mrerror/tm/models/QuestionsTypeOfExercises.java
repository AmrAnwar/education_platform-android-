package com.mrerror.tm.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kareem on 9/13/2017.
 */

public class QuestionsTypeOfExercises implements Parcelable {


    String mTypeQuestion;
    int mType;

    public QuestionsTypeOfExercises(String type, int stype){

        mTypeQuestion =type;
mType=stype;
    }
    public int getmType() {
        return mType;
    }

    public String getmTypeQuestion() {
        return mTypeQuestion;
    }

    public void setmTypeQuestion(String mTypeQuestion) {
        this.mTypeQuestion = mTypeQuestion;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    protected QuestionsTypeOfExercises(Parcel in) {
 this.mTypeQuestion=in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTypeQuestion);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QuestionsTypeOfExercises> CREATOR = new Creator<QuestionsTypeOfExercises>() {
        @Override
        public QuestionsTypeOfExercises createFromParcel(Parcel in) {
            return new QuestionsTypeOfExercises(in);
        }

        @Override
        public QuestionsTypeOfExercises[] newArray(int size) {
            return new QuestionsTypeOfExercises[size];
        }
    };
}
