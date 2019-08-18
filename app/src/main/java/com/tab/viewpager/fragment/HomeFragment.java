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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private Toolbar mToolbar;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private MyViewpaerAdapter myViewpaerAdapter;
    List<String> images = new ArrayList<>();
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
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews();
        //banner设置方法全部调用完毕时最后调用
        new Thread(new Runnable() {
            @Override
            public void run() {
                //需要在子线程中处理的逻辑
                Document doc = null;
                try {
                    doc = Jsoup.connect("http://www.mmvtc.cn/templet/default/index.jsp").get();
                    Elements imgs = doc.select("img[src^=slider]");
                    images.clear();
                    for (Element ele : imgs) {
                        Log.e("images", ele.attr("abs:src"));
                        images.add(ele.attr("abs:src"));
                    }
                    newsLink = doc.select(".col-md-6 .news .title .pull-right a").attr("href");
                    noticeLink = doc.select(".col-md-4 .news .title .pull-right a").attr("href");
                    xueshuLink = "http://www.mmvtc.cn/templet/xskyw/ShowClass.jsp?id=2002";
                    xibuLink = doc.select(".col-md-6 .tabs .tab-content:nth-of-type(2) .more .pull-right a").attr("href");
                    gaozhuanLink = doc.select(".col-md-6 .tabs .tab-content:nth-of-type(3) .more .pull-right a").attr("href");
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
//                    new Thread(avatarRun).start();
                    break;
//                case 2:
//                    Bitmap bitmap = (Bitmap) msg.obj;
//                    Bitmap avatar = toRoundCornerImage(bitmap, 0);
//                    Log.e("avatar", avatar.toString());
//                    iv_avatar.setImageBitmap(avatar);
//                    break;
            }
        }
    };

    private void initViews() {
//        mToolbar = (Toolbar) view.findViewById(R.id.tb);
        tabLayout = (TabLayout) view.findViewById(R.id.tl);
        mViewPager = (ViewPager) view.findViewById(R.id.vp);
        banner = (Banner) view.findViewById(R.id.banner);
//        setSupportActionBar(mToolbar);
    }

    private void initDatas() {
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

        myViewpaerAdapter = new MyViewpaerAdapter(getChildFragmentManager(), titles, fragments);

        mViewPager.setAdapter(myViewpaerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }
}
