package com.example.xing.androidlearning;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.xing.androidlearning.view.BottomMenuBar;

import java.util.LinkedList;

public class ContactsHomeActivity extends FragmentActivity {
    final private String TAG = "ContactsHomeActivity";

    private ViewPager mVpContainer;
    private LinkedList<Fragment> mFragList;
    private FragmentPagerAdapter mPagerAdapter;
    private BottomMenuBar mBottomMenuBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_home);

        initView();
        initCtrl();
    }

    private void initView(){
        mVpContainer = (ViewPager) findViewById(R.id.ch_vp_container);
        mBottomMenuBar = (BottomMenuBar) findViewById(R.id.ch_bottom_menu_bar);
    }

    private void initCtrl(){
        mVpContainer.setOffscreenPageLimit(3);
        mFragList = new LinkedList<>();
        mFragList.add(new PlusOneFragment());
        mFragList.add(new PlusOneFragment());
        mFragList.add(ContactsListFragment.newInstance());
        mFragList.add(new PlusOneFragment());

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

        mVpContainer.setAdapter(mPagerAdapter);
        mBottomMenuBar.setViewPager(mVpContainer);
    }
}
