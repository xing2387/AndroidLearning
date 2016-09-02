package com.example.xing.androidlearning;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jiaxing on 8/29/16.
 */
public class ImageDataUtil {

    final private String TAG = "ImageDataUtil";

    private HashMap<String, ImageFolderInfo> mImageCollection;
    private OnQueryResultListener mOnQueryResultListener;
    private Context context;

    public ImageDataUtil(Context context) {
        this.context = context;
        if (mImageCollection == null) {
            mImageCollection = new HashMap<>();
        }
    }

    public ImageDataUtil(Context context, OnQueryResultListener onQueryResultListener) {
        this(context);
        this.mOnQueryResultListener = onQueryResultListener;
    }

    public void queryData() {
        (new Thread(){
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String[] project = new String[]{Media.DATA, Media.MIME_TYPE, Media.DISPLAY_NAME, Media.WIDTH, Media.HEIGHT, Media.DATE_TAKEN};
                String select = Media.WIDTH + ">100 and " + Media.HEIGHT + ">100";
                ContentResolver contentResolver = context.getContentResolver();

                Cursor cursor = contentResolver.query(mImageUri, project, select, null, Media.DEFAULT_SORT_ORDER);
                boolean isSuccess = false;
                while (cursor.moveToNext()) {
                    String pathName = (new File(cursor.getString(cursor.getColumnIndex(Media.DATA))).getParentFile().getPath());
//                    Log.d(TAG, "found file: "+cursor.getString(cursor.getColumnIndex(Media.DATA)));
                    if (mImageCollection.containsKey(pathName)) {
                        mImageCollection.get(pathName).getFileNameList().add(cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME)));
                    } else {
                        ArrayList<String> images = new ArrayList<>();
                        images.add(cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME)));
                        mImageCollection.put(pathName, new ImageFolderInfo(images));
                    }
                }
                isSuccess = true;
                if (mOnQueryResultListener != null){
                    mOnQueryResultListener.result(isSuccess, mImageCollection);
                }
            }

        }).start();
    }

//    private void reScan() {
//        sendBroadcast(new Intent(
//                Intent.ACTION_MEDIA_MOUNTED,
//                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//    }

    static interface OnQueryResultListener {
        public void result(boolean isSuccess, HashMap<String, ImageFolderInfo> mImageCollection);
    }

    public class ImageFolderInfo {
        private String folderPath;
        private ArrayList<String> fileNameList;

        public ImageFolderInfo(String folderPath, ArrayList<String> fileNameList) {
            this.folderPath = folderPath;
            this.fileNameList = fileNameList;
        }

        public ImageFolderInfo(ArrayList<String> fileNameList) {
            this(null, fileNameList);
        }

        public String getFolderPath() {
            return folderPath;
        }

        public void setFolderPath(String folderPath) {
            this.folderPath = folderPath;
        }

        public ArrayList<String> getFileNameList() {
            return fileNameList;
        }

        public void setFileNameList(ArrayList<String> fileNameList) {
            this.fileNameList = fileNameList;
        }


    }
}
