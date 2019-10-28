package com.tab.mmvtc_news.LostAndFound;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tab.mmvtc_news.LostAndFound.bean.Person;
import com.tab.mmvtc_news.R;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btnAddData;
    private Button btnGetData;
    private Button btnUpdataData;
    private Button btnDeleteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and_found);

        //第一：默认初始化
        Bmob.initialize(this, "5178f13fc41ca9e649d9c12eee9a6d68");

        initView();
        initData();
        initListener();

    }

    private void initView() {
        btnAddData = (Button) findViewById(R.id.btn_add_data);
        btnGetData = (Button) findViewById(R.id.btn_get_data);
        btnUpdataData = (Button) findViewById(R.id.btn_change_data);
        btnDeleteData = (Button) findViewById(R.id.btn_delete_data);
    }

    private void initData() {

    }

    private void initListener() {
        btnAddData.setOnClickListener(this);
        btnGetData.setOnClickListener(this);
        btnUpdataData.setOnClickListener(this);
        btnDeleteData.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_data:
                addData();//添加数据
                break;
            case R.id.btn_get_data:
                getData();//获取数据
                break;
            case R.id.btn_change_data:
                updata();//更新数据
                break;
            case R.id.btn_delete_data:
                deleteData();//删除数据
                break;
            default:
                break;
        }
    }

    private void deleteData() {
        Person person = new Person();
        person.setObjectId("81512f4485");
        person.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("删除数据成功");
                } else {
                    showToast("删除数据失败--" + e.getMessage());
                }
            }
        });
    }

    private void updata() {
        Person person = new Person();
        person.setName("showly123");
        person.update("81512f4485", new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("更新数据成功");
                } else {
                    showToast("更新数据失败--" + e.getMessage());
                }
            }
        });
    }

    private void getData() {
        BmobQuery<Person> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject("81512f4485", new QueryListener<Person>() {
            @Override
            public void done(Person person, BmobException e) {
                if (e == null) {
                    showToast("name:" + person.getName() + "---" + "psw:" + person.getPassword());
                } else {
                    showToast("查询失败：" + e.getMessage());
                }
            }
        });
    }

    private void addData() {
        Person person = new Person();
        person.setName("showly");
        person.setPassword("123456");
        person.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    showToast("添加数据成功，回调数据为：" + s);
                } else {
                    showToast("添加失败：" + e.getMessage());
                }
            }
        });
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
