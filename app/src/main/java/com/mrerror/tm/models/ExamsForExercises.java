package com.mrerror.tm.models;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by kareem on 9/13/2017.
 */

public class ExamsForExercises extends ExpandableGroup<QuestionsTypeOfExercises> {


    int mId;

    public int getmId() {
        return mId;
    }

    public ExamsForExercises(String title, List<QuestionsTypeOfExercises> items , int id) {
        super(title, items);
        mId=id;
    }
}
