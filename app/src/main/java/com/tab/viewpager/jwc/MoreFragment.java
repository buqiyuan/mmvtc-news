package com.tab.viewpager.jwc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tab.viewpager.R;
import com.tab.viewpager.activity.MainActivity;
import com.tab.viewpager.activity.TimeTableActive;
import com.tab.viewpager.activity.WebViewActivity;

import static android.R.attr.id;
import static android.content.Context.MODE_PRIVATE;
import static com.tab.viewpager.R.id.iv_avatar;
import static com.tab.viewpager.R.id.tv_desc;
import static com.tab.viewpager.R.id.tv_name;
import static com.tab.viewpager.R.id.tv_timetable;
import static com.tab.viewpager.R.id.videoview;


public class MoreFragment extends BaseFragment implements View.OnClickListener {
    private TextView tv_timetable;
    private TextView tvGradeTest;
    private TextView tvAbout;
    private TextView tvExit;
    private AlertDialog alertDialog;
    private CustomVideoView videoview;

    @Override
    protected String getTitleName() {
        return getString(R.string.more);
    }

    @Override
    protected int resourceViewId() {
        return R.layout.fragment_more;
    }

    @Override
    public void onStart() {
        super.onStart();
        autoPlay();
    }

    @Override
    protected void initView(View view) {
        tv_timetable = (TextView) view.findViewById(R.id.tv_timetable);
        tv_timetable.setOnClickListener(this);
        tvGradeTest = (TextView) view.findViewById(R.id.tv_grade_test);
        tvGradeTest.setOnClickListener(this);
        tvAbout = (TextView) view.findViewById(R.id.tv_about);
        tvAbout.setOnClickListener(this);
        tvExit = (TextView) view.findViewById(R.id.tv_exit);
        tvExit.setOnClickListener(this);
        //加载视频资源控件
        videoview = (CustomVideoView) view.findViewById(R.id.videoview);
    }

    public void autoPlay(){
        //设置播放加载路径
        videoview.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.lol_hayato));
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
    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.tv_exit) {
//            退出登录
            if (alertDialog == null) {
                alertDialog = new AlertDialog.Builder(getActivity()).
                        setMessage("真的要狠心离开吗？").
                        setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getActivity(), "goodbye~！", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                                System.exit(0);
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
        } else if (viewId == R.id.tv_timetable) {
//            打开作息时间表
            Intent intent = new Intent(getActivity(), TimeTableActive.class);
            startActivity(intent);
        } else if (viewId == R.id.tv_grade_test) {
            Toast.makeText(getContext(), "啥也没有！占位置的", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.tv_about) {
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            startActivity(intent);
        }
    }
}
