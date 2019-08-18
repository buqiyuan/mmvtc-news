package com.tab.viewpager.jwc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.tab.viewpager.R;
import com.tab.viewpager.activity.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tab.viewpager.R.id.ll_load;

public class ScoreFragment extends AppCompatActivity {

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
            ll_load = (LinearLayout)findViewById(R.id.ll_load);
            ll_load.setVisibility(View.VISIBLE);

            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            refererUrl = sp.getString("refererUrl", null);
            scoreUrl = sp.getString("scoreUrl", null);
            cookie = sp.getString("cookie", null);
            listView = (ListView) findViewById(R.id.listView);

            adapter = new SimpleAdapter(ScoreFragment.this, list, R.layout.score_item, new String[]{"xuenian","xueqi", "className", "score"},
                    new int[]{R.id.xuenian,R.id.xueqi, R.id.className, R.id.score});
            listView.setAdapter(adapter);

            new Thread(runnable).start();
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
            getScore();
            handler.sendEmptyMessage(0);
        }
    };

    private void getViewstate() {
        HttpGet httpGet = new HttpGet(scoreUrl);
        httpGet.setHeader("Cookie", cookie);
        httpGet.setHeader("Referer", scoreUrl);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = client.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(httpResponse.getEntity());
                Document html = Jsoup.parse(content);
                Elements e = html.select("input[name=__VIEWSTATE]");
                viewstate = e.get(0).attr("value");
            }else if (httpResponse.getStatusLine().getStatusCode() == 302) {
                String html = EntityUtils.toString(httpResponse.getEntity());
                Document dom = Jsoup.parse(html);
                String text = dom.select("body").text();
                if (text.toLowerCase().replaceAll("\\s*", "") == "objectmovedtohere") {
                    Toast.makeText(ScoreFragment.this, "登录身份已过期，请重新登录！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getScore() {
        HttpPost httpPost = new HttpPost(scoreUrl);

        httpPost.setHeader("Cookie", cookie);
        httpPost.setHeader("Referer", scoreUrl);
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:47.0) Gecko/20100101 Firefox/47.0");

        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("__EVENTTARGET", ""));
        data.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
        data.add(new BasicNameValuePair("__VIEWSTATE", viewstate));
        data.add(new BasicNameValuePair("ddlXN", ""));
        data.add(new BasicNameValuePair("ddlXQ", ""));
        data.add(new BasicNameValuePair("ddl_kcxz", ""));
        data.add(new BasicNameValuePair("btn_zcj", "历年成绩"));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data);
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String str = EntityUtils.toString(httpResponse.getEntity());

                getScoreItem(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    }
}

