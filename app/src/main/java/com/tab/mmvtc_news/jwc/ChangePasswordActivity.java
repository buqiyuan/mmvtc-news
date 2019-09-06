package com.tab.mmvtc_news.jwc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.okhttpUtil.OkHttpUtils;
import com.tab.mmvtc_news.okhttpUtil.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

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
    private SweetAlertDialog errorDialog;
    private SweetAlertDialog successDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        et_name = (EditText) findViewById(R.id.et_name);
        et_old_password = (EditText) findViewById(R.id.et_old_password);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_new_password_again = (EditText) findViewById(R.id.et_new_password_again);
        tv_change = (TextView) findViewById(R.id.tv_change);

        errorDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        successDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        errorDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//返回
            }
        });

        read();
        initEvnt();
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        xgPswUrl = sp.getString("xgPswUrl", null);
        cookie = sp.getString("cookie", null);
        getViewstate(xgPswUrl);
    }

    private void initEvnt() {
        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("修改密码", xgPswUrl);
                new_psw1 = et_new_password.getText().toString().trim();
                new_psw2 = et_new_password_again.getText().toString().trim();
                if (TextUtils.isEmpty(et_old_password.getText().toString().trim())){
                    errorDialog.setTitleText("旧密码不能为空！").show();
                    return;
                }
                if (new_psw1.equals("") || new_psw2.equals("")) {
                    errorDialog.setTitleText("新密码或重置密码不能为空").show();
                    return;
                }
                if (new_psw1.length() < 6 || new_psw2.length() < 6) {
                    errorDialog.setTitleText("密码长度不能小于 6 位！").show();
                    return;
                }
                changePsw();
            }
        });
    }

    private void changePsw() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Referer", xgPswUrl);
        OkHttpUtils
                .post()
                .url(xgPswUrl)
                .headers(headers)
                .addParams("TextBox1", et_name.getText().toString().trim())
                .addParams("TextBox2", et_old_password.getText().toString().trim())
                .addParams("TextBox3", new_psw1.trim())
                .addParams("TextBox4", new_psw2.trim())
                .addParams("__VIEWSTATE", viewstate)
                .addParams("Button1", "修  改")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("response",response);
                        checkChangePsw(response);
                    }
                });
    }
    private void read() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String strName = sp.getString("name", null);
        String strPassword = sp.getString("password", null);
        if (!(TextUtils.isEmpty(strName) && TextUtils.isEmpty(strPassword))) {// 账号密码不为空
            et_name.setText("当前账号：" + strName);
        }
    }

    private void changeFail(String tip) {
        if (tip == "修改成功"){
            successDialog.setTitleText(tip).show();
        }else {
            errorDialog.setTitleText(tip).show();
        }
        et_new_password.setText("");
        et_new_password_again.setText("");
    }

    private void checkChangePsw(String content) {
        Log.e("content",content);
        if (content.indexOf("两次输入的新密码不相同") != -1) {
            changeFail("两次输入的新密码不相同");
        } else if (content.indexOf("旧密码不正确") != -1) {
            changeFail("旧密码不正确");
        } else if (content.indexOf("修改成功") != -1) {
            changeFail("修改成功");
        }
    }

    private void getViewstate(String url) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Referer", url);

        OkHttpUtils
                .get()
                .url(url)
                .headers(headers)
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
                    }
                });
    }
}
