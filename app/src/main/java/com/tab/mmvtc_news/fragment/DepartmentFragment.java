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
import com.tab.mmvtc_news.adapter.MyViewpageAdapter;
import com.tab.mmvtc_news.adapter.FragmentAdapter;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by 卜启缘 on 2019/10/8.
 */
public class DepartmentFragment extends Fragment {
    private Toolbar mToolbar;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private MyViewpageAdapter myViewpageAdapter;
    List<String> images = new ArrayList<>();
    private Banner banner;
    String tmgcxLink;
    String hxgcxLink;
    String jdxxxLink;
    String jsjgcxjiLink;
    String skbLink;
    String jjglxLink;
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
        view = inflater.inflate(R.layout.fragment_department, container, false);

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
        tmgcxLink = "http://www.mmvtc.cn/templet/tmgcx/ShowClass.jsp?id=1273";
        hxgcxLink = "http://www.mmvtc.cn/templet/hxgcx/ShowClass.jsp?id=1355";
        jjglxLink = "http://www.mmvtc.cn/templet/jjglx/ShowClass.jsp?id=1200";
        jdxxxLink = "http://www.mmvtc.cn/templet/jdxxx/ShowClassPage.jsp?id=1180";
        jsjgcxjiLink = "http://www.mmvtc.cn/templet/jsjgcx/ShowClass.jsp?id=1212";
        skbLink = "http://www.mmvtc.cn/templet/skb/ShowClass.jsp?id=2190";
        if (titles.isEmpty()) {

            titles.add("土木工程系");
            titles.add("化学工程系");
            titles.add("经济管理系");
            titles.add("机电信息系");
            titles.add("计算机工程系");
            titles.add("社科基础部");

            Fragment fragment1 = ContentFragment.newInstance(tmgcxLink);
            Fragment fragment2 = ContentFragment.newInstance(hxgcxLink);
            Fragment fragment3 = ContentFragment.newInstance(jjglxLink);
            Fragment fragment4 = ContentFragment.newInstance(jdxxxLink);
            Fragment fragment5 = ContentFragment.newInstance(jsjgcxjiLink);
            Fragment fragment6 = ContentFragment.newInstance(skbLink);
            fragments.add(fragment1);
            fragments.add(fragment2);
            fragments.add(fragment3);
            fragments.add(fragment4);
            fragments.add(fragment5);
            fragments.add(fragment6);
        }
    }

    private void initEvents() {
        myViewpageAdapter = new MyViewpageAdapter(getChildFragmentManager(), titles, fragments);
        mViewPager.setOffscreenPageLimit(6);
        mViewPager.setAdapter(myViewpageAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }
}
