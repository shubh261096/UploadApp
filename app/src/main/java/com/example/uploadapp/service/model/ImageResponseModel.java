package com.example.uploadapp.service.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ImageResponseModel {

    @SerializedName("images")
    private List<ImagesItem> images;

    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    public void setImages(List<ImagesItem> images) {
        this.images = images;
    }

    public List<ImagesItem> getImages() {
        return images;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isError() {
        return error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return
                "ImageResponseModel{" +
                        "images = '" + images + '\'' +
                        ",error = '" + error + '\'' +
                        ",message = '" + message + '\'' +
                        "}";
    }
}