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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class CourseFragment extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {
            setContentView(R.layout.fragment_course);
            ll_load = (LinearLayout) findViewById(R.id.ll_load);
            tv_error = (TextView) findViewById(R.id.tv_error);
            ll_load.setVisibility(View.VISIBLE);
            tv_filter = (TextView) findViewById(R.id.tv_filter);
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
//            adapter = new SimpleAdapter(CourseFragment.this, courseList, R.layout.course_item, keyArr, itemArr);
            adapter = new MyBaseAdapter(CourseFragment.this, courseList);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(CourseFragment.this, R.layout.course_time_item, data);//适配器

            listViewRight.setAdapter(adapter);
            listViewLeft.setAdapter(adapter2);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();//返回
                }
            });
            new Thread(runnable).start();
        }
    }

    private final MyHandler mHandler = new MyHandler(this);

    static class MyHandler extends Handler {

        private final WeakReference<CourseFragment> mActivity;

        public MyHandler(CourseFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (mActivity.get() == null) {
                return;
            }
            final CourseFragment activity = mActivity.get();
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
            getViewstate();
        }
    };

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
                    Toast.makeText(CourseFragment.this, "请选择想要查询的学年或学期", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(CourseFragment.this, year + "学年的第" + semester + "学期", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

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

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document html = Jsoup.parse(response);
                        __VIEWSTATE = html.select("#Form1 input[name=__VIEWSTATE]").attr("value");
                        getScoreItem(html);
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
//                LogUtils.e("trs",trs.toString());
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
//           String tempStr = tr.toString().replace("<br>", "").replace("&nbsp;", "");
//            Document tr_doc = Jsoup.parse(tempStr);
//            Log.i("size", String.valueOf(tr_doc));
            Elements tds = tr.select("td[align=Center]");
            int size = tds.size();

//                    LogUtils.e("tr",tr.toString());
//                    LogUtils.i("tds",table.select("tr:nth-child(" + i + ") > td").toString());
            Map<String, String> map = new HashMap<String, String>();
            map.put("monday", size > 0 ? Jsoup.parse(tds.get(0).html().replace("<br>", "~")).text().replace("~","\n").trim() : "");
            map.put("tuesday", size > 1 ? Jsoup.parse(tds.get(1).html().replace("<br>", "~")).text().replace("~","\n").trim() : "");
            map.put("wednesday", size > 2 ? Jsoup.parse(tds.get(2).html().replace("<br>", "~")).text().replace("~","\n").trim() : "");
            map.put("thursday", size > 3 ? Jsoup.parse(tds.get(3).html().replace("<br>", "~")).text().replace("~","\n").trim() : "");
            map.put("friday", size > 4 ? Jsoup.parse(tds.get(4).html().replace("<br>", "~")).text().replace("~","\n").trim() : "");
            map.put("saturday", size > 5 ? Jsoup.parse(tds.get(5).html().replace("<br>", "~")).text().replace("~","\n").trim() : "");
            map.put("sunday", size > 6 ? Jsoup.parse(tds.get(6).html().replace("<br>", "~")).text().replace("~","\n").trim() : "");
            courseList.add(map);
            Message msg = new Message();
            msg.what = 1;
            mHandler.sendMessage(msg);
            LogUtils.e("courseList", courseList.toString());
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}