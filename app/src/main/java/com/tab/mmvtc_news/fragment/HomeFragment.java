package com.tab.mmvtc_news.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.adapter.MyViewpageAdapter;
import com.tab.mmvtc_news.utils.GlideImageLoader;
import com.tab.mmvtc_news.activity.MainActivity;
import com.tab.mmvtc_news.utils.LogUtil;
import com.youth.banner.Banner;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private MyViewpageAdapter myViewpageAdapter;
    List<String> images = new ArrayList<>();
    private Banner banner;
    String newsLink;
    String noticeLink;
    String xueshuLink;
    String xibuLink;
    String gaozhuanLink;
    //tab标题
    private List<String> titles = new ArrayList<>();
    //fragments
    private List<Fragment> fragments = new ArrayList<>();
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews();
        initDatas();
        initEvents();
        //banner设置方法全部调用完毕时最后调用
        return view;
    }


    private void initViews() {
        tabLayout = (TabLayout) view.findViewById(R.id.tl);
        mViewPager = (ViewPager) view.findViewById(R.id.vp);
        banner = (Banner) view.findViewById(R.id.banner);
    }

    private void initDatas() {
        Document doc  = MainActivity.getDocument();
        Elements imgs = doc.select("img[src^=slider]");
        images.clear();
        LogUtil.e("imgs", imgs.toString());
        for (Element ele : imgs) {
            LogUtil.e("images22",ele.attr("abs:src") );
            images.add(TextUtils.isEmpty(ele.attr("abs:src").trim())
                    ? "https://www.mmvtc.cn/templet/default/" + ele.attr("src")
                    : ele.attr("abs:src"));
        }
        newsLink = doc.select(".col-md-6 .news .title .pull-right a").attr("href");
        noticeLink = doc.select(".col-md-4 .news .title .pull-right a").attr("href");
        xueshuLink = "https://www.mmvtc.cn/templet/xskyw/ShowClass.jsp?id=2002";
        xibuLink = doc.select(".col-md-6 .tabs .tab-content:nth-of-type(2) .more .pull-right a").attr("href");
        gaozhuanLink = doc.select(".col-md-6 .tabs .tab-content:nth-of-type(3) .more .pull-right a").attr("href");

        if (titles.isEmpty()) {

            titles.add("学院新闻");
            titles.add("通知公告");
            titles.add("学术信息");
            titles.add("系部动态");
            titles.add("高职高专动态");

            Fragment fragment1 = ContentFragment.newInstance(newsLink);
            Fragment fragment2 = ContentFragment.newInstance(noticeLink);
            Fragment fragment3 = ContentFragment.newInstance(xueshuLink);
            Fragment fragment4 = ContentFragment.newInstance(xibuLink);
            Fragment fragment5 = ContentFragment.newInstance(gaozhuanLink);
            fragments.add(fragment1);
            fragments.add(fragment2);
            fragments.add(fragment3);
            fragments.add(fragment4);
            fragments.add(fragment5);
        }
    }

    private void initEvents() {
        //设置图片加载器
        banner.setImages(images)
                .setImageLoader(new GlideImageLoader())
                .setDelayTime(3000)
                .start();

        myViewpageAdapter = new MyViewpageAdapter(getChildFragmentManager(), titles, fragments);
        mViewPager.setOffscreenPageLimit(6);
        mViewPager.setAdapter(myViewpageAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }
}
