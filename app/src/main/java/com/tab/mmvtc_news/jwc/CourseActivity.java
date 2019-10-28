package com.tab.mmvtc_news.jwc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.adapter.MyBaseAdapter;
import com.tab.mmvtc_news.okhttpUtil.OkHttpUtils;
import com.tab.mmvtc_news.okhttpUtil.callback.StringCallback;
import com.tab.mmvtc_news.utils.LogUtil;
import com.tab.mmvtc_news.views.NoScrollListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
/**
 * Created by 卜启缘 on 2019/10/8.
 */
public class CourseActivity extends AppCompatActivity {

    private NoScrollListView listViewLeft;
    private NoScrollListView listViewRight;
    private MyBaseAdapter adapter;
    private List<Map<String, String>> courseList = new ArrayList<Map<String, String>>();
    private String refererUrl = "";
    private String courseUrl = "";
    private String cookie = "";
    private String viewstate = "";
    private int[] itemArr = new int[]{R.id.tv_monday, R.id.tv_tuesday, R.id.tv_wednesday, R.id.tv_thursday,
            R.id.tv_friday, R.id.tv_saturday, R.id.tv_sunday};
    private String[] keyArr = new String[]{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
    private String[] data = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
    private int[] courseColors = {R.color.courseBackground1, R.color.courseBackground2,
            R.color.courseBackground3, R.color.courseBackground4, R.color.courseBackground5,
            R.color.courseBackground6, R.color.courseBackground7};
    private TextView tv_title;
    private LinearLayout ll_load;
    private List<String> ctype = new ArrayList<String>();
    private ArrayAdapter<String> spinnerAdapter;
    private Spinner spinner_xn;
    private Spinner spinner_xq;
    private AlertDialog.Builder chooseDialogBuilder;
    private Dialog chooseDialog;
    private TextView tv_filter;
    private String nowYear;
    private String nowSemester;
    private List<String> year = new ArrayList<String>();
    private String __VIEWSTATE;
    private List<String> semester = new ArrayList<String>();
    private TextView tv_error;
    private TextView tv_monday;
    private TextView tv_tuesday;
    private TextView tv_wednesday;
    private TextView tv_thursday;
    private TextView tv_friday;
    private TextView tv_saturday;
    private TextView tv_sunday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {
            setContentView(R.layout.fragment_course);
            initView();
            setCurrentDay();
            chooseDialogBuilder = new AlertDialog.Builder(this);
            tv_filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chooseDialog == null) {
                        chooseDialog = chooseDialogBuilder.show();
                    } else {
                        chooseDialog.show();
                    }
                }
            });
            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            refererUrl = sp.getString("refererUrl", null);
            courseUrl = sp.getString("courseUrl", null);
            cookie = sp.getString("cookie", null);
            listViewRight = (NoScrollListView) findViewById(R.id.listViewRight);
            listViewLeft = (NoScrollListView) findViewById(R.id.listViewLeft);
//            adapter = new MySimpleAdapter(CourseActivity.this, courseList, R.layout.course_item, keyArr, itemArr);
            adapter = new MyBaseAdapter(CourseActivity.this, courseList);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(CourseActivity.this, R.layout.course_time_item, data);//适配器

            listViewRight.setAdapter(adapter);
            listViewLeft.setAdapter(adapter2);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationOnClickListener(v -> {
                finish();//返回
            });
            new Thread(runnable).start();
        }
    }

    private void initView() {
        ll_load = (LinearLayout) findViewById(R.id.ll_load);
        tv_error = (TextView) findViewById(R.id.tv_error);
        ll_load.setVisibility(View.VISIBLE);
        tv_filter = (TextView) findViewById(R.id.tv_filter);
        tv_title = (TextView) findViewById(R.id.tv_title);
//    "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday
        tv_monday = (TextView) findViewById(R.id.tv_monday);
        tv_tuesday = (TextView) findViewById(R.id.tv_tuesday);
        tv_wednesday = (TextView) findViewById(R.id.tv_wednesday);
        tv_thursday = (TextView) findViewById(R.id.tv_thursday);
        tv_friday = (TextView) findViewById(R.id.tv_friday);
        tv_saturday = (TextView) findViewById(R.id.tv_saturday);
        tv_sunday = (TextView) findViewById(R.id.tv_sunday);
    }

    private void setCurrentDay() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 EEEE");
        format = new SimpleDateFormat("EEEE");
        Log.e("time", "time=" + format.format(date));
        switch (format.format(date)) {
            case "星期一":
                tv_monday.setBackgroundResource(R.drawable.week_active);
                break;
            case "星期二":
                tv_tuesday.setBackgroundResource(R.drawable.week_active);
                break;
            case "星期三":
                tv_wednesday.setBackgroundResource(R.drawable.week_active);
                break;
            case "星期四":
                tv_thursday.setBackgroundResource(R.drawable.week_active);
                break;
            case "星期五":
                tv_friday.setBackgroundResource(R.drawable.week_active);
                break;
            case "星期六":
                tv_saturday.setBackgroundResource(R.drawable.week_active);
                break;
            case "星期日":
                tv_sunday.setBackgroundResource(R.drawable.week_active);
                break;
        }
    }

    private final MyHandler mHandler = new MyHandler(this);

    static class MyHandler extends Handler {

        private final WeakReference<CourseActivity> mActivity;

        public MyHandler(CourseActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (mActivity.get() == null) {
                return;
            }
            final CourseActivity activity = mActivity.get();
            switch (msg.what) {
                case 1:
                    activity.tv_error.setVisibility(View.GONE);
                    activity.ll_load.setVisibility(View.GONE);
                    activity.showFilterMenu(activity.year, activity.semester);
                    activity.adapter.notifyDataSetChanged();
                    break;
                case 2:
                    activity.tv_error.setVisibility(View.GONE);
                    activity.ll_load.setVisibility(View.VISIBLE);
                    activity.adapter.notifyDataSetChanged();
                    final Bundle b = msg.getData();
                    final String year = b.getString("year");
                    final String semester = b.getString("semester");
                    new Thread() {
                        @Override
                        public void run() {
                            activity.filterCourse(year, semester);
                        }
                    }.start();
                    break;
                case 3:
                    activity.ll_load.setVisibility(View.GONE);
                    activity.tv_error.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            getWeek();
            getViewstate();
        }
    };

    //弹出过滤菜单选项
    private void showFilterMenu(List<String> year, List<String> semester) {
        View view = View.inflate(this, R.layout.dialog_filter, null);
        TextView tvFirst = (TextView) view.findViewById(R.id.tv_first);
        tvFirst.setText(getString(R.string.year));
        final Spinner spFirst = (Spinner) view.findViewById(R.id.sp_first);
        final ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, year);
        yearAdapter.setDropDownViewResource(R.layout.dropdown_stytle);
        spFirst.setAdapter(yearAdapter);
        for (int i = 0; i < year.size(); i++) {
            if (year.get(i).indexOf(nowYear) != -1) {
                spFirst.setSelection(i, true);// 默认选中项
                break;
            }
        }
        TextView tvSecond = (TextView) view.findViewById(R.id.tv_second);
        tvSecond.setText(getString(R.string.semester));
        final Spinner spSecond = (Spinner) view.findViewById(R.id.sp_second);
        final ArrayAdapter<String> semesterAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, semester);
        semesterAdapter.setDropDownViewResource(R.layout.dropdown_stytle);
        spSecond.setAdapter(semesterAdapter);
        for (int i = 0; i < year.size(); i++) {
            if (semester.get(i).indexOf(nowSemester) != -1) {
                spSecond.setSelection(i, true);// 默认选中项
                break;
            }
        }
        chooseDialogBuilder.setView(view);
        chooseDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final String year = (String) spFirst.getSelectedItem();
                final String semester = (String) spSecond.getSelectedItem();
                if (year == nowYear && semester == nowSemester) {
                    Toast.makeText(CourseActivity.this, "请选择想要查询的学年或学期", Toast.LENGTH_LONG).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            courseList.clear();
                            Message msg = mHandler.obtainMessage();
                            Bundle b = new Bundle();
                            b.putString("year", year);
                            b.putString("semester", semester);
                            msg.setData(b);
                            msg.what = 2;
                            mHandler.sendMessage(msg);
                        }
                    }).start();
                    Toast.makeText(CourseActivity.this, year + "学年的第" + semester + "学期", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //过滤课表
    private void filterCourse(String xn, String xq) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.clear();
        headers.put("Referer", courseUrl);
        OkHttpUtils
                .post()
                .url(courseUrl)
                .headers(headers)
                .addParams("xn", xn)
                .addParams("xq", xq)
                .addParams("__VIEWSTATE", __VIEWSTATE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document html = Jsoup.parse(response);
                        getScoreItem(html);
                    }
                });

    }

    private void getViewstate() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Referer", courseUrl);

        OkHttpUtils
                .get()
                .url(courseUrl)
                .headers(headers)
                .tag(this)
                .build()
                .connTimeOut(20000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        getViewstate();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document html = Jsoup.parse(response);
                        __VIEWSTATE = html.select("#Form1 input[name=__VIEWSTATE]").attr("value");
                        getScoreItem(html);
                    }
                });

    }

    //获取当前周数
    private void getWeek() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String weekUrl = sp.getString("weekUrl", null);
        Log.e("weekUrl", weekUrl);
        OkHttpUtils
                .get()
                .url("https://www.mmvtc.cn" + weekUrl)
                .tag(this)
                .build()
                .connTimeOut(20000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document html = Jsoup.parse(response);
                        String tempStr = html.select("tbody .tabcolor .bigfont b").text();
                        LogUtil.e("第几周", tempStr);
                        Pattern pattern = Pattern.compile("第\\d周");
                        Matcher matcher = pattern.matcher(tempStr);

                        if (matcher.find()) {

                            LogUtil.e("匹配结果", matcher.group());
                            long time = System.currentTimeMillis();
                            Date date = new Date(time);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 EEEE");
                            format = new SimpleDateFormat("EEEE");

//                            如果是星期日则将周数减减
                            if (String.valueOf(format.format(date)).equals("星期日")) {
                                String regEx = "[^0-9]";
                                Pattern p = Pattern.compile(regEx);
                                Matcher m = p.matcher(matcher.group());
                                Log.e("今天是周日",m.replaceAll("").trim());

                                int weekNumber = Integer.parseInt(m.replaceAll("").trim());
                                tv_title.setText(tv_title.getText() + "(第" + (--weekNumber) + "周)");
                            } else {
                                tv_title.setText(tv_title.getText() + "(" + matcher.group() + ")");
                            }
                        }
                    }
                });
    }

    private void getScoreItem(Document html) {
        __VIEWSTATE = html.select("#Form1 input[name=__VIEWSTATE]").attr("value");
        Elements table1 = html.select("#Table1");
        Elements table6 = html.select("#Table6");
        Elements trs = html.select("#Table6 tr");
//        如果字符串长度小于100就没必要继续往下执行
        if (trs.text().length() < 100) {
            Message msg = new Message();
            msg.what = 3;
            mHandler.sendMessage(msg);
            return;
        }
        courseList.clear();
        year.clear();
        semester.clear();
        nowYear = table1.select("select[name=xn] option[selected]").attr("value");
        nowSemester = table1.select("select[name=xq] option[selected]").attr("value");
        Elements xns = table1.select("select[name=xn] option");
        Elements xqs = table1.select("select[name=xq] option");
//               学年
        for (Element option : xns) {
            if (!TextUtils.isEmpty(option.attr("value"))) {
                year.add(option.attr("value"));
            }
        }
//                学期
        for (Element option : xqs) {
            if (!TextUtils.isEmpty(option.attr("value"))) {
                semester.add(option.attr("value"));
            }
        }
        for (int i = 3; i < trs.size(); i++) {
            Elements tr = table6.select("tr:nth-child(" + i + ")");
            for (int j = 0; j < 8; j++) {
                tr.select("td:nth-child(" + j + ")").attr("test", String.valueOf(j));
            }

            if (i % 2 == 0) {
                continue;
            }
            Elements tds = tr.select("td[align=Center]");
            int size = tds.size();
            Map<String, String> map = new HashMap<String, String>();
            map.put("monday", size > 0 ? Jsoup.parse(tds.get(0).html().replace("<br>", "~")).text().replace("~", "\n").trim() : "");
            map.put("tuesday", size > 1 ? Jsoup.parse(tds.get(1).html().replace("<br>", "~")).text().replace("~", "\n").trim() : "");
            map.put("wednesday", size > 2 ? Jsoup.parse(tds.get(2).html().replace("<br>", "~")).text().replace("~", "\n").trim() : "");
            map.put("thursday", size > 3 ? Jsoup.parse(tds.get(3).html().replace("<br>", "~")).text().replace("~", "\n").trim() : "");
            map.put("friday", size > 4 ? Jsoup.parse(tds.get(4).html().replace("<br>", "~")).text().replace("~", "\n").trim() : "");
            map.put("saturday", size > 5 ? Jsoup.parse(tds.get(5).html().replace("<br>", "~")).text().replace("~", "\n").trim() : "");
            map.put("sunday", size > 6 ? Jsoup.parse(tds.get(6).html().replace("<br>", "~")).text().replace("~", "\n").trim() : "");
            courseList.add(map);
            Message msg = new Message();
            msg.what = 1;
            mHandler.sendMessage(msg);

            LogUtil.e("courseList", courseList.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}