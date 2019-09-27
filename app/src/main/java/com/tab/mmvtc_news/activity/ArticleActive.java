package com.tab.mmvtc_news.activity;

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

import com.hjq.toast.ToastUtils;
import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.okhttpUtil.OkHttpUtils;
import com.tab.mmvtc_news.okhttpUtil.callback.StringCallback;
import com.tab.mmvtc_news.utils.LogUtil;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;

import okhttp3.Call;

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
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
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
        String href = b.getString("href");
        Log.e("href", href.toString());
        loadArticle(href);
    }

    //加载文章
    private void loadArticle(final String href) {
        OkHttpUtils
                .get()
                .url(href)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.show("文章加载失败！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document doc = Jsoup.parse(response);

                        doc.select("*").attr("style", "max-width:100vw;overflow:hidden;");
                        doc.select("body img").attr("style", "display: block;margin: 0 auto;max-width: 100%;height: auto;");

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
                        } else if (href.indexOf("tmgcx") != -1) {
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
                        } else if (href.indexOf("jjglx") != -1) {
                            // 经济管理系标题
                            doc.select("#content_wenzhang h3").attr("style", "font-size:20px;color:#666;text-align:center;font-weight:800;");
                            String title = doc.select("#content_wenzhang h3").toString();
                            String subTitle = doc.select("#content_wenzhang #date").attr("style", "font-size:14px;color:#999;text-align:center;").toString();
                            doc.select("#content_wenzhang .news_content p").attr("style", "font-size:18px;color:#666;text-indent: 36px;");
                            doc.select("#content_wenzhang .news_content span").attr("style", "font-size:18px;color:#666");
                            String body = doc.select("#content_wenzhang .news_content").toString();
                            html = title + subTitle + body;
                        } else if (href.indexOf("jdxxx") != -1) {
                            // 机电系标题
                            doc.select(".articleMain .articleTitle").attr("style", "font-size:20px;color:#666;text-align:center;font-weight:800;");
                            String title = doc.select(".articleMain .articleTitle").toString();
                            String subTitle = doc.select(".articleMain .articleInfo").attr("style", "font-size:14px;color:#999;text-align:center;").toString();
                            doc.select(".articleMain .articleContent p").attr("style", "font-size:18px;color:#666;text-indent: 36px;");
                            doc.select(".articleMain .articleContent span").attr("style", "font-size:18px;color:#666");
                            String body = doc.select(".articleMain .articleContent").toString();
                            html = title + subTitle + body;
                        } else if (href.indexOf("jsjgcx") != -1) {
                            // 计算系标题
                            doc.select(".main .newsTitle").attr("style", "font-size:20px;color:#666;text-align:center;font-weight:800;");
                            String title = doc.select(".main .newsTitle").toString();
                            doc.select(".main .newsContent p").attr("style", "font-size:18px;color:#666;text-indent: 36px;");
                            doc.select(".main .newsContent span").attr("style", "font-size:18px;color:#666");
                            String body = doc.select(".main .newsContent").toString();
                            html = title + body;
                        } else if (href.indexOf("skb") != -1) {
                            // 社科基础部标题
                            doc.select("#list-you h3").attr("style", "font-size:20px;color:#666;text-align:center;font-weight:800;");
                            String title = doc.select("#list-you h3").toString();
                            String subTitle = doc.select("#list-you #date").attr("style", "font-size:14px;color:#999;text-align:center;").toString();
                            doc.select("#list-you #news_content p").attr("style", "font-size:18px;color:#666;text-indent: 36px;");
                            doc.select("#list-you #news_content span").attr("style", "font-size:18px;color:#666");
                            String body = doc.select("#list-you #news_content").toString();
                            html = title + subTitle + body;
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
                        ll_load.setVisibility(View.GONE);
                        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
