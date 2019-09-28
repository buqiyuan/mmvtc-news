package com.tab.mmvtc_news.jwc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.okhttpUtil.OkHttpUtils;
import com.tab.mmvtc_news.okhttpUtil.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class MeActivity extends AppCompatActivity {

    private String infoUrl;
    private String cookie;
    private String refererUrl;
    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    private String[] listKey = {"学号", "姓名", "性别", "出生日期", "身份证号", "民族", "来源地区",
            "政治面貌", "学院", "专业名称", "专业方向", "培养方向", "行政班", "学制",
            "学籍状态", "学历层次", "入学日期", "当前所在级", "考生号", "准考证号", "毕业中学"};

    private String[] listValue = {"xh", "xm", "lbl_xb", "lbl_csrq", "lbl_sfzh", "lbl_mz", "lbl_lydq",
            "lbl_zzmm", "lbl_xy", "lbl_zymc", "lbl_zyfx", "lbl_pyfx", "lbl_xzb", "lbl_xz",
            "lbl_xjzt", "lbl_CC", "lbl_rxrq", "lbl_dqszj", "lbl_ksh", "lbl_zkzh", "lbl_byzx"};

    private String[] listKey1 = {"籍贯", "出生地", "宿舍号", "电子邮箱", "联系电话", "邮政编码", "家庭所在地"};
    private String[] listValue1 = {"txtjg", "csd", "ssh", "dzyxdz", "lxdh", "yzbm", "jtszd"};
private TextView tv_title;
    private LinearLayout ll_load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {
            setContentView(R.layout.fragment_me);
            ll_load = (LinearLayout)findViewById(R.id.ll_load);
            ll_load.setVisibility(View.VISIBLE);

            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            infoUrl = sp.getString("infoUrl", null);
            refererUrl = sp.getString("refererUrl", null);
            cookie = sp.getString("cookie", null);

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


            listView = (ListView) findViewById(R.id.listView);
            adapter = new SimpleAdapter(MeActivity.this, list, R.layout.me_item, new String[]{"key", "value"}, new int[]{R.id.tv_key, R.id.tv_value});
            listView.setAdapter(adapter);
            new Thread(contentRun).start();
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ll_load.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    };
    Runnable contentRun = new Runnable() {

        @Override
        public void run() {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Referer", infoUrl);
            OkHttpUtils
                    .get()
                    .url(infoUrl)
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
                            getData(response);
//                            handler.sendEmptyMessage(0);
                        }
                    });

        }

        private void getData(String html) {//解析html获取数据
            Document dom = Jsoup.parse(html);
            list.clear();
            for (int i = 0; i < listKey.length; i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", listKey[i]);
                map.put("value", dom.getElementById(listValue[i]).text());
                list.add(map);
            }
            for (int i = 0; i < listKey1.length; i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", listKey1[i]);
                map.put("value", dom.getElementById(listValue1[i]).attr("value"));
                list.add(map);
            }
            ll_load.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();

        }
    };
}

