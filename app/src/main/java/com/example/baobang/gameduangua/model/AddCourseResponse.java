package com.example.baobang.gameduangua.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by huuduc on 10/04/2018.
 */

public class AddCourseResponse extends BaseResponse {
    @SerializedName("results")
    private AddCourse addCourse;

    public AddCourseResponse(int statuscode) {
        super(statuscode);
    }

    public AddCourse getAddCourse() {
        return addCourse;
    }

    public void setAddCourse(AddCourse addCourse) {
        this.addCourse = addCourse;
    }
}
