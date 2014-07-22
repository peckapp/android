package com.peck.android.listeners;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by mammothbane on 7/7/2014.
 */
public class ImagePickerListener implements View.OnClickListener {
    public final static int REQUEST_CODE = 5;

    @NonNull
    private Fragment fragment;

    public ImagePickerListener(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        fragment.startActivityForResult(intent, REQUEST_CODE);
    }
}
