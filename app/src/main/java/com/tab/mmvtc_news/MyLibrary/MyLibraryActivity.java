package com.tab.mmvtc_news.MyLibrary;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.hjq.toast.ToastUtils;
import com.tab.mmvtc_news.MyLibrary.verticaltablayout.TabView;
import com.tab.mmvtc_news.MyLibrary.verticaltablayout.VerticalTabLayout;
import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.adapter.DummyAdapter;
import com.tab.mmvtc_news.adapter.MyTabAdapter;
import com.tab.mmvtc_news.fragment.PlaceholderFragment;
import com.tab.mmvtc_news.okhttpUtil.OkHttpUtils;
import com.tab.mmvtc_news.okhttpUtil.callback.StringCallback;
import com.tab.mmvtc_news.views.VerticalViewPager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by 卜启缘 on 2019/10/30.
 */
public class MyLibraryActivity extends AppCompatActivity {

    private String url = "http://hwlibsys.mmvtc.cn:8080/reader/redr_info.php";
    private static final float MIN_SCALE = 0.75f;
    private static final float MIN_ALPHA = 0.75f;
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> urls = new ArrayList<>();
    private VerticalViewPager verticalViewPager;
    private VerticalTabLayout tablayout;
   private List<PlaceholderFragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_library);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            finish();//返回
        });

        initView();
        parseHtml();
    }

    private void initView() {
        verticalViewPager = (VerticalViewPager) findViewById(R.id.verticalviewpager);
        tablayout = (VerticalTabLayout) findViewById(R.id.tablayout);
    }

    private void parseHtml() {
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .connTimeOut(10000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.show("获取数据失败！正在重连...");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document doc =  Jsoup.parse(response,"http://hwlibsys.mmvtc.cn:8080/reader/");
                        Elements nav_mylibs = doc.select("#nav_mylib > li");
                        for (Element li : nav_mylibs) {
                            titles.add(li.text());
                            Log.e("URL???",li.select("a").attr("abs:href"));
                            fragments.add(PlaceholderFragment.newInstance(li.select("a").attr("abs:href")));
                        }
                        //设置布局
                        setLayout();
                    }
                });

    }
//    设置布局
    private void setLayout(){
        //        Collections.addAll(titles, "我的首页", "证件信息", "当前借阅", "借阅历史");
        tablayout.setTabAdapter(new MyTabAdapter(MyLibraryActivity.this, titles));
        tablayout.addOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
                verticalViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselected(TabView tab, int position) {

            }
        });
        verticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tablayout.setTabSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        verticalViewPager.setAdapter(new DummyAdapter(getFragmentManager(),fragments));
        verticalViewPager.setPageMargin(getResources().
                getDimensionPixelSize(R.dimen.margin_15));
        verticalViewPager.setPageMarginDrawable(new ColorDrawable(
                getResources().getColor(android.R.color.holo_green_dark)));

        verticalViewPager.setPageTransformer(true, (view, position) -> {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // 这一页离屏幕左侧很远
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // 修改默认的幻灯片转换以缩小页面
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationY(vertMargin - horzMargin / 2);
                } else {
                    view.setTranslationY(-vertMargin + horzMargin / 2);
                }

                // 缩小页面(在MIN_SCALE和1之间)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // 这一页离屏幕右侧很远
                view.setAlpha(0);
            }
        });
    }
}
