package com.example.baobang.gameduangua.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by huuduc on 10/04/2018.
 */

public class AddCourse {

    @SerializedName("role")
    private int role;

    @SerializedName("gender")
    private boolean gender;

    @SerializedName("course")
    private List<String> listCourse;

    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("birthday")
    private String birthday;

    public AddCourse() {
    }

    public AddCourse(int role, boolean gender, List<String> listCourse, String id, String name, String email, String birthday) {
        this.role = role;
        this.gender = gender;
        this.listCourse = listCourse;
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public List<String> getListCourse() {
        return listCourse;
    }

    public void setListCourse(List<String> listCourse) {
        this.listCourse = listCourse;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
