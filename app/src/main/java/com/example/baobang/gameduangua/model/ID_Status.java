package com.example.baobang.gameduangua.model;

/**
 * Created by huuduc on 10/04/2018.
 */

public class ID_Status {
    private String courseID;
    private int status;

    public ID_Status() {
    }

    public ID_Status(String courseID, int status) {
        this.courseID = courseID;
        this.status = status;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
