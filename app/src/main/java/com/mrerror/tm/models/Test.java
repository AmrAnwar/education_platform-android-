package com.mrerror.tm.models;

import org.json.JSONArray;

/**
 * Created by kareem on 7/30/2017.
 */

public class Test {
    private String title;
    private JSONArray choose;
    private JSONArray mistake;
    private JSONArray dialog;
    private JSONArray complete;

    public void setTitle(String title) {this.title = title;}

    public void setChoose(JSONArray choose) {this.choose = choose;}

    public void setComplete(JSONArray complete) {this.complete = complete;}

    public void setDialog(JSONArray dialog) {this.dialog = dialog;}

    public void setMistake(JSONArray mistake) {this.mistake = mistake;}

    public String getTitle() {return title;}

    public JSONArray getChoose() {return choose;}

    public JSONArray getComplete() {return complete;}

    public JSONArray getDialog() {return dialog;}

    public JSONArray getMistake() {return mistake;}
}

