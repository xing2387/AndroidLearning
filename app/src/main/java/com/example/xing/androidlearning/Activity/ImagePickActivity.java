package com.example.xing.androidlearning.Activity;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.xing.androidlearning.ImageDataUtil;
import com.example.xing.androidlearning.ImageListPresenter;
import com.example.xing.androidlearning.LoadingDialog;
import com.example.xing.androidlearning.R;
import com.example.xing.androidlearning.fragment.ImagePickerFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class ImagePickActivity extends AppCompatActivity implements ImageListPresenter.OnListUpdatedListener {
    private final String TAG = "ImagePickActivity";

    private ImageListPresenter mImageListPresenter;
    private LoadingDialog mLoadingDialog;

    public static final String TAG_ImageFolderFragment = "ImageFolderFragment";
    public static final String TAG_ImageFragment = "ImageFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        mLoadingDialog = new LoadingDialog(this);

        mImageListPresenter = new ImageListPresenter(this);
        mImageListPresenter.updateList();
    }

    public void showDialog() {
        mLoadingDialog.show();
    }

    public void dismissDialog() {
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onListUpdated(HashMap<String, ImageDataUtil.ImageFolderInfo> imageCollection) {
        ArrayList<String> firstPicList = mImageListPresenter.getFirstPicOfEachFolder();
        Log.d(TAG, "add folder fragmet, 1st of " + firstPicList.size() + (firstPicList.size() > 0 ? ": " + firstPicList.get(0) : ""));
        getFragmentManager().beginTransaction().add(R.id.imagepicker_container,
                ImagePickerFragment.newInstance("", firstPicList), TAG_ImageFolderFragment)//.commit();
                .addToBackStack(TAG_ImageFolderFragment).commit();
    }

    public ImageListPresenter getmImageListPresenter() {
        return mImageListPresenter;
    }


}
