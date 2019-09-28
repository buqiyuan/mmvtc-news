package com.tab.mmvtc_news.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.adapter.MyViewpageAdapter;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private MyViewpageAdapter myViewpageAdapter;
    String top_lendLink;
    String top_shelfLink;
    String top_bookLink;
    //tab标题
    private List<String> titles = new ArrayList<>();

    //fragments
    private List<Fragment> fragments = new ArrayList<>();
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_library, container, false);

        initViews();
        initDatas();
        initEvents();
        return view;
    }

    private void initViews() {
        tabLayout = (TabLayout) view.findViewById(R.id.tl);
        mViewPager = (ViewPager) view.findViewById(R.id.vp);
    }

    private void initDatas() {
        top_lendLink = "http://hwlibsys.mmvtc.cn:8080/top/top_lend.php";
        top_shelfLink = "http://hwlibsys.mmvtc.cn:8080/top/top_shelf.php";
        top_bookLink = "http://hwlibsys.mmvtc.cn:8080/top/top_book.php";
        if (titles.isEmpty()) {

            titles.add("书目检索");
            titles.add("热门借阅");
            titles.add("热门收藏");
            titles.add("热门图书");
//            titles.add("我的图书");

            Fragment fragment1 = new SearchBookFragment();
            Fragment fragment2 = BookContentFragment.newInstance(top_lendLink);
            Fragment fragment3 = BookContentFragment.newInstance(top_shelfLink);
            Fragment fragment4 = BookContentFragment.newInstance(top_bookLink);
//            Fragment fragment5 = new MyLibraryFragment();
            fragments.add(fragment1);
            fragments.add(fragment2);
            fragments.add(fragment3);
            fragments.add(fragment4);
//            fragments.add(fragment5);
        }
    }

    private void initEvents() {
        myViewpageAdapter = new MyViewpageAdapter(getChildFragmentManager(), titles, fragments);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(myViewpageAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }
}
