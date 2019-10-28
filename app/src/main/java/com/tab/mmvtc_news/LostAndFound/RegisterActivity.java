//package com.tab.mmvtc_news.LostAndFound;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.showly.bmobdemo.bean.RegisterUser;
//import com.tab.mmvtc_news.R;
//
//import java.util.List;
//
//import cn.bmob.v3.BmobQuery;
//import cn.bmob.v3.BmobUser;
//import cn.bmob.v3.exception.BmobException;
//import cn.bmob.v3.listener.FindListener;
//import cn.bmob.v3.listener.SaveListener;
//
///**
// * Created by Administrator on 2018/9/18.
// */
//
//public class RegisterActivity extends Activity implements View.OnClickListener {
//
//    private EditText accountRegisterName;
//    private EditText accountRegisterPassword;
//    private Button registerBtn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.register_account);
//
//        initView();
//        initData();
//        initListener();
//    }
//
//    private void initView() {
//        accountRegisterName = (EditText) findViewById(R.id.i8_accountRegister_name);
//        accountRegisterPassword = (EditText) findViewById(R.id.i8_accountRegister_password);
//        registerBtn = (Button) findViewById(R.id.i8_accountRegistern_toRegister);
//    }
//
//    private void initData() {
//
//    }
//
//    private void initListener() {
//        registerBtn.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.i8_accountRegistern_toRegister:
//                //registerAccount();//owner
//                bmobRegisterAccount();//Bmob
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void bmobRegisterAccount() {
//        final String registerName = accountRegisterName.getText().toString().trim();//账号
//        final String registerPassword = accountRegisterPassword.getText().toString().trim();//密码
//
//        if (TextUtils.isEmpty(registerName) || TextUtils.isEmpty(registerPassword)) {
//            showToast("注册账号或密码为空");
//            return;
//        }
//
//        BmobUser bmobUser = new BmobUser();
//        bmobUser.setUsername(registerName);
//        bmobUser.setPassword(registerPassword);
//        bmobUser.signUp(new SaveListener<BmobUser>() {
//            @Override
//            public void done(BmobUser bmobUser, BmobException e) {
//                if (e == null) {
//                    showToast("恭喜，注册账号成功");
//                    finish();
//                } else {
//                    showToast("register fail:" + e.getMessage());
//                }
//            }
//        });
//    }
//
//    /**
//     * 账号注册
//     */
//    private void registerAccount() {
//        final String registerName = accountRegisterName.getText().toString().trim();
//        final String registerPassword = accountRegisterPassword.getText().toString().trim();
//
//        if (TextUtils.isEmpty(registerName) || TextUtils.isEmpty(registerPassword)) {
//            showToast("注册账号或密码为空");
//            return;
//        }
//
//        //账号查询
//        BmobQuery<RegisterUser> registerUserBmobQuery = new BmobQuery<>();
//        registerUserBmobQuery.order("-createdAt");//按时间排序
//        registerUserBmobQuery.findObjects(new FindListener<RegisterUser>() {
//            @Override
//            public void done(List<RegisterUser> lists, BmobException e) {
//                for (RegisterUser list : lists) {
//                    if (registerName.equals(list.getRegisterName())) {
//                        showToast("账号已被注册，请重新输入");
//                    } else {
//                        registerAccount(registerName, registerPassword);
//                    }
//                }
//            }
//        });
//    }
//
//    /**
//     * 注册账号，将用户账号密码添加进数据库
//     *
//     * @param registerName     注册名
//     * @param registerPassword 密码
//     */
//    private void registerAccount(String registerName, String registerPassword) {
//        RegisterUser registerUser = new RegisterUser();
//        registerUser.setRegisterName(registerName);
//        registerUser.setRegisterPassword(registerPassword);
//        registerUser.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                if (e == null) {
//                    showToast("恭喜，注册账号成功");
//                    finish();
//                } else {
//                    showToast("注册账号失败");
//                }
//            }
//        });
//    }
//
//    private void showToast(String msg) {
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//    }
//}
