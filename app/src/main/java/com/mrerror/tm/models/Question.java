package com.mrerror.tm.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahmed on 25/07/17.
 */

public class Question implements Parcelable {
    private String question;
    private String answer;
    private String linkForImg;
    private String linkForRec;
    private String date;

    public Question(String question, String answer, String img, String rec,String date) {
        this.question = question;
        this.answer = answer;
        this.linkForImg = img;
        this.linkForRec = rec;
        this.date=TimeAndDate.timeHandeler(date);
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getLinkForImg() {
        return linkForImg;
    }

    public String getLinkForRec() {
        return linkForRec;
    }
    public String getDate(){return date;}

    protected Question(Parcel in) {
        question = in.readString();
        answer = in.readString();
        linkForImg = in.readString();
        linkForRec = in.readString();
        date=in.readString();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(answer);
        dest.writeString(linkForImg);
        dest.writeString(linkForRec);
        dest.writeString(date);
    }
}
