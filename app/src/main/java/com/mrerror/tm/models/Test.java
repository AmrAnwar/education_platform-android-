package com.mrerror.tm.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ahmed on 30/07/17.
 */

public class Test implements Parcelable {
    private String title;
    private ArrayList<TestChoices> testChoicesList;
    private ArrayList<TestComplete> testCompleteList;
    private ArrayList<TestDialog> testDialogList;
    private ArrayList<TestMistake> testMistakeList;

    public Test(String title, ArrayList<TestChoices> testChoicesList, ArrayList<TestComplete> testCompleteList,
                ArrayList<TestDialog> testDialogList, ArrayList<TestMistake> testMistakeList) {
        this.title = title;
        this.testChoicesList = testChoicesList;
        this.testCompleteList = testCompleteList;
        this.testDialogList = testDialogList;
        this.testMistakeList = testMistakeList;
    }


    protected Test(Parcel in) {
        title = in.readString();
        testChoicesList = in.createTypedArrayList(TestChoices.CREATOR);
        testCompleteList = in.createTypedArrayList(TestComplete.CREATOR);
        testDialogList = in.createTypedArrayList(TestDialog.CREATOR);
        testMistakeList = in.createTypedArrayList(TestMistake.CREATOR);
    }

    public static final Creator<Test> CREATOR = new Creator<Test>() {
        @Override
        public Test createFromParcel(Parcel in) {
            return new Test(in);
        }

        @Override
        public Test[] newArray(int size) {
            return new Test[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public ArrayList<TestChoices> getTestChoicesList() {
        return testChoicesList;
    }

    public ArrayList<TestComplete> getTestCompleteList() {
        return testCompleteList;
    }

    public ArrayList<TestDialog> getTestDialogList() {
        return testDialogList;
    }

    public ArrayList<TestMistake> getTestMistakeList() {
        return testMistakeList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeTypedList(testChoicesList);
        dest.writeTypedList(testCompleteList);
        dest.writeTypedList(testDialogList);
        dest.writeTypedList(testMistakeList);
    }
}
