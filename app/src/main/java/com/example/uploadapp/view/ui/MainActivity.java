package com.example.uploadapp.view.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uploadapp.R;
import com.example.uploadapp.service.model.ImageResponseModel;
import com.example.uploadapp.service.model.ImagesItem;
import com.example.uploadapp.service.model.UploadResponseModel;
import com.example.uploadapp.utils.TouchImageView;
import com.example.uploadapp.view.adapter.ImageListAdapter;
import com.example.uploadapp.view.receiver.NetworkChangeReceiver;
import com.example.uploadapp.viewModel.MainActivityViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.uploadapp.utils.CommonUtils.convertBitmapToByteArray;
import static com.example.uploadapp.utils.CommonUtils.hideProgress;
import static com.example.uploadapp.utils.CommonUtils.showInformativeDialog;
import static com.example.uploadapp.utils.CommonUtils.showProgress;
import static com.example.uploadapp.utils.CommonUtils.showSettingsDialog;

public class MainActivity extends BaseActivity implements ImageListAdapter.OnItemClickListener {

    private static final int REQUEST_IMAGE = 100;

    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;
    @BindView(R.id.rvImage)
    RecyclerView rvImage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.txtNoData)
    TextView txtNoData;

    private MainActivityViewModel mainActivityViewModel;
    private List<ImagesItem> imagesItemList;
    private ImageListAdapter imageListAdapter;
    private NetworkChangeReceiver networkChangeReceiver;
    private boolean networkStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        /* Starting observer of Internet change */
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);


        // Clearing older images from cache directory
        // don't call this line if you want to choose multiple images in the same activity
        // call this once the bitmap(s) usage is over
        ImagePickerActivity.clearCache(this);

        observeUpload();
        observeImages();

        imagesItemList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvImage.setLayoutManager(layoutManager);
        imageListAdapter = new ImageListAdapter(imagesItemList, this);
        rvImage.setAdapter(imageListAdapter);

    }

    @Override
    public void getNetworkStatus(boolean status) {
        this.networkStatus = status;
        if (status) {
            if (imageListAdapter != null) {
                imageListAdapter.clearData();
            }
            subscribe();
        }
    }

    private void subscribe() {
        txtNoData.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        mainActivityViewModel.sendRequest();
    }

    private void observeImages() {
        mainActivityViewModel.getImagesResponse().observe(this, responseEvent -> {
            progressBar.setVisibility(View.GONE);
            if (responseEvent != null) {
                /* Clearing data */
                if (imageListAdapter != null) {
                    imageListAdapter.clearData();
                }
                if (responseEvent.isSuccess()) {
                    ImageResponseModel imageResponseModel = responseEvent.getImageResponseModel();
                    if (!imageResponseModel.isError()) {
                        List<ImagesItem> imagesItems = imageResponseModel.getImages();
                        /* Adding those data to list */
                        imagesItemList.addAll(imagesItems);
                        imageListAdapter.notifyDataSetChanged();
                    } else {
                        txtNoData.setVisibility(View.VISIBLE);
                        txtNoData.setText(imageResponseModel.getMessage());
                    }
                } else {
                    showInformativeDialog(this, responseEvent.getErrorModel().getMessage());
                }
            }
        });
    }


    private void subscribeUploadImage(byte[] imageData) {
        showProgress(this, getString(R.string.msg_wait_upload_image));
        mainActivityViewModel.uploadRequest(imageData);
    }

    private void observeUpload() {
        mainActivityViewModel.getUploadResponse().observe(this, responseEvent -> {
            hideProgress();
            if (responseEvent != null) {
                if (responseEvent.isSuccess()) {
                    UploadResponseModel commonResponseModel = responseEvent.getUploadResponseModel();
                    Toast.makeText(this, commonResponseModel.getMessage(), Toast.LENGTH_LONG).show();
                    subscribe();
                } else {
                    showInformativeDialog(this, responseEvent.getErrorModel().getMessage());
                }
            }
        });
    }


    @OnClick(R.id.fab_add)
    public void onViewClicked() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog(MainActivity.this);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(MainActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(MainActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = null;
                if (data != null) {
                    uri = data.getParcelableExtra("path");
                }
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    if (networkStatus) {
                        subscribeUploadImage(convertBitmapToByteArray(bitmap));
                    } else {
                        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onItemClick(int position) {
        showImage(position);
    }


    private void showImage(int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_fullimage_dialog, null);
        dialogBuilder.setView(dialogView);

        TouchImageView touchImageView = dialogView.findViewById(R.id.fullImage);
        touchImageView.setMaxZoom(4f);
        Picasso.get()
                .load(imagesItemList.get(position).getImageUrl())
                .into(touchImageView);
        AlertDialog alertDialog = dialogBuilder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

}

