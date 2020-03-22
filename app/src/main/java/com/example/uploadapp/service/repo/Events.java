package com.example.uploadapp.service.repo;


import com.example.uploadapp.service.model.ImageResponseModel;
import com.example.uploadapp.service.model.UploadResponseModel;
import com.example.uploadapp.service.model.ErrorModel;

public class Events {

    private Events() {
    }

    public static class BaseEvent {
        private final ErrorModel errorModel;
        private final boolean success;

        BaseEvent(ErrorModel errorModel, boolean success) {
            this.errorModel = errorModel;
            this.success = success;
        }

        public ErrorModel getErrorModel() {
            return errorModel;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public static class UploadResponseEvent extends BaseEvent {
        private final UploadResponseModel uploadResponseModel;

        UploadResponseEvent(ErrorModel errorModel, boolean success, UploadResponseModel uploadResponseModel) {
            super(errorModel, success);
            this.uploadResponseModel = uploadResponseModel;
        }

        public UploadResponseModel getUploadResponseModel() {
            return uploadResponseModel;
        }
    }

    public static class ImagesResponseEvent extends BaseEvent {
        private final ImageResponseModel imageResponseModel;

        ImagesResponseEvent(ErrorModel errorModel, boolean success, ImageResponseModel imageResponseModel) {
            super(errorModel, success);
            this.imageResponseModel = imageResponseModel;
        }

        public ImageResponseModel getImageResponseModel() {
            return imageResponseModel;
        }
    }
}
