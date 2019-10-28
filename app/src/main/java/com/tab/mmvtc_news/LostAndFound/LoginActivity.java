//package com.tab.mmvtc_news.LostAndFound;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
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
// * Created by 卜启缘 on 2019/10/19.
// */
//
//public class LoginActivity extends Activity implements View.OnClickListener {
//    private EditText accountLoginName;
//    private EditText accountLoginPassword;
//    private Button loginBtn;
//    private TextView registerAccountBtn;
//    private ProgressBar progressBar;
//    private LinearLayout llLogin;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.login_accountlogin);
//
//        //第一：默认初始化
//        //Bmob.initialize(this, "7a69e455af36e9d2814f4016ece2e3c9");
//
//        initView();
//        initData();
//        initListener();
//    }
//
//    private void initView() {
//        accountLoginName = (EditText) findViewById(R.id.i8_accountLogin_name);
//        accountLoginPassword = (EditText) findViewById(R.id.i8_accountLogin_password);
//        loginBtn = (Button) findViewById(R.id.i8_accountLogin_toLogin);
//        registerAccountBtn = (TextView) findViewById(R.id.register_account_btn);
//        progressBar = (ProgressBar) findViewById(R.id.pb);
//        llLogin = (LinearLayout) findViewById(R.id.ll_login);
//    }
//
//    private void initData() {
//
//    }
//
//    private void showProgressBar() {
//        progressBar.setVisibility(View.VISIBLE);
//        llLogin.setVisibility(View.GONE);
//    }
//
//    private void hiddenProgressBar() {
//        progressBar.setVisibility(View.GONE);
//        llLogin.setVisibility(View.VISIBLE);
//    }
//
//    private void initListener() {
//        loginBtn.setOnClickListener(this);
//        registerAccountBtn.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.i8_accountLogin_toLogin:
//                //userAccountLogin();//账号登录 owner
//                bmobUserAccountLogin();//bmob登录
//                break;
//            case R.id.register_account_btn:
//                //跳转到注册界面
//                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                startActivity(intent);
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void bmobUserAccountLogin() {
//        final String accountName = accountLoginName.getText().toString().trim();//账号
//        final String accountPassword = accountLoginPassword.getText().toString().trim();//密码
//
//        if (TextUtils.isEmpty(accountName)) {
//            showToast("账号不能为空");
//            return;
//        }
//
//        if (TextUtils.isEmpty(accountPassword)) {
//            showToast("密码不能为空");
//            return;
//        }
//
//        //登录过程
//        showProgressBar();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //BmobUser类为Bmob后端云提供类
//                BmobUser bmobUser = new BmobUser();
//                bmobUser.setUsername(accountName);
//                bmobUser.setPassword(accountPassword);
//                bmobUser.login(new SaveListener<BmobUser>() {
//                    @Override
//                    public void done(BmobUser bmobUser, BmobException e) {
//                        if (e == null) {
//                            //登录成功后进入主界面
//                            Intent intent = new Intent(LoginActivity.this, LostAndFoundActivity.class);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            showToast(""+e.getMessage());
//                            hiddenProgressBar();//隐藏
//                        }
//                    }
//                });
//            }
//        }, 3000);
//    }
//
//    private void userAccountLogin() {
//        final String accountName = accountLoginName.getText().toString().trim();
//        final String accountPassword = accountLoginPassword.getText().toString().trim();
//
//        if (TextUtils.isEmpty(accountName)) {
//            showToast("账号不能为空");
//            return;
//        }
//
//        if (TextUtils.isEmpty(accountPassword)) {
//            showToast("密码不能为空");
//            return;
//        }
//
//        showProgressBar();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //查询用户有登录
//                BmobQuery<RegisterUser> userBmobQuery = new BmobQuery<>();
//                userBmobQuery.order("-createdAt");//按时间排序
//                userBmobQuery.findObjects(new FindListener<RegisterUser>() {
//                    @Override
//                    public void done(List<RegisterUser> lists, BmobException e) {
//                        if (e == null) {
//                            for (RegisterUser list : lists) {
//                                if (accountName.equals(list.getRegisterName())) {
//                                    if (accountPassword.equals(list.getRegisterPassword())) {
//                                        showToast("登录成功");
//                                        Intent intent = new Intent(LoginActivity.this, LostAndFoundActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                        return;
//                                    } else {
//                                        showToast("密码错误");
//                                        hiddenProgressBar();
//                                        return;
//                                    }
//                                }
//                            }
//                            showToast("账号未注册");
//                            hiddenProgressBar();
//                        }
//                    }
//                });
//            }
//        }, 3000);
//    }
//
//    /**
//     * @param msg 打印信息
//     */
//    private void showToast(String msg) {
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//    }
//
//}
