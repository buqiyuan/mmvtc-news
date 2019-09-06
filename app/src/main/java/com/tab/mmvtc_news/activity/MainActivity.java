package com.tab.mmvtc_news.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.toast.ToastUtils;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.adapter.MyViewpaerAdapter;
import com.tab.mmvtc_news.fragment.AboutSchoolFragment;
import com.tab.mmvtc_news.fragment.DepartmentFragment;
import com.tab.mmvtc_news.fragment.HomeFragment;
import com.tab.mmvtc_news.jwc.ChangePasswordActivity;
import com.tab.mmvtc_news.jwc.CourseFragment;
import com.tab.mmvtc_news.jwc.FragmentAdapter;
import com.tab.mmvtc_news.jwc.LoginActivity;
import com.tab.mmvtc_news.jwc.MeFragment;
import com.tab.mmvtc_news.jwc.MoreFragment;
import com.tab.mmvtc_news.jwc.ScoreFragment;
import com.tab.mmvtc_news.okhttpUtil.OkHttpUtils;
import com.tab.mmvtc_news.okhttpUtil.callback.BitmapCallback;
import com.tab.mmvtc_news.okhttpUtil.callback.StringCallback;
import com.xuexiang.xupdate.utils.UpdateUtils;
import com.youth.banner.Banner;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

//import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener {
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
    private String password;
    private String url = "http://jwc.mmvtc.cn/";
    private String autoLoginUrl = "http://jwc.mmvtc.cn/default4.aspx";
    private static String refererUrl = "";
    private static String infoUrl = "";
    private static String scoreUrl = "";
    private static String xgPswUrl = "";
    private static String courseUrl = "";
    private ViewPager pager;
    private TextView tv_studentName;
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
    private TabLayout tab_bottom;
    private String[] itemsName;
    private FragmentManager fragmentManager;
    private FrameLayout frameLayout;
    private static Document doc;
    private String viewstate;
    private LinearLayout ll_header;

    public static Document getDocument() {
        return doc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itemsName = getResources().getStringArray(R.array.mainItemsName);
        initViews();
        initDatas();
        initEvents();
        //banner设置方法全部调用完毕时最后调用
        new Thread(new Runnable() {
            @Override
            public void run() {
                //需要在子线程中处理的逻辑
                doc = null;
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
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void initTab() {
        TypedArray typedArray = getResources().obtainTypedArray(R.array.mainItemsIcon);
        if (itemsName.length != typedArray.length()) {
            throw new IllegalStateException("The items name length must same with icons ");
        }
        for (int i = 0; i < itemsName.length; i++) {
            tab_bottom.addTab(tab_bottom.newTab().setIcon(typedArray.getResourceId(i, 0)).setTag(i));
        }
        typedArray.recycle();
    }

    private final MyHandler mHandler = new MyHandler(this);

    static class MyHandler extends Handler {

        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (mActivity.get() == null) {
                return;
            }
            MainActivity activity = mActivity.get();
            switch (msg.what) {
                case 1:
                    new Thread(activity.avatarRun).start();
                    break;
                case 2:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    Bitmap avatar = toRoundCornerImage(bitmap, 0);
                    Log.e("avatar", avatar.toString());
                    activity.iv_avatar.setImageBitmap(avatar);
                    break;
                case 3:
                    activity.isLogin = false;
                    activity.tv_name.setText("登录身份已过期，请重新登录");
                    activity.tv_desc.setText("");
                    Toast.makeText(activity, "登录身份已过期，请重新登录！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    Runnable avatarRun = new Runnable() {
        @Override
        public void run() {
            getAvatar();
        }
    };

    /**
     * 获取圆角位图的方法
     *
     * @param bitmap 需要转化成圆角的位图
     * @param pixels 圆角的度数，数值越大，圆角越大
     * @return 处理后的圆角位图
     */
    public static Bitmap toRoundCornerImage(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        // 抗锯齿
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private void getAvatar() {
        if (TextUtils.isEmpty(infoUrl) || isLogin == false) {
            return;
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.clear();
        headers.put("Referer", infoUrl);
        headers.put("Host", "jwc.mmvtc.cn");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");

        OkHttpUtils
                .get()
                .url(infoUrl)
                .headers(headers)
                .tag(this)
                .build()
                .connTimeOut(20000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        //尝试重新登录
                        autoLogin();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document dom = Jsoup.parse(response);
                        String avatarUrl = url + dom.select("#xszp").attr("src");
                        OkHttpUtils
                                .get()//
                                .url(avatarUrl)//
                                .tag(this)//
                                .build()//
                                .connTimeOut(20000)
                                .readTimeOut(20000)
                                .writeTimeOut(20000)
                                .execute(new BitmapCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        //尝试重新登录
                                        autoLogin();
                                    }

                                    @Override
                                    public void onResponse(Bitmap bitmap, int id) {
                                        Log.e("TAG", "onResponse：complete");
                                        iv_avatar.setImageBitmap(bitmap);
                                    }
                                });
                    }

                });
    }

    private void doLogin() {
        OkHttpUtils
                .post()
                .url(autoLoginUrl)
                .addParams("TextBox1", name)
                .addParams("TextBox2", password)
                .addParams("RadioButtonList1", "学生")
                .addParams("__VIEWSTATE", viewstate)
                .addParams("Button1", " 登 录 ")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                判断登陆身份cookie是否失效
                        if (isCookieOverdue(infoUrl)) {
                            Message msg = new Message();
                            msg.what = 3;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        getCookie();
                    }
                });
    }

    private void getCookie() {
        String jwcUrl = "http://jwc.mmvtc.cn/xs_main.aspx";
        OkHttpUtils
                .get()
                .url(jwcUrl)
                .addParams("xh", name)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //获取头像
                        getAvatar();
                    }

                });
    }

    private void autoLogin() {
        OkHttpUtils
                .get()
                .url(autoLoginUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document html = Jsoup.parse(response);
                        Elements e = html.select("input[name=__VIEWSTATE]");
                        viewstate = e.get(0).attr("value");
                        doLogin();
                    }

                });
    }

    public static boolean isCookieOverdue(String href) {
        Document doc = null;
        Connection.Response response = null;
        try {
            response = Jsoup.connect(href)
                    .method(Connection.Method.GET)
                    .execute();
            Map<String, String> getCookies = response.cookies();
            String Cookie = getCookies.toString();
            Cookie = Cookie.substring(Cookie.indexOf("{") + 1, Cookie.lastIndexOf("}"));
            Cookie = Cookie.replaceAll(",", ";");
            Log.e("COOKIE", Cookie);

            doc = Jsoup.connect(href)
                    .header("Cookie", Cookie)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
                    .get();

            String text = doc.select("body").text();
            String textInfo = text.toLowerCase().replaceAll("\\s*", "");
            if (textInfo.indexOf("objectmovedtohere") != -1) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void initViews() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tl);
        iv_avatar = (ImageView) headView.findViewById(R.id.iv_avatar);
        ll_header = (LinearLayout) headView.findViewById(R.id.ll_header);
        tv_name = (TextView) headView.findViewById(R.id.tv_name);
        tv_desc = (TextView) headView.findViewById(R.id.tv_desc);
        mToolbar.setTitle("首页");

        setSupportActionBar(mToolbar);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//左侧抽屉
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        setSupportActionBar(mToolbar);

        tab_bottom = (TabLayout) findViewById(R.id.tl_bottom);
        if (tab_bottom.getTabCount() > 0) {
            tab_bottom.getTabAt(0).select();
            switchTabSelect(0);
        }
        initTab();
        tab_bottom.addOnTabSelectedListener(this);

        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new HomeFragment());
        fragments.add(new DepartmentFragment());
        fragments.add(new AboutSchoolFragment());
        fragments.add(new MoreFragment());
        adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        pager = (ViewPager) findViewById(R.id.vp);
        pager.setOffscreenPageLimit(4);
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);
        pager.setOnPageChangeListener(this);
        fragmentManager = getSupportFragmentManager();
    }

    private void initDatas() {
        Intent intent = getIntent();
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        isLogin = sp.contains("password");
//        判断用户是否是登录状态，根据密码判断
        if (intent.getBooleanExtra("isLogin", false)) {
            name = intent.getStringExtra("name");
            studentName = intent.getStringExtra("studentName");
            cookie = intent.getStringExtra("cookie");
            infoUrl = url + intent.getStringExtra("infoUrl");
            xgPswUrl = url + intent.getStringExtra("xgPswUrl");
            scoreUrl = url + intent.getStringExtra("scoreUrl");
            courseUrl = url + intent.getStringExtra("courseUrl");
            refererUrl = "http://jwc.mmvtc.cn/xs_main.aspx?xh=" + name;
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("studentName", studentName);
            editor.putString("cookie", cookie);
            editor.putString("infoUrl", infoUrl);
            editor.putString("scoreUrl", scoreUrl);
            editor.putString("courseUrl", courseUrl);
            editor.putString("xgPswUrl", xgPswUrl);
            editor.putString("refererUrl", refererUrl);
            editor.commit();
        } else if (isLogin) {
            password = sp.getString("password", null);
            name = sp.getString("name", null);
            studentName = sp.getString("studentName", null);
            cookie = sp.getString("cookie", null);
            infoUrl = sp.getString("infoUrl", null);
            xgPswUrl = sp.getString("xgPswUrl", null);
            scoreUrl = sp.getString("scoreUrl", null);
            courseUrl = sp.getString("courseUrl", null);
        }
//        设置用户名
        if (isLogin) {
            if (sp.contains("studentName") && sp.getString("studentName", null) != "") {
                tv_name.setText(sp.getString("studentName", null));
                tv_desc.setText("这人很懒，什么也没留下。");
            }
        }
    }

    private void initEvents() {
        //给头像注册点击事件
        ll_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin) {
                    return;
                }
                Bundle bundle = new Bundle();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
//        myViewpaerAdapter = new MyViewpaerAdapter(getSupportFragmentManager(), titles, fragments);
    }

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
        if (id == R.id.action_upgrade) {
            ToastUtils.show("当前已是最新版本！");
            return true;
        }else if (id == R.id.action_about) {
            ToastUtils.show("茂职院校园信息APP！\n 版本号:"+ UpdateUtils.getVersionName(MainActivity.this));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {
//            关于
            new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("茂职院校园信息APP"+UpdateUtils.getVersionName(MainActivity.this))
                    .setContentText("用途：毕业设计\n\n" +
                            "作者：猿谋人\n\n" +
                            "BUG反馈: 1743369777@qq.com\n\n" +
                            "github：https://github.com/buqiyuan")
                    .setCustomImage(R.drawable.about_avatar)
                    .show();
            return true;
        } else if (id == R.id.nav_state) {
//            说明
            new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("简要说明")
                    .setContentText("登录账户为茂职院教务处学生登陆账户。")
                    .setCustomImage(R.drawable.state)
                    .show();
            return true;
        }
//        如果用户名不为空，则查询相关信息
        if (isLogin) {
            if (id == R.id.nav_camera) {
//            打开个人信息
                Intent intent = new Intent(MainActivity.this, MeFragment.class);
                startActivity(intent);
            } else if (id == R.id.nav_gallery) {
//            打开历年成绩
                Intent intent = new Intent(MainActivity.this, ScoreFragment.class);
                startActivity(intent);
            } else if (id == R.id.nav_slideshow) {
//            打开课程表
                Intent intent = new Intent(MainActivity.this, CourseFragment.class);
                startActivity(intent);
            } else if (id == R.id.nav_manage) {
//            打开修改密码
                Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_send) {
//            退出登录
                if (alertDialog == null) {
                    alertDialog = new AlertDialog.Builder(MainActivity.this).
                            setMessage("确定退出登录吗？").
                            setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    isLogin = false;
                                    tv_name.setText("未登录");
                                    tv_desc.setText("");
                                    iv_avatar.setImageResource(R.drawable.default_avatar);
                                    SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.remove("password");
                                    editor.commit();
                                    ToastUtils.show("你已退出登录！");
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    Toast.makeText(MainActivity.this,"你点击了取消",Toast.LENGTH_LONG).show();
                                }
                            })
                            .create();
                }
                alertDialog.show();
                alertDialog = null;
            }
        } else {
            ToastUtils.show("您尚未登录，请先登录！");
        }
//          关闭左右抽屉
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int tag = Integer.parseInt(String.valueOf(tab.getTag()));
        switchTabSelect(tag);
        tab_bottom.setTag(tag);
        pager.setCurrentItem(tag);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (arg0 == 2) {
            switchTabSelect(pager.getCurrentItem());
            switch (pager.getCurrentItem()) {
                case 0:
                    tab_bottom.getTabAt(0).select();
                    break;
                case 1:
                    tab_bottom.getTabAt(1).select();
                    break;
                case 2:
                    tab_bottom.getTabAt(2).select();
                    break;
                case 3:
                    tab_bottom.getTabAt(3).select();
                    break;
            }
        }
    }

    private void switchTabSelect(int i) {
        if (i == 0) {
            mToolbar.setTitle("首页");
        } else if (i == 1) {
            mToolbar.setTitle("系部新闻");
        } else if (i == 2) {
            mToolbar.setTitle("学校概况");
        } else if (i == 3) {
            mToolbar.setTitle("其他");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {// 双击退出软件
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (flag == false) {
                flag = true;
                ToastUtils.show("再按一次退出软件");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flag = false;
                    }
                }, 2000);
            } else {
                finish();
                System.exit(0);
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
