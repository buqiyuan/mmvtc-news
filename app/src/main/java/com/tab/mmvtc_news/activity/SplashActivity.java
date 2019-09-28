package com.tab.mmvtc_news.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.views.CustomVideoView;

public class SplashActivity extends Activity {
    private CustomVideoView videoview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
//加载视频资源控件
//        videoview = (CustomVideoView) findViewById(R.id.videoview);
//        //设置播放加载路径
//        videoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_splash));
//        //播放
//        videoview.start();
//        //循环播放
//        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                videoview.start();
//            }
//        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
