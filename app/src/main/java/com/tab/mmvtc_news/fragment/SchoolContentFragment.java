package com.tab.mmvtc_news.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.tab.mmvtc_news.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;

public class SchoolContentFragment extends Fragment {
    private static final String LINK = "link";
    private View loadMoreView;
    private String link;
    private WebView webView;
    private LinearLayout ll_load;
    private String html;


    public SchoolContentFragment() {
        // Required empty public constructor
    }

    public static android.support.v4.app.Fragment newInstance(String link) {
        SchoolContentFragment fragment = new SchoolContentFragment();
        Bundle args = new Bundle();
        args.putString(LINK, link);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            link = getArguments().getString(LINK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_school_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    Log.e("html", link);

                    Connection.Response response = Jsoup.connect(link)
                            .method(Connection.Method.GET)
                            .execute();
                    Map<String, String> getCookies = response.cookies();
                    String Cookie = getCookies.toString();
                    Cookie = Cookie.substring(Cookie.indexOf("{") + 1, Cookie.lastIndexOf("}"));
                    Cookie = Cookie.replaceAll(",", ";");
                    Log.e("COOKIE", Cookie);

                    doc = Jsoup.connect(link)
                            .header("Cookie", Cookie)
                            .header("Referer", "http://www.mmvtc.cn/templet/default/aboutme.html")
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
                            .get();
                    doc.select("*").attr("style", "max-width:100vw;overflow:hidden;");
                    doc.select("body img").attr("style", "display: block;margin: 0 auto;max-width: 100%;height: auto;");

                    doc.select(" .ali-ol-experiment-content iframe").attr("style", "width:100%!important;height:auto!important;");
                    doc.select(" .ali-ol-experiment-content iframe #youku-playerBox").attr("style", "width:90%!important;height:auto!important;margin: 10px auto;");
                    doc.select(" .ali-ol-experiment-content p").attr("style", "font-size:18px!important;color:#666;text-indent:36px;");
                    doc.select(" .ali-ol-experiment-content span").attr("style", "font-size:18px!important;color:#666;");
                    //文章主题内容
                    String body = doc.select(".ali-ol-experiment-content").attr("style", "font-size:18px!important;color:#666;").toString();
                    html = body;
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private final MyHandler mHandler = new MyHandler(this);

    static class MyHandler extends Handler {

        private final WeakReference<SchoolContentFragment> mActivity;

        public MyHandler(SchoolContentFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (mActivity.get() == null) {
                return;
            }
            SchoolContentFragment activity = mActivity.get();
            switch (msg.what) {
                case 1:
                    activity.ll_load.setVisibility(View.GONE);
                    activity.webView.loadDataWithBaseURL(null, activity.html, "text/html", "utf-8", null);
                    break;
            }
        }
    }

    private void initView() {
        View view = getView();
        ll_load = (LinearLayout) view.findViewById(R.id.ll_load);
        ll_load.setVisibility(View.VISIBLE);
        webView = (WebView) view.findViewById(R.id.webview);
//自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        loadMoreView = getActivity().getLayoutInflater().inflate(R.layout.load_more_layout, null);
        loadMoreView.setVisibility(View.VISIBLE);

    }

}
