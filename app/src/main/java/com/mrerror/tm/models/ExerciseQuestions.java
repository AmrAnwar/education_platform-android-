package com.mrerror.tm.models;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by kareem on 9/12/2017.
 */

public class ExerciseQuestions {

    private int id ;
    private String question;
    private String answer;
    private int type;
    private SharedPreferences sp;
    private JSONArray mUsers;



    public void setmUsers(JSONArray mUsers) {this.mUsers = mUsers;}

    public void setSp(SharedPreferences sp) {this.sp = sp;}

    public void setId(int id) {
        this.id = id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getType() {
        return type;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean ismHasFav() {
        if(mUsers==null){return false;}
        else {
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
        }
        return false;
    }

}
