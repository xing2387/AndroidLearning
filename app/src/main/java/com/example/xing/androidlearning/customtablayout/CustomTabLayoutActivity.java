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

    private List<String> mPageTitle = Arrays.asList("Tabadsf1", "Tab232", "Tab3", "Ta4b4", "Tab511", "Tab3336", "T4ab7", "Tab52348", "Tab9");
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
        CustomTabLayout2 customTabLayout2 = ((CustomTabLayout2) findViewById(R.id.tablayout));
        customTabLayout2.setupViewPager(viewPager);
        customTabLayout2.addUnreadDot(0, "99+");
        customTabLayout2.addSmallDot(2);
        customTabLayout2.dismissRedDot(2);
    }
}
