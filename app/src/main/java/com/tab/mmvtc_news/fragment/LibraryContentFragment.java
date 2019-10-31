package com.tab.mmvtc_news.fragment;

import android.os.Bundle;
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
import com.tab.mmvtc_news.okhttpUtil.OkHttpUtils;
import com.tab.mmvtc_news.okhttpUtil.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import okhttp3.Call;

/**
 * Created by 卜启缘 on 2019/10/8.
 */
public class LibraryContentFragment extends Fragment {
    private static final String LINK = "link";
    private View loadMoreView;
    private String link;
    private WebView webView;
    private LinearLayout ll_load;
    private String html;


    public LibraryContentFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String link) {
        LibraryContentFragment fragment = new LibraryContentFragment();
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
        getData();
    }

    private void getData() {
        OkHttpUtils
                .get()
                .url(link)
                .tag(this)
                .build()
                .connTimeOut(25000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        getData();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document doc = Jsoup.parse(response,"https://www.mmvtc.cn");
                        doc.select("*").attr("style", "max-width:100vw;overflow:hidden;");
                        doc.select("body img").attr("style", "display: block;margin: 0 auto;max-width: 100%;height: auto;");
                        String src = doc.select("#content_list .zhengwen_wen img").attr("abs:src");
                         doc.select("#content_list .zhengwen_wen img").attr("src",src);
                        html = doc.select("#content_list .zhengwen_wen img").toString();
                        ll_load.setVisibility(View.GONE);
                        Log.e("html222",src);
                        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                    }
                });
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
