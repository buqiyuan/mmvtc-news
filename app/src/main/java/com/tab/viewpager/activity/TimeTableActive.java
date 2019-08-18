package com.tab.viewpager.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.tab.viewpager.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;

import static android.R.attr.type;

public class TimeTableActive extends AppCompatActivity {
    private WebView webView;
    private LinearLayout ll_load;
    String html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        webView = (WebView) findViewById(R.id.webview);
        ll_load = (LinearLayout) findViewById(R.id.ll_load);
        ll_load.setVisibility(View.VISIBLE);
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
        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
//// 设置可以支持缩放
//        webView.getSettings().setSupportZoom(true);
//// 设置出现缩放工具
//        webView.getSettings().setBuiltInZoomControls(true);
////扩大比例的缩放
//        webView.getSettings().setUseWideViewPort(true);
////自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setTextZoom(180);
//        Bundle b = getIntent().getExtras();
        //获取Bundle的信息
        final String href = "http://www.mmvtc.cn/templet/default/zxb/zxsj.html";
        Log.e("href", href.toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    Connection.Response response = Jsoup.connect(href)
                            .method(Connection.Method.GET)
                            .execute();
                    Map<String, String> getCookies = response.cookies();
                    String Cookie = getCookies.toString();
                    Cookie = Cookie.substring(Cookie.indexOf("{") + 1, Cookie.lastIndexOf("}"));
                    Cookie = Cookie.replaceAll(",", ";");
                    Log.e("COOKIE", Cookie);

                    doc = Jsoup.connect(href)
                            .header("Cookie", Cookie)
                            .header("Referer", "http://www.mmvtc.cn/templet/default/index.jsp")
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
                            .get();
                    String style = "";
//                    获取所有样式
                    Elements css = doc.select("link[type=text/css]");
                    for (Element ele : css) {
                        style += "<link href=" + ele.attr("abs:href") + " rel=\"stylesheet\" type=\"text/css\">";
                    }
                    doc.select(".container").attr("style","width:100%;");
                    doc.select(".container table").attr("style","width:100%;");
                    html = style + doc.select(".container").toString();
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.e("html", html);
                    ll_load.setVisibility(View.GONE);
                    webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                    break;
            }
        }
    };
}
