package com.tab.mmvtc_news.adapter;


import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blog.www.guideview.Guide;
import com.blog.www.guideview.GuideBuilder;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.activity.MainActivity;
import com.tab.mmvtc_news.component.MutiComponent;
import com.tab.mmvtc_news.component.SimpleComponent;
import com.tab.mmvtc_news.jwc.CourseActivity;
import com.tab.mmvtc_news.utils.SharedPreferencesUtil;

//import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.media.CamcorderProfile.get;


public class MyBaseAdapter extends BaseAdapter {
private int showTimes = 0;
    private List<Map<String, String>> list;
    private Boolean flag = true;
    private Context ctx;
    private LayoutInflater mInflater;
    private int[] courseBgColors = {R.drawable.course_item_bg1, R.drawable.course_item_bg2, R.drawable.course_item_bg3,
            R.drawable.course_item_bg4, R.drawable.course_item_bg5, R.drawable.course_item_bg6, R.drawable.course_item_bg7};

    public MyBaseAdapter(Context context, List<Map<String, String>> list) {
        ctx = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
        convertView = mInflater.inflate(R.layout.course_item, null, true);
//            viewHolder = new ViewHolder();
//            private String[] keyArr = new String[]{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        LinearLayout ll_course_header = (LinearLayout) convertView.findViewById(R.id.ll_course_header);
        TextView monday = (TextView) convertView.findViewById(R.id.tv_monday);
        TextView tuesday = (TextView) convertView.findViewById(R.id.tv_tuesday);
        TextView wednesday = (TextView) convertView.findViewById(R.id.tv_wednesday);
        TextView thursday = (TextView) convertView.findViewById(R.id.tv_thursday);
        TextView friday = (TextView) convertView.findViewById(R.id.tv_friday);
        TextView saturday = (TextView) convertView.findViewById(R.id.tv_saturday);
        TextView sunday = (TextView) convertView.findViewById(R.id.tv_sunday);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }

        Map<String, String> map = list.get(position);
        monday.setText(map.get("monday"));
        tuesday.setText(map.get("tuesday"));
        wednesday.setText(map.get("wednesday"));
        thursday.setText(map.get("thursday"));
        friday.setText(map.get("friday"));
        saturday.setText(map.get("saturday"));
        sunday.setText(map.get("sunday"));
        Log.e("viewHolder", tuesday.getText().toString());

        setRandomBgColor(map, "monday", monday, convertView);
        setRandomBgColor(map, "tuesday", tuesday, convertView);
        setRandomBgColor(map, "wednesday", wednesday, convertView);
        setRandomBgColor(map, "thursday", thursday, convertView);
        setRandomBgColor(map, "friday", friday, convertView);
        setRandomBgColor(map, "saturday", saturday, convertView);
        setRandomBgColor(map, "sunday", sunday, convertView);

        View finalConvertView = convertView;
        View.OnClickListener mOnClickListener = v -> {
            TextView tv = (TextView) finalConvertView.findViewById(v.getId());
            if (TextUtils.isEmpty(tv.getText().toString().trim())) {
                return;
            }
            switch (v.getId()) {
                case R.id.tv_monday:
                    showAlertDialog(monday);
                    break;
                case R.id.tv_tuesday:
                    showAlertDialog(tuesday);
                    break;
                case R.id.tv_wednesday:
                    showAlertDialog(wednesday);
                    break;
                case R.id.tv_thursday:
                    showAlertDialog(thursday);
                    break;
                case R.id.tv_friday:
                    showAlertDialog(friday);
                    break;
                case R.id.tv_saturday:
                    showAlertDialog(saturday);
                    break;
                case R.id.tv_sunday:
                    showAlertDialog(sunday);
                    break;
            }
        };
        monday.setOnClickListener(mOnClickListener);
        tuesday.setOnClickListener(mOnClickListener);
        wednesday.setOnClickListener(mOnClickListener);
        thursday.setOnClickListener(mOnClickListener);
        friday.setOnClickListener(mOnClickListener);
        saturday.setOnClickListener(mOnClickListener);
        sunday.setOnClickListener(mOnClickListener);
        return convertView;
    }

    static class ViewHolder {
        public TextView monday;
        public TextView tuesday;
        public TextView wednesday;
        public TextView thursday;
        public TextView friday;
        public TextView saturday;
        public TextView sunday;
    }

    //    设置随机背景颜色
    private void setRandomBgColor(Map<String, String> map, String day, TextView tv, View view) {
        Random random = new Random();
        if (!TextUtils.isEmpty(map.get(day).trim()) && tv.getText().toString().trim().length() > 6) {
            tv.setBackgroundResource(courseBgColors[random.nextInt(7)]);
//            如果是第一次加载课程表
            if (flag) {
                boolean isFirstOpen = SharedPreferencesUtil.getBoolean(ctx, SharedPreferencesUtil.FIRST_OPEN_COURSE, true);
//                 如果是第一次进入到主界面，则先开始新手引导页
                if (isFirstOpen) {
                    flag = false;
                    final View finalView = view;
                    view.post(() -> showGuideView(finalView));
                }
            }
        }
    }

    //    弹出课程详情框
    private void showAlertDialog(TextView tv) {
        new SweetAlertDialog(ctx)
                .setTitleText("课程详情")
                .setContentText(tv.getText().toString())
                .show();
    }

    private void showGuideView(View targetView) {
        GuideBuilder builder = new GuideBuilder();
        builder.setTargetView(targetView)
                .setAlpha(150)
                .setHighTargetCorner(20)
                .setHighTargetPadding(10)
                .setOverlayTarget(false)
                .setOutsideTouchable(false);
        builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
                // 如果切换到后台，就设置下次不进入新手引导页
                SharedPreferencesUtil.putBoolean(ctx, SharedPreferencesUtil.FIRST_OPEN_COURSE, false);
            }
        });

        builder.addComponent(new MutiComponent());
        Guide guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(true);
        guide.show((Activity) ctx);
    }
}