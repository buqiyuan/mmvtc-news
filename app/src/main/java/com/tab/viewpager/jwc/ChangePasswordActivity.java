package com.tab.viewpager.jwc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.tab.viewpager.R;
import com.tab.viewpager.activity.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import static com.tab.viewpager.R.id.toolbar;

/**
 * Created by bqy on 2019/7/6.
 */

public class ChangePasswordActivity extends Activity {
    private EditText et_name;
    private EditText et_old_password;
    private EditText et_new_password;
    private EditText et_new_password_again;
    private String xgPswUrl;
    private String cookie;
    private String new_psw1;
    private String new_psw2;
    private String viewstate;
    private SimpleAdapter adapter;
    private TextView tv_change;
    private ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        et_name = (EditText) findViewById(R.id.et_name);
        et_old_password = (EditText) findViewById(R.id.et_old_password);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_new_password_again = (EditText) findViewById(R.id.et_new_password_again);
        tv_change = (TextView) findViewById(R.id.tv_change);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//返回
            }
        });

        read();
        initEvnt();
        new Thread(viewstateRun).start();
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        xgPswUrl = sp.getString("xgPswUrl", null);
        cookie = sp.getString("cookie", null);
    }
private void initEvnt(){
    tv_change.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e("修改密码",xgPswUrl);
            new_psw1 = et_new_password.getText().toString().trim();
            new_psw2 = et_new_password_again.getText().toString().trim();
            if (new_psw1.equals("") || new_psw2.equals("")){
                Toast.makeText(ChangePasswordActivity.this,"新密码或重置密码不能为空！",Toast.LENGTH_SHORT).show();
                return;
            }
            if (new_psw1.length() < 6 || new_psw2.length() < 6){
                Toast.makeText(ChangePasswordActivity.this,"密码长度不能小于 6 位！",Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(contentRun).start();
        }
    });
}
    // handler更新界面
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:// 显示验证码
                    changeFail("两次输入的新密码不相同");
                    break;
                case 2:// 验证码错误
                    changeFail("旧密码不正确");
                    break;
                case 3:// 密码错误
                    changeFail("修改成功");
                    break;
            }
        }
    };

    Runnable viewstateRun = new Runnable(){

        @Override
        public void run() {
            getViewstate(xgPswUrl);
        }
    };
    Runnable contentRun = new Runnable() {

        @Override
        public void run() {
            Log.e("viewstate=>", viewstate);
            Log.e("cookie=>", cookie);
            Connection connection = Jsoup.connect(xgPswUrl)
                    .method(Connection.Method.POST)
                    .header("Cookie",cookie)
                    .data("TextBox1", et_name.getText().toString().trim())
                    .data("TextBox2", et_old_password.getText().toString().trim())
                    .data("TextBox3", new_psw1.trim())
                    .data("TextBox4", new_psw2.trim())
                    .data("__VIEWSTATE", viewstate)
                    .data("Button1", "修  改")
                    .referrer(xgPswUrl);
            try {
                String content = connection.execute().body();
                checkChangePsw(content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 判断用户登陆结果
    };
    private void read() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String strName = sp.getString("name", null);
        String strPassword = sp.getString("password", null);
        if (!(TextUtils.isEmpty(strName) && TextUtils.isEmpty(strPassword))) {// 账号密码不为空
            et_name.setText("当前账号："+strName);
        }
    }
    private void changeFail(String tip){
        Toast.makeText(ChangePasswordActivity.this, tip, Toast.LENGTH_LONG).show();
        et_new_password.setText("");
        et_new_password_again.setText("");
    }
    private void checkChangePsw(String content) {
        Message msg = new Message();
        if (content.indexOf("两次输入的新密码不相同") != -1) {
            msg.what = 1;
        } else if (content.indexOf("旧密码不正确") != -1) {
            msg.what = 2;
        } else if (content.indexOf("修改成功") != -1) {
            msg.what = 3;
            Document html = Jsoup.parse(content);
            Elements top = html.select("#headDiv .nav > li");
        }
        handler.sendMessage(msg);
    }
    private void getViewstate(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Cookie", cookie);
        httpGet.setHeader("Referer", url);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = client.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(httpResponse.getEntity());
                Document html = Jsoup.parse(content);
                Elements e = html.select("input[name=__VIEWSTATE]");
                viewstate = e.get(0).attr("value");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
