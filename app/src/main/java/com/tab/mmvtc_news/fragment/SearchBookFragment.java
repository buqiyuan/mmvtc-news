package com.tab.mmvtc_news.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.hjq.toast.ToastUtils;
import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.activity.BookDetailActivity;
import com.tab.mmvtc_news.adapter.MySimpleAdapter;
import com.tab.mmvtc_news.adapter.MySimpleHolder;
import com.tab.mmvtc_news.jsonBean.Content;
import com.tab.mmvtc_news.jsonBean.JsonRootBean;
import com.tab.mmvtc_news.okhttpUtil.OkHttpUtils;
import com.tab.mmvtc_news.okhttpUtil.callback.BitmapCallback;
import com.tab.mmvtc_news.okhttpUtil.callback.StringCallback;
import com.tab.mmvtc_news.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

public class SearchBookFragment extends BaseFragment {

    private EditText etLibrary;
    private RecyclerView rvLibrary;
    private MySimpleAdapter<Content> mAdapter;
    private List<Content> content;
    private PullRefreshLayout layout;
    public boolean isLoading = false;//表示是否正处于加载状态
    private Spinner spinner;

    @Override
    protected String getTitleName() {
        return null;
    }

    @Override
    protected int resourceViewId() {
        return R.layout.fragment_search_book;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    protected void initView(View view ) {
        TextView btnSearch = (TextView) view.findViewById(R.id.btn_search);
        etLibrary = (EditText) view.findViewById(R.id.et_library);
        rvLibrary = (RecyclerView) view.findViewById(R.id.rv_search_library);
        layout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        spinner = (Spinner) view.findViewById(R.id.sp_library);
        /*
         * 动态添显示下来菜单的选项，可以动态添加元素
         */
        ArrayList<String> list = new ArrayList<String>();
        list.add("任意词");
        list.add("题名");
        list.add("责任者");
        list.add("主题词");
        list.add("ISBN");
        list.add("分类号");
        list.add("索书号");
        list.add("出版社");
        list.add("丛书名");
        /*
         * 第二个参数是显示的布局
         * 第三个参数是在布局显示的位置id
         * 第四个参数是将要显示的数据
         */
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, R.id.textView,list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new spinnerListener());

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSearch();
            }
        });
        searchBook("鲁迅");
    }
    class spinnerListener implements android.widget.AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            String selected = parent.getItemAtPosition(position).toString();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            System.out.println("nothingSelect");
        }
    }
    //    执行搜索
    private void doSearch() {
        String keyword = etLibrary.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            ToastUtils.show("关键词不能为空！");
        } else {
            searchBook(keyword);
        }
    }

    protected void initData() {
        mAdapter = null;
        rvLibrary.setLayoutManager(new LinearLayoutManager(getActivity()));
        //设置适配器
        mAdapter = new MySimpleAdapter<Content>(getActivity(), R.layout.item_search_book, content) {
            @Override
            public void onBindView(MySimpleHolder holder, int position) {
                Content list = mDatas.get(position);
                final ImageView iv_book = holder.getView(R.id.iv_book);
                final TextView book_total = holder.getView(R.id.tv_book_total);
                final TextView book_remain_total = holder.getView(R.id.tv_book_remain_total);

                getMarcNo(position, iv_book, book_total, book_remain_total);

                TextView book_name = holder.getView(R.id.tv_book_name);
                book_name.setText(list.getTitle());

                TextView tv_book_publisher = holder.getView(R.id.tv_book_publisher);
                tv_book_publisher.setText(list.getPublisher());


                TextView tv_author = holder.getView(R.id.tv_book_author);
                tv_author.setText(list.getAuthor());
            }
        };
        rvLibrary.setAdapter(mAdapter);
        //设置mRecyclerview的item单击事件
        mAdapter.setOnItemClickListener(new MySimpleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                String href = "http://hwlibsys.mmvtc.cn:8080/opac/item.php?marc_no=" + content.get(position).getMarcRecNo();
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
                        if (content != null) {
                            content.clear();
                        }
                        //再重新请求数据，通常是请求网络，获取最新数据
                        doSearch();
                        //刷新适配器
                        mAdapter.notifyDataSetChanged();
                        //刷新完成
                        Toast.makeText(getActivity(), "刷新成功！", Toast.LENGTH_SHORT).show();
                        layout.setRefreshing(false);
                    }
                }, 0);
            }
        });
    }


    //设置图书封面和馆藏
    private void getMarcNo(int position, final ImageView iv_book, final TextView book_total, final TextView book_remain_total) {
        OkHttpUtils
                .get()//
                .url("http://hwlibsys.mmvtc.cn:8080/opac/ajax_isbn_marc_no.php")//
                .addParams("marc_no", content.get(position).getMarcRecNo())
                .addParams("isbn", content.get(position).getIsbn())
                .tag(this)//
                .build()//
                .connTimeOut(20000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.e("getImgErr","获取图书封面失败！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String imageUrl = jsonObject.getString("image");
                            String lendAvl = jsonObject.getString("lendAvl");
                            String[] temp = null;
                            temp = lendAvl.split("<br>");

                            book_total.setText(Jsoup.parse(temp[0]).text());
                            book_remain_total.setText(Jsoup.parse(temp[1]).text());
                            if (!TextUtils.isEmpty(imageUrl.trim())) {
                                OkHttpUtils
                                        .get()
                                        .url(imageUrl)
                                        .build()
                                        .execute(new BitmapCallback() {
                                            @Override
                                            public void onError(Call call, Exception e, int id) {
//                                                ToastUtils.show("获取图书封面失败！");
                                            }

                                            @Override
                                            public void onResponse(Bitmap bitmap, int id) {
                                                iv_book.setImageBitmap(bitmap);
                                            }
                                        });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void searchBook(final String keyword) {
        rvLibrary.setEnabled(false);
        String jsonText = "{'searchWords':[{'fieldList':[{'fieldCode':'','fieldValue':'" + keyword + "'}]}],'filters':[],'limiter':[],'sortField':'relevance','sortType':'desc','pageSize':50,'pageCount':1,'locale':'zh_CN','first':true}";
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            OkHttpUtils
                    .postString()
                    .url("http://hwlibsys.mmvtc.cn:8080/opac/ajax_search_adv.php")
                    .content(jsonObject.toString())
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ToastUtils.show("检索图书失败！");
                        }

                        @Override
                        public void onResponse(String response, int id) {
//                            Log.e("respone", response);
                            Gson gson = new Gson();
                            //GSON直接解析成对象
                            JsonRootBean result = gson.fromJson(response, JsonRootBean.class);
                            if (result.getTotal() > 0) {
                                //对象中拿到集合
                                content = result.getContent();
                                LogUtil.e("requestBody", gson.toJson(content));
                                LogUtil.e("response", response);
//                            Toast.makeText(getActivity(), gson.toJson(content), Toast.LENGTH_LONG).show();
//                            初始化搜索结果
                                initData();
                                rvLibrary.setEnabled(true);
                            } else {
                                if (content != null) content.clear();
                                initData();
                                ToastUtils.show("没有检索到\"" + keyword + "\"相关的图书！");
                            }
                        }
                    });
        } else {
            ToastUtils.show("JSON数据有误！");
        }
    }

    /**
     * 加载完成
     */
    public void loadComplete() {
        isLoading = false;//设置正在刷新标志位false
//        rvLibrary.removeFooterView(loadMoreView);//如果是最后一页的话，则将其从ListView中移出
    }
}
