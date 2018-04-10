package com.example.baobang.gameduangua.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by huuduc on 05/04/2018.
 */

public class NewCourse {
    @SerializedName("_id")
    private String courseID;

    @SerializedName("image")
    private String imgUrl;

    @SerializedName("tenKH")
    private String tenKH;

    @SerializedName("fee")
    private int fee;

    @SerializedName("description")
    private String description;

    @SerializedName("lessons")
    private List<String> lessonID;

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLessonID() {
        return lessonID;
    }

    public void setLessonID(List<String> lessonID) {
        this.lessonID = lessonID;
    }
}
