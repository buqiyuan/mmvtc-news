package com.tab.viewpager.jwc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tab.viewpager.R;
import com.tab.viewpager.activity.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity implements OnClickListener {
    private EditText et_name, et_password, et_vertify;
    private ImageView iv_vertify;
    private Button btn_login;
    private CheckBox cb_isSave;
    private String url = "http://jwc.mmvtc.cn";
    private String urlStr = "http://jwc.mmvtc.cn/CheckCode.aspx";
    private String loginUrl = "http://jwc.mmvtc.cn/default2.aspx";
    private String cookie = "";
    private String name = "";
    private String password = "";
    private String vertify = "";
    private String viewstate = "";
    private String studentName = "";
    private String infoUrl = "";
    private String scoreUrl = "";
    private String xgPswUrl = "";
    private String courseUrl = "";
    private String refererUrl = "";
    //创建播放视频的控件对象
    private CustomVideoView videoview;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();// 初始化控件
        read();// 读取保存的账号密码
        initView();
        new Thread(vertifyRun).start();// 获取验证码
    }

    // 读取保存的账号密码
    private void read() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String strName = sp.getString("name", null);
        String strPassword = sp.getString("password", null);
        if (!(TextUtils.isEmpty(strName) && TextUtils.isEmpty(strPassword))) {// 账号密码不为空
            et_name.setText(strName);
            et_password.setText(strPassword);
        }
    }

    // 保存账号密码
    private void save(String name, String password) {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("name", name);
        editor.putString("password", password);
        editor.commit();
    }

    // 初始化控件
    private void init() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_password = (EditText) findViewById(R.id.et_password);
        et_vertify = (EditText) findViewById(R.id.et_vertify);
        iv_vertify = (ImageView) findViewById(R.id.iv_vertify);
        btn_login = (Button) findViewById(R.id.btn_login);
        cb_isSave = (CheckBox) findViewById(R.id.cb_isSave);

        iv_vertify.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }
    private void initView() {
        //加载视频资源控件
        videoview = (CustomVideoView) findViewById(R.id.videoview);
        //设置播放加载路径
        videoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_login));
        //播放
        videoview.start();
        //循环播放
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }
        });
    }

    //返回重启加载
    @Override
    protected void onRestart() {
        initView();
        super.onRestart();
    }

    //防止锁屏或者切出的时候，音乐在播放
    @Override
    protected void onStop() {
        videoview.stopPlayback();
        super.onStop();
    }

    private void loginFail(String tip) {
        Toast.makeText(LoginActivity.this, tip, Toast.LENGTH_LONG).show();
        et_vertify.setText("");
        new Thread(refreshRun).start();
    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    iv_vertify.setImageBitmap(bitmap);
                    break;
                case 2:
                    loginFail("验证码错误");
                    break;
                case 3:
                    loginFail("密码错误");
                    break;
                case 4:
                    loginFail("用户名不存在或未按照要求参加教学活动");
                    break;
                case 5:
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_LONG)
                            .show();

                    Intent intent = new Intent(LoginActivity.this,
                            MainActivity.class);
                    intent.putExtra("isLogin", true);
                    intent.putExtra("name", name);
                    intent.putExtra("studentName", studentName);
                    intent.putExtra("cookie", cookie);
                    intent.putExtra("viewstate", viewstate);
                    intent.putExtra("infoUrl", infoUrl);
                    intent.putExtra("xgPswUrl", xgPswUrl);
                    intent.putExtra("scoreUrl", scoreUrl);
                    intent.putExtra("courseUrl", courseUrl);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    break;
            }
        }
    };

    // 登陆
    public void doLogin() {
        name = et_name.getText().toString().trim();
        password = et_password.getText().toString().trim();
        vertify = et_vertify.getText().toString().trim();

        if (name.equals("") || password.equals("")) {
            Toast.makeText(this, "学号或者密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (vertify.length() != 4) {
            Toast.makeText(this, "请输入4位验证码", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(loginRun).start();
    }

    // 提交数据执行登陆
    private void login() {
        getViewstate();
        HttpPost httpPost = new HttpPost(loginUrl);
        httpPost.setHeader("Cookie", cookie);
        Log.e("Cookie", cookie);
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        list.add(new BasicNameValuePair("TextBox1", name));
        list.add(new BasicNameValuePair("TextBox2", password));
        list.add(new BasicNameValuePair("TextBox3", vertify));
        list.add(new BasicNameValuePair("RadioButtonList1_2", "学生"));
        list.add(new BasicNameValuePair("__VIEWSTATE", viewstate));
        list.add(new BasicNameValuePair("Button1", ""));
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list);
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(httpResponse.getEntity());
                checkLogin(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getViewstate() {
        HttpGet httpGet = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = client.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(httpResponse.getEntity());
                Document html = Jsoup.parse(content);
                Elements e = html.select("input[name=__VIEWSTATE]");
                viewstate = e.get(0).attr("value");
                Log.e("viewstate=>", viewstate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 判断用户登陆结果
    private void checkLogin(String content) {
        Message msg = new Message();
        if (content.indexOf("验证码不正确") != -1) {
            msg.what = 2;
        } else if (content.indexOf("密码错误") != -1) {
            msg.what = 3;
        } else if (content.indexOf("用户名不存在或未按照要求参加教学活动") != -1) {
            msg.what = 4;
        } else if (content.indexOf("欢迎您") != -1) {
            msg.what = 5;
            Document html = Jsoup.parse(content);
            Elements top = html.select("#headDiv .nav > li");
            studentName = html.select("span#xhxm").text();
            studentName = studentName.substring(0, studentName.length() - 2);
            String info = top.get(3).select(".sub > li:first-child a").attr("href");
            String course = top.get(4).select(".sub > li:first-child a").attr("href");
            String xgPsw = top.get(3).select(".sub > li:nth-child(2) a").attr("href");
            String cj = top.get(4).select(".sub > li:nth-child(4) a").attr("href");
            String encodeName = null;
            try {
                encodeName = URLEncoder.encode(studentName, "gb2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            infoUrl = info.replaceAll(studentName, encodeName);
            xgPswUrl = xgPsw.replaceAll(studentName, encodeName);
            scoreUrl = cj.replaceAll(studentName, encodeName);
            courseUrl = course.replaceAll(studentName, encodeName);
            Log.e("courseUrl",courseUrl);
            if (cb_isSave.isChecked()) {
                save(name, password);
            }
        }
        handler.sendMessage(msg);
    }
    private void getVertify() {
        try {
            HttpGet httpGet = new HttpGet(urlStr);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                cookie = httpResponse.getFirstHeader("set-cookie").getValue().split(";")[0];// 得到cookie
                InputStream is = httpResponse.getEntity().getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(is);// 得到bitmap格式验证码
                Message msg = new Message();
                msg.obj = bitmap;
                msg.what = 1;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 刷新验证码
    private void refreshVertify() {
        try {
            HttpGet httpGet = new HttpGet(urlStr);
            httpGet.setHeader("Cookie", cookie);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                InputStream is = httpResponse.getEntity().getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Message msg = new Message();
                msg.obj = bitmap;
                msg.what = 1;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Runnable vertifyRun = new Runnable() {

        @Override
        public void run() {
            getVertify();
        }
    };
    Runnable refreshRun = new Runnable() {

        @Override
        public void run() {
            refreshVertify();
        }
    };
    Runnable loginRun = new Runnable() {

        @Override
        public void run() {
            login();
        }
    };

    // 点击事件
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login:
                doLogin();
                break;

            case R.id.iv_vertify:
                new Thread(refreshRun).start();
                break;
        }
    }
}
