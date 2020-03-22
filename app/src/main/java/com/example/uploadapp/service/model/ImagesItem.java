package com.example.uploadapp.service.model;

import com.google.gson.annotations.SerializedName;

public class ImagesItem {

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("id")
    private String id;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return
                "ImagesItem{" +
                        "image_url = '" + imageUrl + '\'' +
                        ",id = '" + id + '\'' +
                        "}";
    }
}