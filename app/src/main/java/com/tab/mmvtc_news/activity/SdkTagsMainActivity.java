package com.tab.mmvtc_news.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.utils.SerializableHashMap;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
/**
 * Created by 卜启缘 on 2019/10/8.
 */
public class SdkTagsMainActivity extends Activity implements View.OnClickListener, PlatformActionListener {

    private RelativeLayout loginWechat;
    private RelativeLayout loginSina;
    private Button login_test;
    private RelativeLayout loginQq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_tags_main);
        initView();
    }

    private void initView() {
        loginWechat = (RelativeLayout) findViewById(R.id.login_wechat);
        loginQq = (RelativeLayout) findViewById(R.id.login_qq);
        loginSina = (RelativeLayout) findViewById(R.id.login_sina);
        loginWechat.setOnClickListener(this);
        loginQq.setOnClickListener(this);
        loginSina.setOnClickListener(this);

        login_test = (Button) findViewById(R.id.login_test);
        login_test.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_wechat: {
                WeChat();
            } break;case R.id.login_qq: {
                QQ();
            } break;
            case R.id.login_sina: {
                Sina();
            } break;
            case R.id.login_test:
                Intent intent = new Intent();
                intent.setClass(this, TagsMyActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void Sina() {
        ShareSDK.setEnableAuthTag(true);
        Platform plat = ShareSDK.getPlatform("SinaWeibo");
        plat.removeAccount(true);
        //plat.SSOSetting(false);
        plat.setPlatformActionListener(this);
        if (plat.isClientValid()) {

        }
        if (plat.isAuthValid()) {

        }
        //plat.authorize();	//要功能，不要数据
        plat.showUser(null);    //要数据不要功能，主要体现在不会重复出现授权界面
    }

    private void WeChat() {
        Platform plat = ShareSDK.getPlatform(Wechat.NAME);
        ShareSDK.setEnableAuthTag(true);
        plat.removeAccount(true);
        //plat.SSOSetting(false);
        plat.setPlatformActionListener(this);
        if (plat.isClientValid()) {

        }
        if (plat.isAuthValid()) {

        }
        //plat.authorize();	//要功能，不要数据
        plat.showUser(null);    //要数据不要功能，主要体现在不会重复出现授权界面
    }

    private void QQ() {
        Platform plat = ShareSDK.getPlatform(QQ.NAME);
        ShareSDK.setEnableAuthTag(true);
        plat.removeAccount(true);
        //plat.SSOSetting(false);
        plat.setPlatformActionListener(this);
        if (plat.isClientValid()) {

        }
        if (plat.isAuthValid()) {

        }
        //plat.authorize();	//要功能，不要数据
        plat.showUser(null);    //要数据不要功能，主要体现在不会重复出现授权界面
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
        //通过打印res数据看看有哪些数据是你想要的
        if (action == Platform.ACTION_USER_INFOR) {
            PlatformDb platDB = platform.getDb();//获取数平台数据DB
            //通过DB获取各种数据
            platDB.getToken();
            platDB.getUserGender();
            platDB.getUserIcon();
            platDB.getUserId();
            platDB.getUserName();
        }

        hashMap.put("userTags", platform.getDb().get("userTags")); //SDK+ tags
        Log.e("SDK+", " SdkTagsMainActivity platform: " + platform +
                " i: " + action + " hashMap " + hashMap);
        Intent inent = new Intent();
        SerializableHashMap serMap = new SerializableHashMap();
        serMap.setMap(hashMap);
        Bundle bundle = new Bundle();
        bundle.putSerializable("serMap", serMap);
        inent.putExtras(bundle);
        inent.setClass(this, TagsMyActivity.class);
        startActivity(inent);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Log.e("SDK+", " SdkTagsMainActivity onError platform: " + platform +
                " i: " + i + " throwable " + throwable.getMessage());
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Log.e("SDK+", " SdkTagsMainActivity onCancel platform: " + platform +
                " i: " + i);
    }
}
