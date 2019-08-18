package com.tab.viewpager.jwc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tab.viewpager.R;
import com.tab.viewpager.activity.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseFragment extends AppCompatActivity {

    private NoScrollListView listViewLeft;
    private NoScrollListView listViewRight;
    private SimpleAdapter adapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {
            setContentView(R.layout.fragment_course);
            ll_load = (LinearLayout) findViewById(R.id.ll_load);
            ll_load.setVisibility(View.VISIBLE);
//            ctype.add("全部");
//            ctype.add("学期");
//            ctype.add("凧");
//            spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, ctype);  //创建一个数组适配器
//            spinnerAdapter.setDropDownViewResource(R.layout.dropdown_stytle);     //设置下拉列表框的下拉选项样式
//
//            spinner_xn = (Spinner) findViewById(R.id.spinner_xn);
//            spinner_xq = (Spinner) findViewById(R.id.spinner_xq);
//            spinner_xq.setAdapter(spinnerAdapter);
//            spinner_xn.setAdapter(spinnerAdapter);
//            spinner_xn.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
//                public void onItemSelected(AdapterView<?> arg0, View arg1,
//                                           int arg2, long arg3) {
//                    // TODO Auto-generated method stub
//                    // 将所选mySpinner 的值带入myTextView 中
////                    myTextView.setText("您选择的是：" + adapter.getItem(arg2));//文本说明
//                    Log.e("您选择的是", spinnerAdapter.getItem(arg2) + "");
//                }

//                public void onNothingSelected(AdapterView<?> arg0) {
//                    // TODO Auto-generated method stub
////                    myTextView.setText("Nothing");
//                    Log.e("Nothing",  "Nothing");
//                }
//            });
            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            refererUrl = sp.getString("refererUrl", null);
            courseUrl = sp.getString("courseUrl", null);
            cookie = sp.getString("cookie", null);
            listViewRight = (NoScrollListView) findViewById(R.id.listViewRight);
            listViewLeft = (NoScrollListView) findViewById(R.id.listViewLeft);
            adapter = new SimpleAdapter(CourseFragment.this, courseList, R.layout.course_item, keyArr, itemArr);
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

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ll_load.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    };

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            getViewstate();
            handler.sendEmptyMessage(0);
        }
    };

    private void getViewstate() {
        HttpGet httpGet = new HttpGet(courseUrl);
        httpGet.setHeader("Cookie", cookie);
        httpGet.setHeader("Referer", courseUrl);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = client.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(httpResponse.getEntity());
                Document html = Jsoup.parse(content);
                Elements table = html.select("#Table6");
                Elements trs = html.select("#Table6 tr");
//                LogUtils.e("trs",trs.toString());
                courseList.clear();
                for (int i = 3; i < trs.size(); i++) {
                    Elements tr = table.select("tr:nth-child(" + i + ")");
                    for (int j = 0; j < 8; j++) {
                        tr.select("td:nth-child(" + j + ")").attr("test", String.valueOf(j));
                    }

                    if (i % 2 == 0) {
                        continue;
                    }
                    Log.i("size", String.valueOf(table.select("tr:nth-child(" + i + ") td")));
                    Elements tds = tr.select("td[align=Center]");
                    int size = tds.size();
//                    LogUtils.e("tr",tr.toString());
//                    LogUtils.i("tds",table.select("tr:nth-child(" + i + ") > td").toString());
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("monday", size > 0 ? tds.get(0).text() : "");
                    map.put("tuesday", size > 1 ? tds.get(1).text() : "");
                    map.put("wednesday", size > 2 ? tds.get(2).text() : "");
                    map.put("thursday", size > 3 ? tds.get(3).text() : "");
                    map.put("friday", size > 4 ? tds.get(4).text() : "");
                    map.put("saturday", size > 5 ? tds.get(5).text() : "");
                    map.put("sunday", size > 6 ? tds.get(6).text() : "");
                    courseList.add(map);
                    LogUtils.e("courseList", courseList.toString());
                }

            }else if (httpResponse.getStatusLine().getStatusCode() == 302) {
                String html = EntityUtils.toString(httpResponse.getEntity());
                Document dom = Jsoup.parse(html);
                String text = dom.select("body").text();
                if (text.toLowerCase().replaceAll("\\s*", "") == "objectmovedtohere") {
                    Toast.makeText(CourseFragment.this, "登录身份已过期，请重新登录！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getScoreItem(String html) {
        Document content = Jsoup.parse(html);
        Element ScoreList = content.getElementById("Datagrid1");
        Elements tr = ScoreList.getElementsByTag("tr");
        for (int i = 1; i < tr.size(); i++) {
            Elements td = tr.get(i).getElementsByTag("td");
            String xueqi = td.get(1).text();
            String className = td.get(3).text();
            String score = td.get(8).text();
            Map<String, String> map = new HashMap<String, String>();
            map.put("monday", className);
            map.put("tuesday", score);
            map.put("wednesday", score);
            map.put("thursday", score);
            map.put("friday", score);
            map.put("saturday", score);
            map.put("sunday", score);
            courseList.add(map);
        }
    }
}