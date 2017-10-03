package com.mrerror.tm.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahmed on 30/07/17.
 */

public class TestChoices implements Parcelable {
    private String question;
    private String choice1;
    private String choice2;
    private String choice3;
    private String answer;
    private String user_answer;

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    public void setChoice3(String choice3) {
        this.choice3 = choice3;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setUser_answer(String user_answer) {
        this.user_answer = user_answer;
    }

    public String getUser_answer() {
        return user_answer;
    }

    protected TestChoices(Parcel in) {
        question = in.readString();
        choice1 = in.readString();
        choice2 = in.readString();
        choice3 = in.readString();
        answer = in.readString();
        user_answer = in.readString();
    }
    public TestChoices(){

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
        dest.writeString(user_answer);
    }
}
