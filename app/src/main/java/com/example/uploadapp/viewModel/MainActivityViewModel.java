package com.example.uploadapp.viewModel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.uploadapp.service.repo.Events;
import com.example.uploadapp.service.repo.Repository;

import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivityViewModel extends AndroidViewModel {

    private MutableLiveData<Events.UploadResponseEvent> uploadResponseEventMutableLiveData;
    private MutableLiveData<Events.ImagesResponseEvent> imagesResponseEventMutableLiveData;
    private Repository repository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        uploadResponseEventMutableLiveData = new MutableLiveData<>();
        imagesResponseEventMutableLiveData = new MutableLiveData<>();
        repository = new Repository();
    }

    public void uploadRequest(byte[] imageBytes) {
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
        MultipartBody.Part part = MultipartBody.Part.createFormData("imageData", UUID.randomUUID().toString() + ".png", fileReqBody);
        repository.uploadImage(part, uploadResponseEventMutableLiveData);
    }

    public LiveData<Events.UploadResponseEvent> getUploadResponse() {
        return uploadResponseEventMutableLiveData;
    }

    public void sendRequest() {
        repository.getImages(imagesResponseEventMutableLiveData);
    }

    public LiveData<Events.ImagesResponseEvent> getImagesResponse() {
        return imagesResponseEventMutableLiveData;
    }
}
