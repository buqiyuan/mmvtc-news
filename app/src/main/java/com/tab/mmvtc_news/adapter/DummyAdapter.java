package com.tab.mmvtc_news.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.tab.mmvtc_news.fragment.PlaceholderFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 卜启缘
 * @version 1.0
 * @date 2019/10/31 21:30
 */
public class DummyAdapter extends FragmentPagerAdapter {
    List<PlaceholderFragment> fragments;

    public DummyAdapter(FragmentManager fm, List<PlaceholderFragment> fragments) {
        super(fm);

       this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        // 调用getItem来实例化给定页面的片段。
        // 返回一个PlaceholderFragment(定义为下面的静态内部类)。
        // PlaceholderFragment返回。newInstance(位置+ 1);
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        // 总共显示几页
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "PAGE 0";
            case 1:
                return "PAGE 1";
            case 2:
                return "PAGE 2";
            case 3:
                return "PAGE 3";
            case 4:
                return "PAGE 4";
        }
        return null;
    }
}