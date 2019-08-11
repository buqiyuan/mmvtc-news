package com.tab.viewpager.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tab.viewpager.R;
import com.tab.viewpager.adapter.MyViewpaerAdapter;
import com.tab.viewpager.fragment.ContentFragment;
import com.youth.banner.Banner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
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
    //tab标题
    private List<String> titles = new ArrayList<>();

    //fragments
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        mToolbar = (Toolbar) findViewById(R.id.tb);
        tabLayout = (TabLayout) findViewById(R.id.tl);
        mViewPager = (ViewPager) findViewById(R.id.vp);
        banner = (Banner) findViewById(R.id.banner);
        mToolbar.setTitle("首页");
        setSupportActionBar(mToolbar);

    }

    private void initDatas() {
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

    private void initEvents() {
        //设置图片加载器
        banner.setImages(images)
                .setImageLoader(new GlideImageLoader())
                .setDelayTime(3000)
                .start();
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_search:
                        //TODO
                        Toast.makeText(MainActivity.this, "别点了！我只是个装饰", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_about:
                        //TODO
                        Toast.makeText(MainActivity.this, "不好意思，我也是！", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        myViewpaerAdapter = new MyViewpaerAdapter(getSupportFragmentManager(), titles, fragments);

        mViewPager.setAdapter(myViewpaerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Toast.makeText(MainActivity.this, "我是个人信息", Toast.LENGTH_SHORT);
        } else if (id == R.id.nav_gallery) {
            Toast.makeText(MainActivity.this, "我是历年成绩", Toast.LENGTH_SHORT);
        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(MainActivity.this, "我是课程表", Toast.LENGTH_SHORT);
        } else if (id == R.id.nav_manage) {
            Toast.makeText(MainActivity.this, "我是修改密码", Toast.LENGTH_SHORT);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
