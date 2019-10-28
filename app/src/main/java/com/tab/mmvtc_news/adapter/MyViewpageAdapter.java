package com.tab.mmvtc_news.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;
/**
 * Created by 卜启缘 on 2019/10/8.
 */
public class MyViewpageAdapter extends FragmentPagerAdapter {
    private List<String> titleLists;
    private List<Fragment> fragments;

    public MyViewpageAdapter(FragmentManager fm, List<String> titleLists, List<Fragment> fragments) {
        super(fm);
        this.titleLists = titleLists;
        this.fragments = fragments;
    }

    public MyViewpageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleLists.get(position);
    }
}
