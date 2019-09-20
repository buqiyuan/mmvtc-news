package com.tab.mmvtc_news.jwc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.okhttpUtil.OkHttpUtils;
import com.tab.mmvtc_news.okhttpUtil.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class ScoreActivity extends AppCompatActivity {

    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    private String refererUrl = "";
    private String scoreUrl = "";
    private String cookie = "";
    private String viewstate = "";
    private View loadMoreView;
    private LinearLayout ll_load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {
            setContentView(R.layout.fragment_score);
            ll_load = (LinearLayout) findViewById(R.id.ll_load);
            ll_load.setVisibility(View.VISIBLE);

            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            refererUrl = sp.getString("refererUrl", null);
            scoreUrl = sp.getString("scoreUrl", null);
            cookie = sp.getString("cookie", null);
            listView = (ListView) findViewById(R.id.listView);

            adapter = new SimpleAdapter(ScoreActivity.this, list, R.layout.score_item, new String[]{"xuenian", "xueqi", "className", "score"},
                    new int[]{R.id.xuenian, R.id.xueqi, R.id.className, R.id.score});
            listView.setAdapter(adapter);

            getViewstate();

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
        }
    }


    private void getViewstate() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Referer", scoreUrl);
        OkHttpUtils
                .get()
                .url(scoreUrl)
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
                        Elements e = html.select("input[name=__VIEWSTATE]");
                        viewstate = e.get(0).attr("value");
//                        获取成绩
                        getScore();
                    }
                });
    }

    private void getScore() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Referer", scoreUrl);
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:47.0) Gecko/20100101 Firefox/47.0");
        OkHttpUtils
                .post()
                .url(scoreUrl)
                .headers(headers)
                .addParams("__EVENTTARGET", "")
                .addParams("__EVENTARGUMENT", "")
                .addParams("__VIEWSTATE", viewstate)
                .addParams("ddlXN", "")
                .addParams("ddlXQ", "")
                .addParams("ddl_kcxz", "")
                .addParams("btn_zcj", "历年成绩")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        getScoreItem(response);
                    }
                });
    }

    private void getScoreItem(String html) {
        Document content = Jsoup.parse(html);
        Element ScoreList = content.getElementById("Datagrid1");
        Elements tr = ScoreList.getElementsByTag("tr");
        list.clear();
        for (int i = 1; i < tr.size(); i++) {
            Elements td = tr.get(i).getElementsByTag("td");
            String xuenian = td.get(0).text();
            String xueqi = td.get(1).text();
            String className = td.get(3).text();
            String score = td.get(8).text();
            Map<String, String> map = new HashMap<String, String>();
            map.put("xuenian", xuenian);
            map.put("xueqi", xueqi);
            map.put("className", className);
            map.put("score", score);
            list.add(map);
        }
        ll_load.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }
}

