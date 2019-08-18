package com.tab.viewpager.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.tab.viewpager.R;
import com.tab.viewpager.activity.ArticleActive;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LINK = "link";

    private String link;

    private TextView textView;
    private ListView mListView;
    private SimpleAdapter mAdapter;
    private List<Map<String, String>> datas = new ArrayList<>();

    private PullRefreshLayout layout;
    private View loadMoreView;

    public int last_index;
    public int total_index;
    public int page_index = 1;
    public boolean isLoading = false;//表示是否正处于加载状态
    private Object initdatas;

    public ContentFragment() {
        // Required empty public constructor
    }

    public static android.support.v4.app.Fragment newInstance(String link) {
        ContentFragment fragment = new ContentFragment();
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
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {

        View view = getView();
        mListView = (ListView) view.findViewById(R.id.lv);
        layout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        textView = (TextView) view.findViewById(R.id.tv);

        loadMoreView = getActivity().getLayoutInflater().inflate(R.layout.load_more_layout, null);
        loadMoreView.setVisibility(View.VISIBLE);

    }

    private void initData() {
//        textView.setText(link);
        //初始化datas
        getDatas(1);
        // key值数组，适配器通过key值取value，与列表项组件一一对应
        String[] from = {"time", "pv", "content"};
        // 列表项组件Id 数组
        int[] to = {R.id.tv_time, R.id.tv_pv, R.id.tv_content};
        //设置适配器
        mAdapter = new SimpleAdapter(getActivity(), datas, R.layout.news, from, to);
        mListView.setAdapter(mAdapter);

        //设置滑动监听，为了上拉加载
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (last_index == total_index && (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)) {
                    //表示此时需要显示刷新视图界面进行新数据的加载(要等滑动停止)
                    if (!isLoading) {
                        //不处于加载状态的话对其进行加载
                        isLoading = true;
                        //设置刷新界面可见
                        loadMoreView.setVisibility(View.VISIBLE);
                        //获取更多数据
                        getDatas(++page_index);
                        Log.e("page_index", String.valueOf(page_index));
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                last_index = firstVisibleItem + visibleItemCount;
                total_index = totalItemCount;
                System.out.println("last:  " + last_index);
                System.out.println("total:  " + total_index);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getActivity(), "Click item" + i, Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                String href = datas.get(i).get("href");
                bundle.putString("href", href);
                Intent intent = new Intent(getActivity(), ArticleActive.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //添加上拉加载视图到listview的底部
        mListView.addFooterView(loadMoreView, null, false);
        // listen refresh event
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
                layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //刷新，先清除数据
                        datas.clear();
                        //再重新请求数据，通常是请求网络，获取最新数据
                        getDatas(1);
                        //刷新适配器
                        mAdapter.notifyDataSetChanged();
                        //刷新完成
                        Toast.makeText(getActivity(), "刷新成功！", Toast.LENGTH_SHORT).show();
                        page_index = 0;
                        layout.setRefreshing(false);
                    }
                }, 0);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //刷新适配器
                    mAdapter.notifyDataSetChanged();
                    //加载完成
                    loadComplete();
                    break;
            }
        }
    };

    /**
     * 上拉加载更多数据
     *
     * @param start 开始索引
     */
    public void getDatas(final int start) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //需要在子线程中处理的逻辑
                Document doc = null;
                try {
                    Log.e("link", link + "&pn=" + start);
                    doc = Jsoup.connect(link + "&pn=" + start).get();
                    //获取总的页码
                    if (!TextUtils.isEmpty(doc.select("#htmlPageCount").text().trim())){
                        total_index = Integer.parseInt(doc.select("#htmlPageCount").text());
                    }
                    // 如果是学术网
                    if (link.indexOf("xskyw") != -1) {
                        Elements elements = doc.select(".new .Column ul li");
                        for (Element ele : elements) {
                            String href = ele.select("a").attr("abs:href");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("time", ele.select("span").text());
                            map.put("pv", "阅读(0)");
                            map.put("content", ele.select("a").text());
                            map.put("href", href);
                            datas.add(map);
                        }
                        //如果是土木工程系
                    } else if (link.indexOf("tmgcx") != -1) {
                        Elements elements = doc.select(".content_wrap .info ul li");
                        for (Element ele : elements) {
                            String href = ele.select("a").attr("abs:href");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("time", ele.select("span").text());
                            map.put("pv", "");
                            map.put("content", ele.select("a").text());
                            map.put("href", href);
                            datas.add(map);
                        }
                    } else if (link.indexOf("hxgcx") != -1) { //如果是化学程系
                        Elements elements = doc.select(".list1-you ul li");
                        for (Element ele : elements) {
                            String href = ele.select("a").attr("abs:href");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("time", ele.select(".r").text());
                            map.put("pv", "");
                            map.put("content", ele.select("a").text());
                            map.put("href", href);
                            datas.add(map);
                        }
                    } else if (link.indexOf("jjglx") != -1) { //如果是经济管理程系
                        Elements elements = doc.select("#zhaopin1 ul li");
                        for (Element ele : elements) {
                            String href = ele.select("a").attr("abs:href");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("time", ele.select(".riqi").text());
                            map.put("pv", "");
                            map.put("content", ele.select("a").text());
                            map.put("href", href);
                            datas.add(map);
                        }
                    } else if (link.indexOf("jdxxx") != -1) { //如果是机电系
                        Elements elements = doc.select(".columnMain ul li");
                        for (Element ele : elements) {
                            String href = ele.select("a").attr("abs:href");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("time", ele.select(".columnTitleTime").text());
                            map.put("pv", "");
                            map.put("content", ele.select(".columnTitleContent").text());
                            map.put("href", href);
                            datas.add(map);
                        }
                    } else if (link.indexOf("jsjgcx") != -1) { //如果是计算机系
                        Elements elements = doc.select(".cbox ul li");
                        for (Element ele : elements) {
                            String href = ele.select("a").attr("abs:href");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("time", ele.select(".text-right").text());
                            map.put("pv", "");
                            map.put("content", ele.select("a").text());
                            map.put("href", href);
                            datas.add(map);
                        }
                    } else if (link.indexOf("skb") != -1) { //如果是社科基础部
                        Elements elements = doc.select("#list-you ul li");
                        for (Element ele : elements) {
                            String href = ele.select("a").attr("abs:href");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("time", ele.select("span").text());
                            map.put("pv", "");
                            map.put("content", ele.select("a").text());
                            map.put("href", href);
                            datas.add(map);
                        }
                    } else {
                        Elements elements = doc.select(".right_zi .list-unstyled li");
                        for (Element ele : elements) {
                            String href = ele.select("a").attr("abs:href");
                            //判断是否是否是外部链接
                            if (href.indexOf("mmvtc") != -1) {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("time", ele.select(".meta time").text());
                                map.put("pv", ele.select(".meta .pv").text());
                                map.put("content", ele.select("div:last-child").text());
                                map.put("href", ele.select("a").attr("abs:href"));
                                datas.add(map);
                            }
                        }
                    }
                    Log.e("datas", datas.toString());
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 加载完成
     */
    public void loadComplete() {
        loadMoreView.setVisibility(View.GONE);//设置刷新界面不可见
        isLoading = false;//设置正在刷新标志位false
//        mListView.removeFooterView(loadMoreView);//如果是最后一页的话，则将其从ListView中移出
    }
}
