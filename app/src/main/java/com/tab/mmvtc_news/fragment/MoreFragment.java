package com.tab.mmvtc_news.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.tab.mmvtc_news.LostAndFound.LostAndFoundActivity;
import com.tab.mmvtc_news.LostAndFound.WelcomeActivity;
import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.activity.TimeTableActive;
import com.tab.mmvtc_news.activity.WebViewActivity;
import com.tab.mmvtc_news.utils.CProgressDialogUtils;
import com.tab.mmvtc_news.views.CustomVideoView;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.proxy.impl.DefaultUpdateChecker;

import cn.bmob.v3.Bmob;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by 卜启缘 on 2019/10/8.
 */
public class MoreFragment extends BaseFragment implements View.OnClickListener {
    private TextView tv_timetable;
    private TextView tvPanorama;
    private TextView tvGuide;
    private TextView tvAbout;
    private TextView tvExit;
    private AlertDialog alertDialog;
    private CustomVideoView videoview;
    private TextView tvUpdate;
    //    private String mUpdateUrl = "https://raw.githubusercontent.com/xuexiangjys/XUpdate/master/jsonapi/update_test.json";
    private String mUpdateUrl = "https://buqiyuan.xyz/mmvtc-news/app_update.json";
    private TextView tvShare;
    private TextView tvLostFound;

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
        tvPanorama = (TextView) view.findViewById(R.id.tv_panorama);
        tvPanorama.setOnClickListener(this);
        tvGuide = (TextView) view.findViewById(R.id.tv_guide);
        tvGuide.setOnClickListener(this);
        tvLostFound = (TextView) view.findViewById(R.id.tv_lost_found);
        tvLostFound.setOnClickListener(this);
        tvAbout = (TextView) view.findViewById(R.id.tv_about);
        tvAbout.setOnClickListener(this);
        tvUpdate = (TextView) view.findViewById(R.id.tv_update);
        tvUpdate.setOnClickListener(this);
        tvShare = (TextView) view.findViewById(R.id.tv_share);
        tvShare.setOnClickListener(this);
        tvExit = (TextView) view.findViewById(R.id.tv_exit);
        tvExit.setOnClickListener(this);
        //加载视频资源控件
        videoview = (CustomVideoView) view.findViewById(R.id.videoview);
    }

    public void autoPlay() {
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
        } else if (viewId == R.id.tv_lost_found) {
//            打开失物招领
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.tv_panorama) {
            //  打开校园全景
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtra("url", "https://720yun.com/t/690jedektn3?scene_id=25472772#scene_id=15527772");
            intent.putExtra("title", "全景校园");
            startActivity(intent);
        } else if (viewId == R.id.tv_guide) {
            //  打开来校指南
//            Intent intent = new Intent(getActivity(), WebViewActivity.class);
//            intent.putExtra("url", "https://websites.mmvtc.cn/zsw/index.php?url=site/lxzn");
//            intent.putExtra("title", "来校指南");
//            startActivity(intent);
            new SweetAlertDialog(getActivity())
                    .setTitleText("公车路线")
                    .setContentText("文明北校区(职业技术学院站)：301专线、302专线\n" +
                            "(经济管理系、人文与传媒系（思政部）所在校区)\n" +
                            "\n" +
                            "水东湾新城校区(茂职院站)：201专线、301专线、303专线、309专线、312专线\n" +
                            "(土木工程系、机电信息系、化学工程系、计算机工程系所在校区)")
                    .show();
        } else if (viewId == R.id.tv_about) {
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtra("url", "https://buqiyuan.xyz");
            intent.putExtra("title", "作者博客");
            startActivity(intent);
        } else if (viewId == R.id.tv_update) {
            XUpdate.newBuild(getActivity())
                    .updateUrl(mUpdateUrl)
                    .updateChecker(new DefaultUpdateChecker() {
                        @Override
                        public void onBeforeCheck() {
                            super.onBeforeCheck();
                            SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean("isAutoUpdate", false);
                            editor.commit();
                            CProgressDialogUtils.showProgressDialog(getActivity(), "获取版本信息中...");
                        }

                        @Override
                        public void onAfterCheck() {
                            super.onAfterCheck();
                            CProgressDialogUtils.cancelProgressDialog(getActivity());
                        }
                    })
                    .supportBackgroundUpdate(true)
                    .update();
        } else if (viewId == R.id.tv_share) {
            OnekeyShare oks = new OnekeyShare();
            // title标题，微信、QQ和QQ空间等平台使用
            oks.setTitle("分享");
            // titleUrl QQ和QQ空间跳转链接
            oks.setTitleUrl("https://buqiyuan.xyz/my-demo/mmvtc_news.apk");
            // text是分享文本，所有平台都需要这个字段
            oks.setText("茂职校园信息APP\n——最好的掌上校园APP");
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//            oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
            oks.setImageUrl("https://s2.ax1x.com/2019/10/28/K65pv9.png");//确保SDcard下面存在此张图片
            // url在微信、微博，Facebook等平台中使用
            oks.setUrl("https://buqiyuan.xyz/my-demo/mmvtc_news.apk");
            // comment是我对这条分享的评论，仅在人人网使用
            oks.setComment("茂职校园信息APP");
            // 启动分享GUI
            oks.show(getActivity());
        }
    }
}
