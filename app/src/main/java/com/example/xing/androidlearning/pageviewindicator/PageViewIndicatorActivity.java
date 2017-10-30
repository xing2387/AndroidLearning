package com.example.xing.androidlearning.pageviewindicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.example.xing.androidlearning.R;
import com.example.xing.androidlearning.view.TriangleIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PageViewIndicatorActivity extends FragmentActivity {


    private List<String> mFragContents = Arrays.asList("Tab1", "Tab2", "Tab3", "Tab4", "Tab5", "Tab6", "Tab7", "Tab8", "Tab9");
    private ArrayList<VpSimpleFragment> mFragList = new ArrayList<>();
    private ViewPager mVp;
    private FragmentPagerAdapter mPagerAdapter;
    private TriangleIndicator mTriangleIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_triangle_page_view_indicator);

        initView();
        initCtrl();
    }

    private void initView(){
        mVp = (ViewPager) findViewById(R.id.trianglepage_vp_fragcontain);
        mTriangleIndicator = (TriangleIndicator) findViewById(R.id.tpvi_triangleindicator);
//        mTriangleIndicator.setTabs(mFragContents);
    }

    private void initCtrl(){
        initPagerContent();

        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragList.get(position);
            }

        };

        mVp.setAdapter(mPagerAdapter);
        mTriangleIndicator.setViewPager(mVp);
    }

    /**
     * 初始化放要在ViewPager里面的Fragment
     */
    private void initPagerContent() {
        for (String fragContent : mFragContents){
            VpSimpleFragment vpSimpleFragment = VpSimpleFragment.newInstance(fragContent);
            mFragList.add(vpSimpleFragment);
        }
    }

}
