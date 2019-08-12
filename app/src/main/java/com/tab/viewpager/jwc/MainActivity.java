//package com.tab.viewpager.jwc;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffXfermode;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.widget.ImageView;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.RadioGroup.OnCheckedChangeListener;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class MainActivity extends FragmentActivity implements OnCheckedChangeListener, OnPageChangeListener {
//
//    private String studentName;
//    private static String cookie;
//    private String name;
//    private String url = "http://jwc.mmvtc.cn/";
//    private static String refererUrl = "";
//    private static String infoUrl = "";
//    private static String scoreUrl = "";
//    private static String xgPswUrl = "";
//    private static String courseUrl = "";
//    private ViewPager pager;
//    private RadioGroup radioGroup;
//    private RadioButton rb_me, rb_score;
//    private RadioButton rb_more;
//    private TextView tv_studentName;
//    private TextView page_title;
//    private FragmentAdapter adapter;
//    private boolean flag = false;
//    private ImageView iv_avatar;
//    private RadioButton rb_course;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        Intent intent = getIntent();
//        name = intent.getStringExtra("name");
//        studentName = intent.getStringExtra("studentName");
//        cookie = intent.getStringExtra("cookie");
//        infoUrl = url + intent.getStringExtra("infoUrl");
//        xgPswUrl = url + intent.getStringExtra("xgPswUrl");
//        scoreUrl = url + intent.getStringExtra("scoreUrl");
//        courseUrl = url + intent.getStringExtra("courseUrl");
//        refererUrl = "http://jwc.mmvtc.cn/xs_main.aspx?xh=" + name;
//        Log.i("refererUrl:", refererUrl);
//        init();
//        new Thread(avatarRun).start();
//    }
//
//    //初始化控件
//    private void init() {
//        radioGroup = (RadioGroup) findViewById(R.id.rg_tab_bar);
//        rb_me = (RadioButton) findViewById(R.id.rb_me);
//        rb_score = (RadioButton) findViewById(R.id.rb_score);
//        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
//        rb_more = (RadioButton) findViewById(R.id.rb_more);
//        rb_course = (RadioButton) findViewById(R.id.rb_course);
//        tv_studentName = (TextView) findViewById(R.id.tv_studentName);
//        page_title = (TextView) findViewById(R.id.page_title);
//
//        tv_studentName.setText(studentName);
//        rb_me.setChecked(true);
//        radioGroup.setOnCheckedChangeListener(this);
//
//        List<Fragment> fragments = new ArrayList<Fragment>();
//        fragments.add(new MeFragment());
//        fragments.add(new ScoreFragment());
//        fragments.add(new CourseFragment());
//        fragments.add(new MoreFragment());
//        adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
//
//        pager = (ViewPager) findViewById(R.id.viewpager);
//        pager.setOffscreenPageLimit(1);
//        pager.setAdapter(adapter);
//        pager.setCurrentItem(0);
//        pager.setOnPageChangeListener(this);
//
//    }
//
//    Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            Bitmap bitmap = (Bitmap) msg.obj;
//            Bitmap avatar = toRoundCornerImage(bitmap, 0);
//            Log.e("avatar", avatar.toString());
//            iv_avatar.setImageBitmap(avatar);
//        }
//    };
//    Runnable avatarRun = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                HttpGet httpGet = new HttpGet(infoUrl);
//                httpGet.setHeader("Cookie", cookie);
//                httpGet.setHeader("Referer", infoUrl);
//                HttpClient client = new DefaultHttpClient();
//                HttpResponse httpResponse = client.execute(httpGet);
//                if (httpResponse.getStatusLine().getStatusCode() == 200) {
//                    String html = EntityUtils.toString(httpResponse.getEntity());
//                    Document dom = Jsoup.parse(html);
//                    Log.e("imaUrl", url + dom.select("#xszp").attr("src"));
//                    httpGet = new HttpGet(url + dom.select("#xszp").attr("src"));
//                    httpGet.setHeader("Cookie", cookie);
//                    httpGet.setHeader("Referer", infoUrl);
//                    httpResponse = client.execute(httpGet);
//                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
//                        InputStream is = httpResponse.getEntity().getContent();
//                        Bitmap bitmap = BitmapFactory.decodeStream(is);
//                        Message msg = new Message();
//                        msg.obj = bitmap;
//                        handler.sendMessage(msg);
//                    }
//
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    /**
//     * 获取圆角位图的方法
//     *
//     * @param bitmap 需要转化成圆角的位图
//     * @param pixels 圆角的度数，数值越大，圆角越大
//     * @return 处理后的圆角位图
//     */
//    public static Bitmap toRoundCornerImage(Bitmap bitmap, int pixels) {
//        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//        final int color = 0xff424242;
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//        final RectF rectF = new RectF(rect);
//        final float roundPx = pixels;
//        // 抗锯齿
//        paint.setAntiAlias(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(color);
//        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, rect, rect, paint);
//        return output;
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int arg0) {
//        if (arg0 == 2) {
//            switch (pager.getCurrentItem()) {
//                case 0:
//                    rb_me.setChecked(true);
//                    break;
//                case 1:
//                    rb_score.setChecked(true);
//                    break;
//                case 2:
//                    rb_course.setChecked(true);
//                    break;
//                case 3:
//                    rb_more.setChecked(true);
//                    break;
//            }
//        }
//    }
//
//    @Override
//    public void onPageScrolled(int arg0, float arg1, int arg2) {
//    }
//
//    @Override
//    public void onPageSelected(int arg0) {
//    }
//
//    @Override
//    public void onCheckedChanged(RadioGroup arg0, int arg1) {//单选按钮选择事件
//        switch (arg1) {
//            case R.id.rb_me:
//                pager.setCurrentItem(0);
//                page_title.setText("个人信息");
//                break;
//            case R.id.rb_score:
//                pager.setCurrentItem(1);
//                page_title.setText("历年成绩");
//                break;
//            case R.id.rb_course:
//                pager.setCurrentItem(2);
//                page_title.setText("课程表");
//                break;
//            case R.id.rb_more:
//                pager.setCurrentItem(3);
//                page_title.setText("更多");
//                break;
//        }
//    }
//
//
//    public static String getRefererUrl() {
//        return refererUrl;
//    }
//
//    public static String getInfoUrl() {
//        return infoUrl;
//    }
//
//    public static String getScoreUrl() {
//        return scoreUrl;
//    }
//    public static String getCourseUrl() {
//        return courseUrl;
//    }
//
//    public static String getXgPswUrl() {
//        return xgPswUrl;
//    }
//
//    public static String getCookie() {
//        return cookie;
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {//双击退出软件
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (flag == false) {
//                flag = true;
//                Toast.makeText(getApplicationContext(), "再按一次退出软件", Toast.LENGTH_SHORT).show();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        flag = false;
//                    }
//                }, 2000);
//            } else {
//                finish();
//                System.exit(0);
//            }
//        }
//        return false;
//    }
//}
