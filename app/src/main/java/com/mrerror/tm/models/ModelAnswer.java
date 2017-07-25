package com.mrerror.tm.models;

import com.mrerror.tm.R;

/**
 * Created by kareem on 7/24/2017.
 */

public class ModelAnswer {
    private   String title;
    private  String note;
    private String type;
    private String fileUrl;
    private  int color;
    private int id;
    private String filePath;
    private Boolean dwonload;

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public Boolean getDwonload() {
        if(getFilePath()==null){

            dwonload=false;
        }else {dwonload= true;}

        return dwonload;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getNote() {
        return note;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public int getColor() {

        if(getType().equals("Sheet")){
            color= R.color.sheet;
        }else if(getType().equals("Exam"))
        { color=R.color.exam;}
        return color;
    }
}
