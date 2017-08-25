package com.mrerror.tm.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahmed on 30/07/17.
 */

public class TestMistake implements Parcelable {
    private String sentence;
    private String wrong;
    private String correct;

    public TestMistake(String sentence, String wrong, String correct) {
        this.sentence = sentence;
        this.wrong = wrong;
        this.correct = correct;
    }

    protected TestMistake(Parcel in) {
        sentence = in.readString();
        wrong = in.readString();
        correct = in.readString();
    }

    public static final Creator<TestMistake> CREATOR = new Creator<TestMistake>() {
        @Override
        public TestMistake createFromParcel(Parcel in) {
            return new TestMistake(in);
        }

        @Override
        public TestMistake[] newArray(int size) {
            return new TestMistake[size];
        }
    };

    public String getSentence() {
        return sentence;
    }

    public String getWrong() {
        return wrong;
    }

    public String getCorrect() {
        return correct;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sentence);
        dest.writeString(wrong);
        dest.writeString(correct);
    }
}
