package com.tab.mmvtc_news.fragment;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.activity.ArticleActive;
import com.tab.mmvtc_news.activity.BookDetailActivity;
import com.tab.mmvtc_news.adapter.MySimpleAdapter;
import com.tab.mmvtc_news.adapter.MySimpleHolder;
import com.tab.mmvtc_news.model.BookModel;

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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BookContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookContentFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LINK = "link";

    private String link = "";

    private TextView textView;
    private RecyclerView rvLibrary;
    private MySimpleAdapter<BookModel> mAdapter;
    private List<BookModel> bookModel = new ArrayList<>();

    private PullRefreshLayout layout;
    public int page_index = 1;
    public boolean isLoading = false;//表示是否正处于加载状态

    public BookContentFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String link) {
        BookContentFragment fragment = new BookContentFragment();
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
        return inflater.inflate(R.layout.fragment_bookcontent, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        View view = getView();
        rvLibrary = (RecyclerView) view.findViewById(R.id.rv_library);
        layout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        textView = (TextView) view.findViewById(R.id.tv);

    }

    private void initData() {
//        textView.setText(link);
        //初始化bookModel
        getDatas();
        rvLibrary.setLayoutManager(new LinearLayoutManager(getActivity()));
        //设置适配器
        mAdapter = new MySimpleAdapter<BookModel>(getActivity(), R.layout.item_book, bookModel) {
            @Override
            public void onBindView(MySimpleHolder holder, int position) {
                TextView tv_isbn=holder.getView(R.id.tv_book_isbn);
                tv_isbn.setText(mDatas.get(position).getClassification());
                TextView book_name=holder.getView(R.id.tv_book_name);
                book_name.setText(mDatas.get(position).getBookName());
                TextView tv_author = holder.getView(R.id.tv_book_author);
                tv_author.setText(mDatas.get(position).getAuthor());
            }
        };
        rvLibrary.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvLibrary.setAdapter(mAdapter);
        //设置mRecyclerview的item单击事件
        mAdapter.setOnItemClickListener(new MySimpleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                String href = bookModel.get(position).getUrlDetail();
                bundle.putString("href", href);
                Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        // listen refresh event
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
                layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //刷新，先清除数据
                        bookModel.clear();
                        //再重新请求数据，通常是请求网络，获取最新数据
                        getDatas();
                        //刷新适配器
                        mAdapter.notifyDataSetChanged();
                        //刷新完成
                        Toast.makeText(getActivity(), "刷新成功！", Toast.LENGTH_SHORT).show();
                        page_index = 1;
                        layout.setRefreshing(false);
                    }
                }, 0);
            }
        });
    }

    private final MyHandler mHandler = new MyHandler(this);

    static class MyHandler extends Handler {

        private final WeakReference<BookContentFragment> mActivity;

        public MyHandler(BookContentFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (mActivity.get() == null) {
                return;
            }
            BookContentFragment activity = mActivity.get();
            switch (msg.what) {
                case 1:
                    //刷新适配器
                    activity.mAdapter.notifyDataSetChanged();
                    //加载完成
                    activity.loadComplete();
                    activity.rvLibrary.setEnabled(true);
                    break;
            }
        }
    }

    /**
     * 上拉加载更多数据
     */
    public void getDatas() {
        rvLibrary.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //需要在子线程中处理的逻辑
                Document doc = null;
                try {
                    Log.e("link", link);
                    doc = Jsoup.connect(link).get();
                    Elements trs = doc.select("#container table tr");
                    for (int i = 1; i < trs.size(); i++) {
                        Elements td = trs.get(i).getElementsByTag("td");
                        String book_isbn = td.get(4).text();
                        String book_name = td.get(1).text();
                        String book_author = td.get(2).text();
                        String href = td.get(1).select("a").attr("abs:href");
                        bookModel.add(new BookModel(book_isbn, book_name, book_author, href));
                    }
                    Log.e("bookModel", bookModel.toString());
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
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
        isLoading = false;//设置正在刷新标志位false
        mHandler.removeCallbacksAndMessages(null);
//        rvLibrary.removeFooterView(loadMoreView);//如果是最后一页的话，则将其从ListView中移出
    }
}
