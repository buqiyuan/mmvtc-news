package com.tab.mmvtc_news.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.adapter.MyViewpaerAdapter;
import com.tab.mmvtc_news.jwc.FragmentAdapter;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {
    private Toolbar mToolbar;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private MyViewpaerAdapter myViewpaerAdapter;
    List<String> images = new ArrayList<>();
    private Banner banner;
    String top_lendLink;
    String top_shelfLink;
    String jdxxxLink;
    String jsjgcxjiLink;
    String skbLink;
    String top_bookLink;
    private String studentName;
    private static String cookie;
    private String name;
    private String url = "http://jwc.mmvtc.cn/";
    private static String refererUrl = "";
    private static String infoUrl = "";
    private static String scoreUrl = "";
    private static String xgPswUrl = "";
    private static String courseUrl = "";
    private ViewPager pager;
    private TextView tv_studentName;
    private TextView page_title;
    private FragmentAdapter adapter;
    //tab标题
    private List<String> titles = new ArrayList<>();

    //fragments
    private List<Fragment> fragments = new ArrayList<>();
    private ImageView iv_avatar;
    private NavigationView navigationView;
    private View headView;
    private boolean flag = false;
    private AlertDialog alertDialog;
    private Boolean isLogin = false;
    private TextView tv_name;
    private TextView tv_desc;
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
        banner = (Banner) view.findViewById(R.id.banner);
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
        myViewpaerAdapter = new MyViewpaerAdapter(getChildFragmentManager(), titles, fragments);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(myViewpaerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }
}
