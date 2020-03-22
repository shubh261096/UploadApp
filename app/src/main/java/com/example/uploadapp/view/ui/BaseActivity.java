package com.example.uploadapp.view.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uploadapp.view.receiver.NetworkChangeReceiver;
import com.google.android.material.snackbar.Snackbar;

public abstract class BaseActivity extends AppCompatActivity {

    private static Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observeInternetChange();
    }

    private void observeInternetChange() {
        NetworkChangeReceiver.networkChange.observe(this, status -> {
            if (status != null) {
                getNetworkStatus(status);
                if (!status) {
                    snackbar = Snackbar.make(findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                } else {
                    if (snackbar != null) {
                        if (snackbar.isShown()) {
                            snackbar.dismiss();
                        }
                    }
                }
            }
        });
    }

    protected abstract void getNetworkStatus(boolean status);

}
