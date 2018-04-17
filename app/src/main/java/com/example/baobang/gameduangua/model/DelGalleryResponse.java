package com.example.baobang.gameduangua.model;

import com.google.gson.annotations.SerializedName;

public class DelGalleryResponse {

    @SerializedName("statuscode")
    private int statuscode;

    @SerializedName("results")
    private Gallery gallery;

    public DelGalleryResponse(int statuscode, Gallery gallery) {
        this.statuscode = statuscode;
        this.gallery = gallery;
    }

    public int getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(int statuscode) {
        this.statuscode = statuscode;
    }

    public Gallery getGallery() {
        return gallery;
    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }
}
