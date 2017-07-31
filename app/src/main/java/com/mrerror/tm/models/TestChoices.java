package com.mrerror.tm.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahmed on 30/07/17.
 */

public class TestChoices implements Parcelable{
    private String question;
    private String choice1;
    private String choice2;
    private String choice3;
    private String answer;

    public TestChoices(String question, String choice1, String choice2, String choice3, String answer) {
        this.question = question;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.answer = answer;
    }

    protected TestChoices(Parcel in) {
        question = in.readString();
        choice1 = in.readString();
        choice2 = in.readString();
        choice3 = in.readString();
        answer = in.readString();
    }

    public static final Creator<TestChoices> CREATOR = new Creator<TestChoices>() {
        @Override
        public TestChoices createFromParcel(Parcel in) {
            return new TestChoices(in);
        }

        @Override
        public TestChoices[] newArray(int size) {
            return new TestChoices[size];
        }
    };

    public String getQuestion() {
        return question;
    }

    public String getChoice1() {
        return choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public String getChoice3() {
        return choice3;
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
        dest.writeString(question);
        dest.writeString(choice1);
        dest.writeString(choice2);
        dest.writeString(choice3);
        dest.writeString(answer);
    }
}
