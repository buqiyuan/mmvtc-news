package com.tab.mmvtc_news.LostAndFound;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tab.mmvtc_news.LostAndFound.bean.LostInfomationReq;
import com.tab.mmvtc_news.R;
import com.tab.mmvtc_news.jwc.LoginActivity;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by 卜启缘 on 2019/10/28.
 */

public class AddLostInformationActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back;
    private ImageView add;
    private EditText title;
    private EditText phoneNum;
    private EditText desc;
    private final static int REQUEST_CODE = 666;
    private LostInfomationReq infomationReq;
    private boolean isChangeInfos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_lost_infos_activity);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        add = (ImageView) findViewById(R.id.iv_add);
        title = (EditText) findViewById(R.id.et_title);
        phoneNum = (EditText) findViewById(R.id.et_phone_num);
        desc = (EditText) findViewById(R.id.et_desc);
    }

    private void initData() {
        infomationReq = (LostInfomationReq) getIntent().getSerializableExtra("editData");
        if (infomationReq != null) {
            isChangeInfos = true;//设置是否是信息更新操作
            title.setText(infomationReq.getTitle());
            phoneNum.setText(infomationReq.getPhoneNum());
            desc.setText(infomationReq.getDesc());
        }
    }

    private void initListener() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            finish();//返回
        });
        add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                String password = getSharedPreferences("user", MODE_PRIVATE).getString("password","");
                Log.e("password", password);
                if ( TextUtils.isEmpty(password)){
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setIcon(R.drawable.app_icon);//设置图标
//                    builder.setTitle("未登录");//设置对话框的标题
                    builder.setMessage("登录后才能发布，现在去登陆吗？");//设置对话框的内容
                    //这个是设置确定按钮
                    builder.setPositiveButton("确定", (arg0, arg1) -> {
                        Intent intent = new Intent(AddLostInformationActivity.this, LoginActivity.class);
                        intent.putExtra("addLost", true);
                        startActivity(intent);
                    });
                    //取消按钮
                    builder.setNegativeButton("取消", (arg0, arg1) -> Toast.makeText(AddLostInformationActivity.this, "取消登录",Toast.LENGTH_SHORT).show());
                    AlertDialog b=builder.create();
                    b.show();
                }else {
                    addData();
                }
                break;
            default:
                break;
        }
    }

    /**
     * @param titleName 标题
     * @param num       电话号码
     * @param descridle 描述
     */
    private void updataInfo(String titleName, String num, String descridle) {
        LostInfomationReq lostInfomationReq = new LostInfomationReq();
        lostInfomationReq.setUsername(getSharedPreferences("user", MODE_PRIVATE).getString("name",""));//设置用户名
        lostInfomationReq.setTitle(titleName);//titleName为用户输入的标题
        lostInfomationReq.setPhoneNum(num);//num为用户输入的号码
        lostInfomationReq.setDesc(descridle);//descridle为信息描述
        lostInfomationReq.update(infomationReq.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("更新信息成功");
                    //更新数据后提示主界面进行数据刷新
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void addData() {
        String titleName = title.getText().toString().trim();
        String Num = phoneNum.getText().toString().trim();
        String descridle = desc.getText().toString().trim();

        if (TextUtils.isEmpty(titleName)) {
            showToast("标题不能为空");
            return;
        }

        if (TextUtils.isEmpty(Num)) {
            showToast("手机号码不能为空");
            return;
        }

        if (TextUtils.isEmpty(descridle)) {
            showToast("描述不能为空");
            return;
        }

        //判断是发表新的信息还是更改信息
        if (isChangeInfos) {
            updataInfo(titleName, Num, descridle);
        } else {
            publishLostInfo(titleName, Num, descridle);
        }
    }

    /**
     * @param titleName 标题
     * @param num       电话号码
     * @param descridle 描述
     */
    private void publishLostInfo(String titleName, String num, String descridle) {
        LostInfomationReq lostInfomationReq = new LostInfomationReq();
        lostInfomationReq.setUsername(getSharedPreferences("user", MODE_PRIVATE).getString("name",""));//设置用户名
        lostInfomationReq.setTitle(titleName);//titleName为用户输入的标题
        lostInfomationReq.setPhoneNum(num);//num为用户输入的号码
        lostInfomationReq.setDesc(descridle);//descridle为信息描述
        lostInfomationReq.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    showToast("招领信息发布成功");
                    //成功后提示主界面刷新数据
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    //成功后将页面销毁
                    finish();
                } else {
                    showToast("信息发布失败");
                }
            }
        });
    }

    /**
     * @param msg 打印信息
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
