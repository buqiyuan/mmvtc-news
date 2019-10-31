package com.tab.mmvtc_news.LostAndFound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tab.mmvtc_news.LostAndFound.adapter.LostAndFoundAdapter;
import com.tab.mmvtc_news.LostAndFound.bean.LostInfomationReq;
import com.tab.mmvtc_news.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by 卜启缘 on 2019/10/19.
 */

public class LostAndFoundActivity extends AppCompatActivity implements View.OnClickListener, LostAndFoundAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private TextView addBtn;
    private LostAndFoundAdapter lostAndFoundAdapter;
    private long exitTime = 0;
    private final static int REQUEST_CODE = 999;
    private List<LostInfomationReq> lostInfomationReqList;
    private String password;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lostandfound_main_activity);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.rl_recyclerview);
        addBtn = findViewById(R.id.tv_add);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        lostAndFoundAdapter = new LostAndFoundAdapter(LostAndFoundActivity.this);
        lostAndFoundAdapter.setLongClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            finish();//返回
        });
    }

    private void initData() {
        BmobQuery<LostInfomationReq> lostInfomationReqBmobQuery = new BmobQuery<>();
        lostInfomationReqBmobQuery.order("-updatedAt");//排序
        lostInfomationReqBmobQuery.findObjects(new FindListener<LostInfomationReq>() {
            @Override
            public void done(List<LostInfomationReq> list, BmobException e) {
                if (e == null) {
                    lostInfomationReqList = list;
                    lostAndFoundAdapter.setData(list);
                    recyclerView.setAdapter(lostAndFoundAdapter);
                } else {
                    showToast("查询数据失败");
                }
            }
        });
    }


    private void initListener() {
        addBtn.setOnClickListener(this);
    }

    /**
     * @param msg 打印信息
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                Intent intent = new Intent(LostAndFoundActivity.this, AddLostInformationActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    refreshData();//数据刷新
                }
                break;
            default:
                break;
        }
    }

    /**
     * 查询数据库中最新的数据
     * */
    private void refreshData() {
        BmobQuery<LostInfomationReq> lostInfomationReqBmobQuery = new BmobQuery<>();
        lostInfomationReqBmobQuery.order("-updatedAt");//按更新时间排序
        lostInfomationReqBmobQuery.findObjects(new FindListener<LostInfomationReq>() {
            @Override
            public void done(List<LostInfomationReq> list, BmobException e) {
                if (e == null) {
                    lostInfomationReqList = list;
                    lostAndFoundAdapter.setData(list);
                    lostAndFoundAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    public void onEditOrDeleteClick(int position, int code) {

        if (code == LostAndFoundAdapter.EDIT_CODE) {
            Intent intent = new Intent(LostAndFoundActivity.this, AddLostInformationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("editData", lostInfomationReqList.get(position));
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_CODE);
        } else if (code == LostAndFoundAdapter.DELETE_CODE) {
            deleteItemData(position);
        }
    }

    private void deleteItemData(final int position) {
        if (lostInfomationReqList.size() != 0) {
            LostInfomationReq lostInfomationReq = new LostInfomationReq();
            lostInfomationReq.setObjectId(lostInfomationReqList.get(position).getObjectId());
            lostInfomationReq.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        lostInfomationReqList.remove(position);
                        lostAndFoundAdapter.setData(lostInfomationReqList);
                        lostAndFoundAdapter.notifyDataSetChanged();
                    } else {
                        showToast("删除数据失败");
                    }
                }
            });
        }
    }
}
