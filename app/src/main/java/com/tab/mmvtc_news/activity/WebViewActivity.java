package com.tab.mmvtc_news.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.okhttpUtil.OkHttpUtils;
import com.tab.mmvtc_news.okhttpUtil.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
/**
 * Created by 卜启缘 on 2019/10/8.
 */
public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private String title;
    private String url;
    private String html;
    private LinearLayout ll_load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = (WebView) findViewById(R.id.webview);
        ll_load = (LinearLayout) findViewById(R.id.ll_load);
        ll_load.setVisibility(View.VISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb);
        TextView tv_title = findViewById(R.id.tv_title);
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

        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        //允许SessionStorage/LocalStorage存储
        webView.getSettings().setDomStorageEnabled(true);
        //允许缓存，设置缓存位置
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAppCachePath(getApplicationContext().getDir("appcache", 0).getPath());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ll_load.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        if (title.indexOf("来校指南") != -1) {
            tv_title.setText(title);
            setGuideLayout(url);
        }else if (title.indexOf("我的图书馆") != -1) {
            tv_title.setText(title);
            ll_load.setVisibility(View.GONE);
            webView.loadUrl(url);
//            setMyLibraryLayout(url);
        } else {
            tv_title.setText(title);
            webView.loadUrl(url);
        }
    }

//设置来校指南布局
    private void setGuideLayout(final String url) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Referer", "https://websites.mmvtc.cn/zsw/index.php?url=site/article&id=581&groups_id=13");
        headers.put("Host", "websites.mmvtc.cn");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36");
        OkHttpUtils
                .get()
                .url(url)
                .headers(headers)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("加载失败", e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document doc = Jsoup.parse(response);
                        doc.select(".content1_l").attr("style", "float: none;");
                        doc.select(".content2 img").attr("style", "display: block;width: 100%;");
                        String top = doc.select(".content1_l").toString();
                        String img = doc.select(".content2 img").toString();
                        String html = top + img;
                        Log.e("htmlHHH", html);
                        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                    }
                });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Document doc = null;
//                try {
//                    Connection.Response response = Jsoup.connect(url)
//                            .method(Connection.Method.GET)
//                            .execute();
//                    Map<String, String> getCookies = response.cookies();
//                    String Cookie = getCookies.toString();
//                    Cookie = Cookie.substring(Cookie.indexOf("{") + 1, Cookie.lastIndexOf("}"));
//                    Cookie = Cookie.replaceAll(",", ";");
//                    Log.e("COOKIE", Cookie);
//
//                    doc = Jsoup.connect(url)
//                            .header("Cookie", Cookie)
//                            .header("Referer", "https://websites.mmvtc.cn/zsw/index.php?url=site/article&id=581&groups_id=13")
//                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
//                            .get();
//                    doc.select(".content1_l").attr("style", "float: none;");
//                        doc.select(".content2 img").attr("style", "display: block;width: 100%;");
//                        String top = doc.select(".content1_l").toString();
//                        String img = doc.select(".content2 img").toString();
//                        html = top + img;
//                    Message msg = new Message();
//                    msg.what = 1;
//                    mHandler.sendMessage(msg);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }
//    设置我的图书馆布局
    private void setMyLibraryLayout(final String url) {
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("加载失败", e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document doc = Jsoup.parse(response,"http://hwlibsys.mmvtc.cn:8080/reader");
                        String baseUrl = "http://hwlibsys.mmvtc.cn:8080/reader/";
                        doc.select("#header_opac").attr("style", "display: none;");
                        doc.select("script").attr("src", doc.select("script").attr("abs:src"));
                        doc.select("link").attr("href", doc.select("link").attr("abs:href"));
                        doc.select("a").attr("href",baseUrl +  doc.select("a").attr("href"));
                        doc.select("form").attr("action",baseUrl +  doc.select("form").attr("action"));
                        doc.select("body img").attr("src",baseUrl + doc.select("body img").attr("src"));
                        doc.select("#menubar").attr("style", "display: none;");
                        String top = doc.select(".content1_l").toString();
                        String img = doc.select(".content2 img").toString();
//                        String html = top + img;
                        Log.e("htmlHHH", doc.select("#captcha_tips").toString());
                        webView.loadDataWithBaseURL(null,  doc.toString(), "text/html", "utf-8", null);
                    }
                });
    }
    private final MyHandler mHandler = new MyHandler(this);

    static class MyHandler extends Handler {

        private final WeakReference<WebViewActivity> mActivity;

        public MyHandler(WebViewActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (mActivity.get() == null) {
                return;
            }
            WebViewActivity activity = mActivity.get();
            switch (msg.what) {
                case 1:
                    Log.e("html", activity.html);
                    activity.ll_load.setVisibility(View.GONE);
                    activity.webView.loadDataWithBaseURL(null, activity.html, "text/html", "utf-8", null);
                    break;
            }
        }
    }
}
