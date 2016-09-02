package com.example.xing.androidlearning;

import android.app.Activity;

import com.example.xing.androidlearning.Activity.ImagePickActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ImageListPresenter implements ImageDataUtil.OnQueryResultListener {

    private ImagePickActivity mActivity;
    private HashMap<String, ImageDataUtil.ImageFolderInfo> mImageCollection;
    private ImageDataUtil mImageDataUtil;
//    private OnListUpdatedListener onListUpdatedListener;


    public ImageListPresenter(Activity activity) {
        mActivity = (ImagePickActivity) activity;

        mImageDataUtil = new ImageDataUtil(mActivity, this);

        if (!(mActivity instanceof OnListUpdatedListener)) {
            throw new RuntimeException(mActivity.toString()
                    + " must implement OnImagePickedListener");
        }
    }

    public void updateList() {
        mActivity.showDialog();
        mImageDataUtil.queryData(); //调用这个之后当成功从content provider中拿到列表时会调用result方法
    }

    public ArrayList<String> getFirstPicOfEachFolder(){
        ArrayList<String> firstPicList = new ArrayList<>();
        Set<Map.Entry<String, ImageDataUtil.ImageFolderInfo>> entrySet = mImageCollection.entrySet();
        for (Map.Entry<String, ImageDataUtil.ImageFolderInfo> entry : entrySet){
            firstPicList.add(entry.getKey()+"/"+entry.getValue().getFileNameList().get(0));
        }
        return firstPicList;
    }

    public ArrayList<String> getAllPicOfFolder(String folderPath){
        return mImageCollection.get(folderPath).getFileNameList();
    }

    @Override
    public void result(boolean isSuccess, HashMap<String, ImageDataUtil.ImageFolderInfo> imageCollection) {
        mImageCollection = imageCollection;
        mActivity.dismissDialog();

        ((OnListUpdatedListener) mActivity).onListUpdated(mImageCollection);
    }

    public interface OnListUpdatedListener{
        void onListUpdated(HashMap<String, ImageDataUtil.ImageFolderInfo> imageCollection);
    }
}
