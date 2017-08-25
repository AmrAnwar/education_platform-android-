package com.mrerror.tm.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahmed on 25/07/17.
 */

public class QuestionForStaff implements Parcelable {
    private String question;
    private String answer;
    private String linkToEdit;
    private String linkToDelete;
    private String username;
    private String studentImg;
    private String studentRec;

    public QuestionForStaff(String question, String answer, String linkToEdit, String linkToDelete, String username,
                            String stImg, String stRec) {
        this.question = question;
        this.answer = answer;
        this.linkToEdit = linkToEdit;
        this.linkToDelete = linkToDelete;
        this.username = username;
        this.studentImg = stImg;
        this.studentRec = stRec;
    }

    public String getQuestion() {
        return question;
    }

    public String getStudentImg() {
        return studentImg;
    }

    public String getStudentRec() {
        return studentRec;
    }

    public String getAnswer() {
        return answer;
    }

    public String getLinkToEdit() {
        return linkToEdit;
    }

    public String getLinkToDelete() {
        return linkToDelete;
    }

    public String getUsername() {
        return username;
    }

    protected QuestionForStaff(Parcel in) {
        question = in.readString();
        answer = in.readString();
        linkToEdit = in.readString();
        linkToDelete = in.readString();
        username = in.readString();
        studentImg = in.readString();
        studentRec = in.readString();
    }

    public static final Creator<QuestionForStaff> CREATOR = new Creator<QuestionForStaff>() {
        @Override
        public QuestionForStaff createFromParcel(Parcel in) {
            return new QuestionForStaff(in);
        }

        @Override
        public QuestionForStaff[] newArray(int size) {
            return new QuestionForStaff[size];
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
        dest.writeString(linkToEdit);
        dest.writeString(linkToDelete);
        dest.writeString(username);
        dest.writeString(studentImg);
        dest.writeString(studentRec);
    }
}
