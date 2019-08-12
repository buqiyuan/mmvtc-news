package com.tab.viewpager.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tab.viewpager.R;
import com.tab.viewpager.adapter.MyViewpaerAdapter;
import com.tab.viewpager.fragment.ContentFragment;
import com.tab.viewpager.jwc.ChangePasswordActivity;
import com.tab.viewpager.jwc.CourseFragment;
import com.tab.viewpager.jwc.FragmentAdapter;
import com.tab.viewpager.jwc.LoginActivity;
import com.tab.viewpager.jwc.MeFragment;
import com.tab.viewpager.jwc.ScoreFragment;
import com.youth.banner.Banner;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.drawable;
import static android.R.attr.name;
import static java.security.AccessController.getContext;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    new Thread(avatarRun).start();
                    break;
                case 2:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    Bitmap avatar = toRoundCornerImage(bitmap, 0);
                    Log.e("avatar", avatar.toString());
                    iv_avatar.setImageBitmap(avatar);
                    break;
            }
        }
    };
    Runnable avatarRun = new Runnable() {
        @Override
        public void run() {
            try {
                HttpGet httpGet = new HttpGet(infoUrl);
                httpGet.setHeader("Cookie", cookie);
                httpGet.setHeader("Referer", infoUrl);
                HttpClient client = new DefaultHttpClient();
                HttpResponse httpResponse = client.execute(httpGet);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    String html = EntityUtils.toString(httpResponse.getEntity());
                    Document dom = Jsoup.parse(html);
                    Log.e("imaUrl", url + dom.select("#xszp").attr("src"));
                    httpGet = new HttpGet(url + dom.select("#xszp").attr("src"));
                    httpGet.setHeader("Cookie", cookie);
                    httpGet.setHeader("Referer", infoUrl);
                    httpResponse = client.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        InputStream is = httpResponse.getEntity().getContent();
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        Message msg = new Message();
                        msg.obj = bitmap;
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    private void initViews() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        mToolbar = (Toolbar) findViewById(R.id.tb);
        tabLayout = (TabLayout) findViewById(R.id.tl);
        mViewPager = (ViewPager) findViewById(R.id.vp);
        iv_avatar = (ImageView) headView.findViewById(R.id.iv_avatar);
        tv_name = (TextView) headView.findViewById(R.id.tv_name);
        tv_desc = (TextView) headView.findViewById(R.id.tv_desc);
        banner = (Banner) findViewById(R.id.banner);
//        mToolbar.setTitle("首页");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        setSupportActionBar(mToolbar);
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
        //给头像注册点击事件
        iv_avatar.setOnClickListener(new View.OnClickListener() {
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

        if (id == R.id.nav_share) {
//            关于
            if (alertDialog == null) {
                alertDialog = new AlertDialog.Builder(MainActivity.this).
                        setMessage("茂名职业技术学院教务管理系统模拟登陆\n\n" +
                                "Android实训作业专用\n\n" +
                                "开发单位: 正方软件股份有限公司").
                        setPositiveButton(R.string.ok, null).
                        create();
            }
            alertDialog.show();
            alertDialog = null;
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
                                    Toast.makeText(MainActivity.this, "你已退出登录！", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MainActivity.this, "您尚未登录，请先登录！！", Toast.LENGTH_SHORT).show();
        }
//          关闭左右抽屉
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {// 双击退出软件
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (flag == false) {
                flag = true;
                Toast.makeText(getApplicationContext(), "再按一次退出软件",
                        Toast.LENGTH_SHORT).show();
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
}
