package com.tab.viewpager.fragment;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tab.viewpager.R;
import com.tab.viewpager.activity.GlideImageLoader;
import com.tab.viewpager.adapter.MyViewpaerAdapter;
import com.tab.viewpager.jwc.FragmentAdapter;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.transformer.CubeInTransformer;
import com.youth.banner.view.BannerViewPager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

public class AboutSchoolFragment extends Fragment {
    private Toolbar mToolbar;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private MyViewpaerAdapter myViewpaerAdapter;
    List<String> images = new ArrayList<>();
    List<String> links = new ArrayList<>();
    private Banner banner;
    String newsLink;
    String noticeLink;
    String xueshuLink;
    String xibuLink;
    String gaozhuanLink;
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
    private BannerViewPager viewPager;
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
        view = inflater.inflate(R.layout.fragment_about_school, container, false);

        initViews();
        //banner设置方法全部调用完毕时最后调用
        new Thread(new Runnable() {
            @Override
            public void run() {
                //需要在子线程中处理的逻辑
                Document doc = null;
                try {
                    doc = Jsoup.connect("http://www.mmvtc.cn/templet/default/aboutme.html").get();
                    Elements li = doc.select(".container .subChannelList li");
                    for (Element ele : li) {
                        titles.add(ele.select("figcaption").text());
                        links.add(ele.select("a").attr("abs:href"));
                        images.add(ele.select("img").attr("abs:src"));
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return view;
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    initDatas();
                    initEvents();
                    break;
            }
        }
    };

    private void initViews() {
        viewPager = (BannerViewPager) view.findViewById(com.youth.banner.R.id.bannerViewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tl);
        mViewPager = (ViewPager) view.findViewById(R.id.vp);
        banner = (Banner) view.findViewById(R.id.banner);
        mViewPager.setOffscreenPageLimit(4);
    }

    private void initDatas() {
        if (fragments.isEmpty()){
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
        myViewpaerAdapter = new MyViewpaerAdapter(getChildFragmentManager(), titles, fragments);

        mViewPager.setAdapter(myViewpaerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }
}
