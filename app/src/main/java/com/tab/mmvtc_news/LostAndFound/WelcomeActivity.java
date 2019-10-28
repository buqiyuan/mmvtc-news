package com.tab.mmvtc_news.LostAndFound;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.hjq.toast.ToastUtils;
import com.tab.mmvtc_news.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by 卜启缘 on 2019/10/28.
 */
public class WelcomeActivity extends Activity {

    private String name;
    private String password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lost_and_found_welcome_activity);
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        name = sp.getString("name", "");//账号
        password = sp.getString("password", "");//密码
        //第一：默认初始化
//        Bmob.initialize(this, "5178f13fc41ca9e649d9c12eee9a6d68");
//        用户登录
        bmobUserAccountLogin();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//              /*  //获取缓存中的用户对象
//                BmobUser currentUser = BmobUser.getCurrentUser();
//                if (currentUser != null) {
//                    //允许用户使用应用，进入程序
//                    Intent intent = new Intent(WelcomeActivity.this, LostAndFoundActivity.class);
//                    startActivity(intent);
//                } else {
//                    //进入登录界面
//                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                }*/
//
//                //进入登录界面
//                Intent intent = new Intent(WelcomeActivity.this, LostAndFoundActivity.class);
//                startActivity(intent);
//                finish();
//
//            }
//        }, 2000);
    }
    private void bmobRegisterAccount() {
        BmobUser bmobUser = new BmobUser();
        bmobUser.setUsername(name);
        bmobUser.setPassword(password);
        bmobUser.signUp(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e == null) {
                    ToastUtils.show("恭喜，注册账号成功");
                    Log.e("bmob success","恭喜，注册账号成功");
                    bmobUserAccountLogin();
                } else if(e.getMessage().indexOf("already") != -1){
                    ToastUtils.show("用户已存在：" + e.getMessage());
                    Log.e("register fail:" , e.getMessage());
                    bmobUserAccountLogin();
                }else {
                    ToastUtils.show("注册失败：" + e.getMessage());
                    Log.e("register fail:" , e.getMessage());
                }
            }
        });
    }

    private void bmobUserAccountLogin() {

        if (TextUtils.isEmpty(name)||TextUtils.isEmpty(password)) {
//            ToastUtils.show("账号不能为空");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WelcomeActivity.this, LostAndFoundActivity.class);
                    startActivity(intent);
                    finish();
                }}, 1800);
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //BmobUser类为Bmob后端云提供类
                BmobUser bmobUser = new BmobUser();
                bmobUser.setUsername(name);
                bmobUser.setPassword(password);
                bmobUser.login(new SaveListener<BmobUser>() {
                    @Override
                    public void done(BmobUser bmobUser, BmobException e) {
                        if (e == null) {
                            //登录成功后进入主界面
                            Intent intent = new Intent(WelcomeActivity.this, LostAndFoundActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            bmobRegisterAccount();
                            ToastUtils.show(""+e.getMessage());
                        }
                    }
                });
            }
        }, 1800);
    }
}
