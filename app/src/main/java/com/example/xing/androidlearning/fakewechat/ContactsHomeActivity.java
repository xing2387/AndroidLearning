package com.example.xing.androidlearning.fakewechat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.xing.androidlearning.R;
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

    private void initView() {
        mVpContainer = (ViewPager) findViewById(R.id.ch_vp_container);
        mBottomMenuBar = (BottomMenuBar) findViewById(R.id.ch_bottom_menu_bar);
    }

    private void initCtrl() {
        mVpContainer.setOffscreenPageLimit(3);
        mFragList = new LinkedList<>();
        mFragList.add(new Fragment());
        mFragList.add(new Fragment());
        mFragList.add(ContactsListFragment.newInstance());
        mFragList.add(new Fragment());

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
