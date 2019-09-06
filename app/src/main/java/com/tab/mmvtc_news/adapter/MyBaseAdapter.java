package com.tab.mmvtc_news.adapter;


import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.tab.mmvtc_news.R;

//import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.media.CamcorderProfile.get;


public class MyBaseAdapter extends BaseAdapter {

    private List<Map<String, String>> list;
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
//        ViewHolder viewHolder = null;
//        if (convertView == null) {
        convertView = mInflater.inflate(R.layout.course_item, null, true);
//            viewHolder = new ViewHolder();
//            private String[] keyArr = new String[]{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        final TextView monday = (TextView) convertView.findViewById(R.id.tv_monday);
        final TextView tuesday = (TextView) convertView.findViewById(R.id.tv_tuesday);
        final TextView wednesday = (TextView) convertView.findViewById(R.id.tv_wednesday);
        final TextView thursday = (TextView) convertView.findViewById(R.id.tv_thursday);
        final TextView friday = (TextView) convertView.findViewById(R.id.tv_friday);
        final TextView saturday = (TextView) convertView.findViewById(R.id.tv_saturday);
        final TextView sunday = (TextView) convertView.findViewById(R.id.tv_sunday);
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
        Random random = new Random();
        if (!TextUtils.isEmpty(map.get("monday").trim()) && monday.getText().toString().trim().length() > 6) {
            monday.setBackgroundResource(courseBgColors[random.nextInt(7)]);
        }
        if (!TextUtils.isEmpty(map.get("tuesday").trim()) && tuesday.getText().toString().trim().length() > 6) {
            tuesday.setBackgroundResource(courseBgColors[random.nextInt(7)]);
        }
        if (!TextUtils.isEmpty(map.get("wednesday").trim()) && wednesday.getText().toString().trim().length() > 6) {
            wednesday.setBackgroundResource(courseBgColors[random.nextInt(7)]);
        }
        if (!TextUtils.isEmpty(map.get("thursday").trim()) && thursday.getText().toString().trim().length() > 6) {
            thursday.setBackgroundResource(courseBgColors[random.nextInt(7)]);
        }
        if (!TextUtils.isEmpty(map.get("friday").trim()) && friday.getText().toString().trim().length() > 6) {
            friday.setBackgroundResource(courseBgColors[random.nextInt(7)]);
        }
        if (!TextUtils.isEmpty(map.get("saturday").trim()) && saturday.getText().toString().trim().length() > 6) {
            saturday.setBackgroundResource(courseBgColors[random.nextInt(7)]);
        }
        if (!TextUtils.isEmpty(map.get("sunday").trim()) && sunday.getText().toString().trim().length() > 6) {
            sunday.setBackgroundResource(courseBgColors[random.nextInt(7)]);
        }


        final View finalConvertView = convertView;
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) finalConvertView.findViewById(v.getId());
                if (TextUtils.isEmpty(tv.getText().toString().trim())){
                    return;
                }
                switch (v.getId()) {
                    case R.id.tv_monday:
                        new SweetAlertDialog(ctx)
                                .setTitleText("课程详情")
                                .setContentText(monday.getText().toString())
                                .show();
                        break;
                    case R.id.tv_tuesday:
                        new SweetAlertDialog(ctx)
                                .setTitleText("课程详情")
                                .setContentText(tuesday.getText().toString())
                                .show();
                        break;
                    case R.id.tv_wednesday:
                        new SweetAlertDialog(ctx)
                                .setTitleText("课程详情")
                                .setContentText(wednesday.getText().toString())
                                .show();
                        break;
                    case R.id.tv_thursday:
                        new SweetAlertDialog(ctx)
                                .setTitleText("课程详情")
                                .setContentText(thursday.getText().toString())
                                .show();
                        break;
                    case R.id.tv_friday:
                        new SweetAlertDialog(ctx)
                                .setTitleText("课程详情")
                                .setContentText(friday.getText().toString())
                                .show();
                        break;
                    case R.id.tv_saturday:
                        new SweetAlertDialog(ctx)
                                .setTitleText("课程详情")
                                .setContentText(saturday.getText().toString())
                                .show();
                        break;
                    case R.id.tv_sunday:
                        new SweetAlertDialog(ctx)
                                .setTitleText("课程详情")
                                .setContentText(sunday.getText().toString())
                                .show();
                        break;
                }
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
}