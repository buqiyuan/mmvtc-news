package com.tab.mmvtc_news.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hjq.toast.ToastUtils;
import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.jwc.NoScrollListView;
import com.tab.mmvtc_news.okhttpUtil.OkHttpUtils;
import com.tab.mmvtc_news.okhttpUtil.callback.StringCallback;
import com.tab.mmvtc_news.utils.LogUtil;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class BookDetailActivity extends AppCompatActivity {
    private WebView webView;
    private LinearLayout ll_load;
    String html;
    private SimpleAdapter infoAdapter;
    private SimpleAdapter collectAdapter;
    private NoScrollListView lv_info;
    private NoScrollListView lv_collect;
    private List<Map<String, String>> infoDatas = new ArrayList<>();
    private List<Map<String, String>> collectDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        lv_info = findViewById(R.id.lv_info);
        lv_collect = findViewById(R.id.lv_collect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb);
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
        Bundle b = getIntent().getExtras();
        //获取Bundle的信息
        String href = b.getString("href");
        Log.e("href", href.toString());
        loadBookDetail(href);
    }

    //加载文章
    private void loadBookDetail(final String href) {
        OkHttpUtils
                .get()
                .url(href)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.show("图书详情加载失败！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document doc = Jsoup.parse(response);
                        Elements infos = doc.select("#item_detail dl");
                        for (Element ele : infos) {
                            if (!TextUtils.isEmpty(ele.select("dd").text())) {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("title", ele.select("dt").text());
                                map.put("detail", ele.select("dd").text());
                                infoDatas.add(map);
                            }
                        }
                        Elements collections = doc.select("#tab_item tbody tr");
                        for (int i = 1; i < collections.size(); i++) {
                            Element ele = collections.get(i);
                            Elements tds = ele.select("td");
                            if (!TextUtils.isEmpty(tds.text().trim())) {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("bookNumber", tds.get(0).text());
                                map.put("codeNumber", tds.get(1).text());
                                map.put("bookCollections", tds.get(3).text());
                                map.put("bookStatus", tds.get(5).text());
                                collectDatas.add(map);
                            }
                        }
                        // key值数组，适配器通过key值取value，与列表项组件一一对应
                        String[] fromInfo = {"title", "detail"};
                        // 列表项组件Id 数组
                        int[] toInfo = {R.id.tv_title, R.id.tv_detail};
                        //设置适配器
                        infoAdapter = new SimpleAdapter(BookDetailActivity.this, infoDatas, R.layout.item_book_detail, fromInfo, toInfo);
                        lv_info.setAdapter(infoAdapter);

                        // key值数组，适配器通过key值取value，与列表项组件一一对应
                        String[] fromCollection = {"bookNumber", "codeNumber", "bookCollections", "bookStatus"};
                        // 列表项组件Id 数组
                        int[] toCollection = {R.id.tv_book_number, R.id.tv_code_number, R.id.tv_address, R.id.tv_status};
                        //设置适配器
                        collectAdapter = new SimpleAdapter(BookDetailActivity.this, collectDatas, R.layout.item_book_collections, fromCollection, toCollection);
                        lv_collect.setAdapter(collectAdapter);
//                        ll_load.setVisibility(View.GONE);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
