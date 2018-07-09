package com.example.xing.androidlearning.customtablayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.xing.androidlearning.R;
import com.example.xing.androidlearning.pageviewindicator.VpSimpleFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomTabLayoutActivity extends AppCompatActivity {

    private List<String> mPageTitle = Arrays.asList("Tab1", "Tab2", "Tab3", "Tab4", "Tab5", "Tab6", "Tab7", "Tab8", "Tab9");
    private ArrayList<Fragment> mFragments = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customtablayout);

        for (String fragContent : mPageTitle) {
            VpSimpleFragment vpSimpleFragment = VpSimpleFragment.newInstance(fragContent);
            mFragments.add(vpSimpleFragment);
        }
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mPageTitle.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mPageTitle.get(position);
            }
        };

        ViewPager viewPager = ((ViewPager) findViewById(R.id.viewpager));
        viewPager.setAdapter(adapter);
        ((CustomTabLayout2) findViewById(R.id.tablayout)).setupViewPager(viewPager);
    }
}
