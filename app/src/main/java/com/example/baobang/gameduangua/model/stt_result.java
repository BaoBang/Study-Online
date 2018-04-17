package com.example.baobang.gameduangua.model;

import com.google.gson.annotations.SerializedName;

public class stt_result extends BaseResponse{

    @SerializedName("results")
    private String results;

    public stt_result(int statuscode) {
        super(statuscode);
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }
}
