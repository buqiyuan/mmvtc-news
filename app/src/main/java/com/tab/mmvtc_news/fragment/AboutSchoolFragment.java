package com.tab.mmvtc_news.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.adapter.MyViewpageAdapter;
import com.tab.mmvtc_news.okhttpUtil.OkHttpUtils;
import com.tab.mmvtc_news.okhttpUtil.callback.StringCallback;
import com.tab.mmvtc_news.utils.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.view.BannerViewPager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
/**
 * Created by 卜启缘 on 2019/10/8.
 */
public class AboutSchoolFragment extends Fragment {
    private Toolbar mToolbar;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private MyViewpageAdapter myViewpageAdapter;
    List<String> images = new ArrayList<>();
    List<String> links = new ArrayList<>();
    private Banner banner;
    //tab标题
    private List<String> titles = new ArrayList<>();
    private BannerViewPager viewPager;
    //fragments
    private List<Fragment> fragments = new ArrayList<>();
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about_school, container, false);

        initViews();
        getData();
        return view;
    }

    private void getData() {
        OkHttpUtils
                .get()
                .url("https://www.mmvtc.cn/templet/default/aboutme.html")
                .tag(this)
                .build()
                .connTimeOut(25000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        getData();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document doc = Jsoup.parse(response, "https://www.mmvtc.cn/templet/default/");
                        Elements li = doc.select(".container .subChannelList li");
                        for (Element ele : li) {
                            titles.add(ele.select("figcaption").text());
                            links.add(ele.select("a").attr("abs:href"));
                            images.add(ele.select("img").attr("abs:src"));
                        }
                        initDatas();
                        initEvents();
                    }
                });
    }

    private void initViews() {
        viewPager = (BannerViewPager) view.findViewById(R.id.bannerViewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tl);
        mViewPager = (ViewPager) view.findViewById(R.id.vp);
        banner = (Banner) view.findViewById(R.id.banner);
        mViewPager.setOffscreenPageLimit(5);
    }

    private void initDatas() {
        if (fragments.isEmpty()) {
            for (String value : links) {
                fragments.add(SchoolContentFragment.newInstance(value));
            }
        }
    }

    private void initEvents() {
        //设置图片加载器
        banner.setImages(images)
                .setImageLoader(new GlideImageLoader())
                .isAutoPlay(false)
                .setViewPagerIsScroll(false)
                .setBannerAnimation(Transformer.ZoomOut)
                .start();
        Log.e("images", String.valueOf(images));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(++position);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        myViewpageAdapter = new MyViewpageAdapter(getChildFragmentManager(), titles, fragments);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(myViewpageAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }
}
