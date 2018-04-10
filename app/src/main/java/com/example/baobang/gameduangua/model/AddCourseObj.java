package com.example.baobang.gameduangua.model;

/**
 * Created by huuduc on 10/04/2018.
 */

public class AddCourseObj {
    private String userEmail;

    public AddCourseObj() {
    }

    public AddCourseObj(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
