package com.example.uploadapp.service.repo;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.uploadapp.service.model.ErrorModel;
import com.example.uploadapp.service.model.ImageResponseModel;
import com.example.uploadapp.service.model.UploadResponseModel;
import com.example.uploadapp.service.rest.ApiClient;
import com.example.uploadapp.service.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.uploadapp.utils.AppConstants.ErrorConstants.BAD_REQUEST_ERROR;
import static com.example.uploadapp.utils.AppConstants.ErrorConstants.SERVER_ERROR;
import static com.example.uploadapp.utils.AppConstants.ErrorConstants.SOCKET_ERROR;
import static com.example.uploadapp.utils.AppConstants.ErrorConstants.UNKNOWN_ERROR;


public class Repository {
    private static ApiInterface apiService;

    public Repository() {
        apiService = ApiClient.getClient().create(ApiInterface.class);
    }

    /* Upload Image Request */
    public void uploadImage(MultipartBody.Part image, MutableLiveData<Events.UploadResponseEvent> uploadResponseEventMutableLiveData) {
        apiService.uploadImage(image)
                .enqueue(new Callback<UploadResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<UploadResponseModel> call, @Nullable Response<UploadResponseModel> response) {
                        if (response != null) {
                            if (response.isSuccessful()) {
                                uploadResponseEventMutableLiveData.
                                        postValue(new Events.UploadResponseEvent(null, true, response.body()));
                            } else {
                                if (response.errorBody() != null) {
                                    uploadResponseEventMutableLiveData.
                                            postValue(new Events.UploadResponseEvent(buildErrorModel(response.code(), response.errorBody()), false, null));
                                } else {
                                    ErrorModel errorModel = new ErrorModel();
                                    errorModel.setMessage(UNKNOWN_ERROR);
                                    uploadResponseEventMutableLiveData.
                                            postValue(new Events.UploadResponseEvent(errorModel, false, null));
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UploadResponseModel> call, @NonNull Throwable t) {
                        String errorMsg = handleFailureResponse(t);
                        ErrorModel errorModel = new ErrorModel();
                        errorModel.setMessage(errorMsg);
                        uploadResponseEventMutableLiveData.
                                postValue(new Events.UploadResponseEvent(errorModel, false, null));
                    }
                });
    }

    /* Get Image Request */
    public void getImages(MutableLiveData<Events.ImagesResponseEvent> imagesResponseEventMutableLiveData) {
        apiService.getImages()
                .enqueue(new Callback<ImageResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<ImageResponseModel> call, @Nullable Response<ImageResponseModel> response) {
                        if (response != null) {
                            if (response.isSuccessful()) {
                                imagesResponseEventMutableLiveData.
                                        postValue(new Events.ImagesResponseEvent(null, true, response.body()));
                            } else {
                                if (response.errorBody() != null) {
                                    imagesResponseEventMutableLiveData.
                                            postValue(new Events.ImagesResponseEvent(buildErrorModel(response.code(), response.errorBody()), false, null));
                                } else {
                                    ErrorModel errorModel = new ErrorModel();
                                    errorModel.setMessage(UNKNOWN_ERROR);
                                    imagesResponseEventMutableLiveData.
                                            postValue(new Events.ImagesResponseEvent(errorModel, false, null));
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ImageResponseModel> call, @NonNull Throwable t) {
                        String errorMsg = handleFailureResponse(t);
                        ErrorModel errorModel = new ErrorModel();
                        errorModel.setMessage(errorMsg);
                        imagesResponseEventMutableLiveData.
                                postValue(new Events.ImagesResponseEvent(errorModel, false, null));
                    }
                });
    }


    /* Handle Retrofit Response Failure */
    private String handleFailureResponse(Throwable throwable) {
        if (throwable instanceof IOException) {
            return SOCKET_ERROR;
        } else {
            return UNKNOWN_ERROR;
        }
    }

    private ErrorModel buildErrorModel(int responseCode, ResponseBody body) {
        Gson gson = new GsonBuilder().create();
        ErrorModel mError = new ErrorModel();
        try {
            mError = gson.fromJson(body.string(), ErrorModel.class);
        } catch (Exception e) {
            if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST && responseCode < HttpURLConnection.HTTP_INTERNAL_ERROR) {
                mError.setMessage(BAD_REQUEST_ERROR);
            } else if (responseCode >= HttpURLConnection.HTTP_INTERNAL_ERROR && responseCode < (HttpURLConnection.HTTP_INTERNAL_ERROR + 100)) {
                mError.setMessage(SERVER_ERROR);
            } else {
                mError.setMessage(UNKNOWN_ERROR);
            }
        }
        return mError;
    }

}
