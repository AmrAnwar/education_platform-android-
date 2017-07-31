package com.mrerror.tm.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahmed on 30/07/17.
 */

public class TestComplete implements Parcelable{
    private String sentence;
    private String answer;

    public TestComplete(String sentence, String answer) {
        this.sentence = sentence;
        this.answer = answer;
    }

    protected TestComplete(Parcel in) {
        sentence = in.readString();
        answer = in.readString();
    }

    public static final Creator<TestComplete> CREATOR = new Creator<TestComplete>() {
        @Override
        public TestComplete createFromParcel(Parcel in) {
            return new TestComplete(in);
        }

        @Override
        public TestComplete[] newArray(int size) {
            return new TestComplete[size];
        }
    };

    public String getSentence() {
        return sentence;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sentence);
        dest.writeString(answer);
    }
}
