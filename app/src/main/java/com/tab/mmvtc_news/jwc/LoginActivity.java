package com.tab.mmvtc_news.jwc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hjq.toast.ToastUtils;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.activity.MainActivity;
import com.tab.mmvtc_news.okhttpUtil.OkHttpUtils;
import com.tab.mmvtc_news.okhttpUtil.callback.BitmapCallback;
import com.tab.mmvtc_news.okhttpUtil.callback.StringCallback;
import com.tab.mmvtc_news.views.CustomVideoView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.OkHttpClient;

//import cn.pedant.SweetAlert.SweetAlertDialog;

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
    SweetAlertDialog loginDialog = null;
    SweetAlertDialog errorDialog = null;
    SweetAlertDialog successDialog = null;
    private OkHttpClient client;
    //创建播放视频的控件对象
    private CustomVideoView videoview;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        errorDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        successDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        errorDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });
        init();// 初始化控件
        read();// 读取保存的账号密码
        initView();
//        new Thread(vertifyRun).start();// 获取验证码
        getVertify();// 获取验证码
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
        //设置为静音
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setVolume(0f, 0f);
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
//        new Thread(refreshRun).start();
        refreshVertify();
    }


    // 登陆
    public void doLogin() {
        name = et_name.getText().toString().trim();
        password = et_password.getText().toString().trim();
        vertify = et_vertify.getText().toString().trim();

        if (name.equals("") || password.equals("")) {
            errorDialog.setTitleText("学号或者密码不能为空").show();
            return;
        }
        if (vertify.length() != 4) {
            errorDialog.setTitleText("请输入4位验证码").show();
            return;
        }
        loginDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loginDialog.setTitleText("登陆中,请稍后片刻...");
        loginDialog.setCancelable(true);
        loginDialog.show();
        login();
    }

    // 提交数据执行登陆
    private void login() {
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        errorDialog.setTitleText("登录失败！").show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document html = Jsoup.parse(response);
                        Elements e = html.select("input[name=__VIEWSTATE]");
                        viewstate = e.get(0).attr("value");

                        OkHttpUtils
                                .post()
                                .url(loginUrl)
                                .addParams("TextBox1", name)
                                .addParams("TextBox2", password)
                                .addParams("TextBox3", vertify)
                                .addParams("RadioButtonList1_2", "学生")
                                .addParams("__VIEWSTATE", viewstate)
                                .addParams("Button1", "")
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        errorDialog.setTitleText("登录失败！").show();
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        checkLogin(response);
                                    }
                                });
                    }

                });
    }

    // 判断用户登陆结果
    private void checkLogin(String content) {
        loginDialog.dismiss();
        if (content.indexOf("验证码不正确") != -1) {
            errorDialog.setTitleText("验证码错误").show();
            refreshVertify();
        } else if (content.indexOf("密码错误") != -1) {
            errorDialog.setTitleText("密码错误").show();
            refreshVertify();
        } else if (content.indexOf("用户名不存在或未按照要求参加教学活动") != -1) {
            errorDialog.setTitleText("用户名不存在或未按照要求参加教学活动").show();
            refreshVertify();
        } else if (content.indexOf("欢迎您") != -1) {
            successDialog.setTitleText("登陆成功")
                    .show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
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
                    finish();
                }
            }, 1100);
            Document html = Jsoup.parse(content);
            Elements top = html.select("#headDiv .nav > li.top");
            studentName = html.select("span#xhxm").text();
            studentName = studentName.substring(0, studentName.length() - 2);
            String info = top.get(2).select(".sub > li:first-child a").attr("href");
            String course = top.get(3).select(".sub > li:first-child a").attr("href");
            String xgPsw = top.get(2).select(".sub > li:nth-child(2) a").attr("href");
            String cj = top.get(3).select(".sub > li:nth-child(4) a").attr("href");
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
            Log.e("infoUrl", infoUrl);
            Log.e("xgPswUrl", xgPswUrl);
            Log.e("scoreUrl", scoreUrl);
            Log.e("courseUrl", courseUrl);
            if (cb_isSave.isChecked()) {
                save(name, password);
            }
        }
    }


    private void getVertify() {
        OkHttpUtils
                .get()//
                .url(urlStr)//
                .tag(this)//
                .build()//
                .connTimeOut(20000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.show("获取验证码失败！");
                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int id) {
                        iv_vertify.setImageBitmap(bitmap);
                    }
                });
    }

    // 刷新验证码
    private void refreshVertify() {
        getVertify();
    }

    // 点击事件
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login:
                doLogin();
                break;

            case R.id.iv_vertify:
                refreshVertify();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
