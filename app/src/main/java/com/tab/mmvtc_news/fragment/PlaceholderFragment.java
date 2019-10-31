package com.tab.mmvtc_news.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.okhttpUtil.OkHttpUtils;
import com.tab.mmvtc_news.okhttpUtil.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import okhttp3.Call;

/**
 * @author 卜启缘
 * @version 1.0
 * @date 2019/10/31 21:25
 */
public class PlaceholderFragment extends Fragment {
    String[] array = new String[]{"Android 1", "Android 2", "Android 3",
            "Android 4", "Android 5", "Android 6", "Android 7", "Android 8",
            "Android 9", "Android 10", "Android 11", "Android 12", "Android 13",
            "Android 14", "Android 15", "Android 16"};

    private static final String ARG_URL = "";
    private LinearLayout ll_load;
    private WebView webView;
    private String link = "http://hwlibsys.mmvtc.cn:8080/reader/redr_info.php";

    public static PlaceholderFragment newInstance(String url) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceholderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            link = getArguments().getString(ARG_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_school_info, container, false);

//        final ListView listView = (ListView) rootView.findViewById(R.id.listView);
//        listView.setAdapter(new ArrayAdapter<>(getActivity(),
//                R.layout.list_item, R.id.text1, array));

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("Debug", "creating fragment "
                + getArguments().getString(ARG_URL));

        link = getArguments().getString(ARG_URL);

        initView();
        getData();
    }

    private void getData() {
        Log.e("link", link);
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
                        String baseUrl = "http://hwlibsys.mmvtc.cn:8080/";
                        Document doc = Jsoup.parse(response);
                        ll_load.setVisibility(View.GONE);
                        doc.select("*").attr("style", "max-width:100vw;");
                        doc.select("body img").attr("style", "display: block;margin: 0 auto;max-width: 100%;height: auto;");
                        doc.select("link").attr("href", doc.select("link").attr("abs:href"));
                        doc.select("a").attr("href", "javascript:void(0)");
                        doc.select("body img").attr("src", baseUrl + doc.select("body img").attr("src"));
                        doc.select("#navsidebar").html("");

                        Elements mylib_info = doc.select("#mylib_info");
                        if (mylib_info != null) {
                            Elements tds = mylib_info.select("table tr td");
                            tds.attr("style", "display: block;width:90vw;margin: 0 auto;");
                            StringBuilder tdStr = new StringBuilder();
                            Log.e("tds",tds.toString());
//                            for (Element td : tds) {
////                                tdStr.append(td.wrap("<tr></tr>"));
//                                Log.e("tdStr",String.valueOf(td.wrap("<tr></tr>")));
//                            }
                            mylib_info.select("table").html("");
                            mylib_info.select("table").html(tds.outerHtml());
                        }

                        webView.loadDataWithBaseURL(null, doc.select("#container").html(), "text/html", "utf-8", null);
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
        View loadMoreView = getActivity().getLayoutInflater().inflate(R.layout.load_more_layout, null);
        loadMoreView.setVisibility(View.VISIBLE);
    }
}