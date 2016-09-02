package com.example.xing.androidlearning.Activity;

import android.app.TabActivity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.xing.androidlearning.R;
import com.example.xing.androidlearning.customview.StrokeTextView;
import com.example.xing.androidlearning.natives.JniEntry;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageFilterActivity extends TabActivity implements View.OnClickListener {
    private final String TAG = "ImageFilterActivity";

    private ImageView ivMainImage;
    private Bitmap mBitmap;
    private Bitmap mThumbnail;
    private String picPath;
    private File mPicFile = null;
//    private LoadingDialog mLoadingDialog;

    enum FilterType {Grayscale, BorW, Nagetive, Anaglyph}

    final String[] filterNames = {"灰度", "黑白", "负片", "浮雕"};

    public static String EXTRA_PIC_PATH;
//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//
//        @Override
//        public void onManagerConnected(int status) {
//            // TODO Auto-generated method stub
//            switch (status) {
//                case BaseLoaderCallback.SUCCESS:
//                    Log.i(TAG, "成功加载");
//                    break;
//                default:
//                    super.onManagerConnected(status);
//                    Log.i(TAG, "加载失败");
//                    break;
//            }
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_filter);
        ivMainImage = (ImageView) findViewById(R.id.imagefilter_main_iv);
        picPath = getIntent().getExtras().getString(EXTRA_PIC_PATH, null);

        if (picPath != null) {
            Log.d(TAG, picPath);
            mPicFile = new File(picPath);
            Point size = new Point();
            this.getWindowManager().getDefaultDisplay().getSize(size);
            Picasso.with(this).load(mPicFile).resize(size.x, size.y).centerInside().into(ivMainImage, new Callback() {
                @Override
                public void onSuccess() {
                    mBitmap = ((BitmapDrawable) ivMainImage.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, false);
                }

                @Override
                public void onError() {

                }
            });

        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_4, getApplicationContext(), mLoaderCallback);

    }

    private void initView() {
//        final ImageView previewBorW = (ImageView) findViewById(R.id.filter_preview_borw);
        TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("filter").setIndicator("滤镜").setContent(R.id.imagefilter_tabcontant_filter));
        tabHost.addTab(tabHost.newTabSpec("others").setIndicator("其他效果").setContent(R.id.imagefilter_tabcontant_others));

        ViewGroup filterTypes = (ViewGroup) findViewById(R.id.imagefilter_tab_filter);

        for (int i = 0; i < filterTypes.getChildCount(); i++) {
            final ViewGroup group = (ViewGroup) filterTypes.getChildAt(i);
            FilterType tryToGetFilterType = null;
            try {
                tryToGetFilterType = FilterType.valueOf(group.getTag().toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                filterTypes.removeView(group);
                Log.e(TAG, "could not match filter with tag " + group.getTag());
                break;
            }
            final FilterType filterType = tryToGetFilterType;
            ViewGroup.LayoutParams imageViewParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final ImageView previewImage = new ImageView(this);
            previewImage.setLayoutParams(imageViewParam);
            group.addView(previewImage);

            FrameLayout.LayoutParams textViewParam = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textViewParam.gravity = Gravity.BOTTOM;
            StrokeTextView filterName = new StrokeTextView(this, Color.parseColor("#000000"), Color.parseColor("#DDDDDD"));
//            final TextView filterName = new TextView(this);
            filterName.setLayoutParams(textViewParam);
            filterName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            filterName.setGravity(Gravity.CENTER_HORIZONTAL);
            filterName.setText(filterNames[filterType.ordinal()]);
            group.addView(filterName);

            if (mThumbnail == null) {
                Picasso.with(ImageFilterActivity.this).load(mPicFile).fit().centerCrop().into(previewImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        mThumbnail = ((BitmapDrawable) previewImage.getDrawable()).getBitmap().copy(Bitmap.Config.RGB_565, false);
                        Bitmap newBitmap = mThumbnail.copy(Bitmap.Config.ARGB_8888, true);
                        JniEntry.bitmapFilter(newBitmap, FilterType.valueOf(group.getTag().toString()).ordinal());
                        previewImage.setImageBitmap(newBitmap);
//                        ivMainImage.invalidate();
                    }

                    @Override
                    public void onError() {

                    }
                });
            } else {
                Bitmap newBitmap = mThumbnail.copy(Bitmap.Config.ARGB_8888, true);
                JniEntry.bitmapFilter(newBitmap, FilterType.valueOf(group.getTag().toString()).ordinal());
                previewImage.setImageBitmap(newBitmap);
            }
            previewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Bitmap newBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        JniEntry.bitmapFilter(newBitmap, FilterType.valueOf(group.getTag().toString()).ordinal());
                        ivMainImage.setImageBitmap(newBitmap);
//                        ivMainImage.invalidate();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        //设置tabhost的高度和标签的字体颜色
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View view = tabHost.getTabWidget().getChildAt(i);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = tabHost.getHeight() - 1;        //它的子view要比tabhost小，不然下面指示条出不来
            view.setLayoutParams(layoutParams);
            //字体颜色
            TextView textView = (TextView) view.findViewById(android.R.id.title);
            textView.setTextColor(Color.WHITE);
        }

        findViewById(R.id.imagefilter_menu_back).setOnClickListener(this);
        findViewById(R.id.imagefilter_menu_save).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagefilter_menu_save:
                //TODO
                break;
            case R.id.imagefilter_menu_back:
                ivMainImage.setImageBitmap(mBitmap);
//                Picasso.with(this).load(mPicFile).fit().centerInside().into(ivMainImage);
                break;
        }
    }
}
