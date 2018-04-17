package com.example.baobang.gameduangua.model;

import com.google.gson.annotations.SerializedName;

public class NewGallery {

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String photUrl;

    @SerializedName("description")
    private String description;

    public NewGallery() {
    }

    public NewGallery(String name, String photUrl, String description) {
        this.name = name;
        this.photUrl = photUrl;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotUrl() {
        return photUrl;
    }

    public void setPhotUrl(String photUrl) {
        this.photUrl = photUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
