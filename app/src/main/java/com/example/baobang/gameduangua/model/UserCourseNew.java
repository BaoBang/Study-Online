package com.example.baobang.gameduangua.model;

import com.google.gson.annotations.SerializedName;

/*
 * Created by baobang on 4/4/18.
 */

public class UserCourseNew {
    @SerializedName("status")
    private Integer status;
    @SerializedName("_id")
    private String _id;
    @SerializedName("courseId")
    private NewCourse course;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public NewCourse getCourse() {
        return course;
    }

    public void setCourse(NewCourse course) {
        this.course = course;
    }
}
