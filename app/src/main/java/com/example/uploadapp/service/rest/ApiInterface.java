package com.example.uploadapp.service.rest;

import com.example.uploadapp.service.model.ImageResponseModel;
import com.example.uploadapp.service.model.UploadResponseModel;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @Multipart
    @POST("teacher/Syllabus/add")
    Call<UploadResponseModel> uploadImage(@Part MultipartBody.Part image);

    @GET("teacher/Syllabus")
    Call<ImageResponseModel> getImages();

}