package com.mrerror.tm.models;

import com.mrerror.tm.R;

import java.io.Serializable;

/**
 * Created by kareem on 7/24/2017.
 */

public class ModelAnswer implements Serializable {
    private String title;
    private String note;
    private String type;
    private String fileUrl;
    private int color;
    private int id;
    private String filePath;
    private Boolean dwonload;
    String fileExtention;
    String fileLocal;
    private boolean downloadingNow;


    public void setDownloadingNow(boolean downloadingNow) {
        this.downloadingNow = downloadingNow;
    }

    public boolean isDownloadingNow() {
        return downloadingNow;
    }

    public void setFileLocal(String fileLocal) {
        this.fileLocal = fileLocal;
    }

    public String getFileLocal() {
        return fileLocal;
    }

    public String getFileExtention() {
        return fileExtention;
    }

    public void setFileExtention(String fileExtention) {
        this.fileExtention = fileExtention;
    }

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
        if (getFilePath() == null) {

            dwonload = false;
        } else {
            dwonload = true;
        }

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

        if (getType().equals("Sheet")) {
            color = R.color.sheet;
        } else if (getType().equals("Exam")) {
            color = R.color.exam;
        }
        return color;
    }

    public String getFileName() {
        if (!fileUrl.equals("null")) return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        else {
            return getFilePath().substring(getFilePath().lastIndexOf('/') + 1);

        }
    }
}
