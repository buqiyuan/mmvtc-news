package com.tab.viewpager.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tab.viewpager.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;

public class ArticleActive extends AppCompatActivity {
    private WebView webView;
    private LinearLayout ll_load;
    String html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        webView = (WebView) findViewById(R.id.webview);
        ll_load = (LinearLayout) findViewById(R.id.ll_load);
        ll_load.setVisibility(View.VISIBLE);
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
        Bundle b = getIntent().getExtras();
        //获取Bundle的信息
        final String href = b.getString("href");
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
                    doc.select("*").attr("style", "max-width:100vw;overflow:hidden;");
                    doc.select("body img").attr("style", "display: block;margin: 0 5vw;max-width: 90vw;height: auto;");

                    if (href.indexOf("xskyw") != -1) {
                        // 学术网标题
                        doc.select(".new .content h3").attr("style", "font-size:20px;color:#666;text-align:center;font-weight:800;");
                        doc.select(".new .content h4").attr("style", "font-size:14px;color:#666;text-align:center;color:#999;");
                        String title = doc.select(".new .content h3").toString();
                        String subTitle = doc.select(".new .content h4").toString();
                        doc.select(".new .news_content span").attr("style", "font-size:18px;color:#666");
                        String body = doc.select(".new .news_content").toString();
                        html = title + subTitle + body;
                    } else if (href.indexOf("hxgcx") != -1) {
                        // 高职高专标题
                        doc.select("#container .frame h1").attr("style", "font-size:20px;color:#666;text-align:center;font-weight:800;");
                        doc.select("#container .frame h4").attr("style", "font-size:14px;color:#666;text-align:center;color:#999;");
                        String title = doc.select("#container .frame  h1").toString();
                        String subTitle = doc.select("#container .frame  h4").toString();
                        doc.select("#container .content p").attr("style", "font-size:18px;color:#666;text-indent: 36px;");
                        doc.select("#container .content span").attr("style", "font-size:18px;color:#666");
                        String body = doc.select("#container .content").toString();
                        html = title + subTitle + body;
                    }  else if (href.indexOf("tmgcx") != -1) {
                        // 土木系标题
                        doc.select(".content #title").attr("style", "font-size:20px;color:#666;text-align:center;font-weight:800;");
                        doc.select(".content #laiyuan").attr("style", "font-size:14px;color:#666;text-align:center;color:#999;");
                        String title = doc.select(".content #title").toString();
                        String subTitle = doc.select(".content #laiyuan").toString();
                        doc.select(".content #xiangxi p").attr("style", "font-size:18px;color:#666;text-indent: 36px;");
                        doc.select(".content #xiangxi span").attr("style", "font-size:18px;color:#666");
                        String body = doc.select(".content #xiangxi").toString();
                        html = title + subTitle + body;
                    } else if (href.indexOf("zzb") != -1) {
                        // 中专部标题
                        doc.select("#article_container #tt").attr("style", "font-size:20px;color:#666;text-align:center;font-weight:800;");
                        String title = doc.select("#article_container #tt").toString();
                        doc.select("#article_container .list_nr p").attr("style", "font-size:18px;color:#666;text-indent: 36px;");
                        doc.select("#article_container.list_nr span").attr("style", "font-size:18px;color:#666");
                        String body = doc.select("#article_container .list_nr").toString();
                        html = title + body;
                    } else {
                        // 文章标题
                        String title = doc.select(".job__top__right .mt-20 .ali-ol-experiment-title")
                                .attr("style", "font-size:20px;font-weight:800;text-align:center;").append("<br>").toString();
                        //文章小标题
                        String subTitle = doc.select(".job__top__right .mt-20 .mb-15").attr("style", "font-size:14px;color:#999;text-align:center;").toString();
                        doc.select(".job__top__right .ali-ol-experiment-content iframe").attr("style", "width:100%!important;height:auto!important;");
                        doc.select(".job__top__right .ali-ol-experiment-content iframe #youku-playerBox").attr("style", "width:90%!important;height:auto!important;margin: 10px auto;");
                        doc.select(".job__top__right .ali-ol-experiment-content p").attr("style", "font-size:18px!important;color:#666;text-indent:36px;");
                        doc.select(".job__top__right .ali-ol-experiment-content span").attr("style", "font-size:18px!important;color:#666;");
                        //文章主题内容
                        String body = doc.select(".job__top__right .ali-ol-experiment-content").attr("style", "font-size:18px!important;color:#666;").toString();
                        html = title + subTitle + body;
                    }
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
