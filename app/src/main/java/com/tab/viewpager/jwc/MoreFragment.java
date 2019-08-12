package com.tab.viewpager.jwc;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tab.viewpager.R;

import static android.content.Context.MODE_PRIVATE;


public class MoreFragment extends BaseFragment implements View.OnClickListener {
    private TextView tvChangePassword;
    private TextView tvGradeTest;
    private TextView tvAbout;
    private TextView tvExit;
    private AlertDialog alertDialog;

    @Override
    protected String getTitleName() {
        return getString(R.string.more);
    }

    @Override
    protected int resourceViewId() {
        return R.layout.fragment_more;
    }

    @Override
    protected void initView(View view) {
        tvChangePassword = (TextView) view.findViewById(R.id.tv_change_password);
        tvChangePassword.setOnClickListener(this);
        tvGradeTest = (TextView) view.findViewById(R.id.tv_grade_test);
        tvGradeTest.setOnClickListener(this);
        tvAbout = (TextView) view.findViewById(R.id.tv_about);
        tvAbout.setOnClickListener(this);
        tvExit = (TextView) view.findViewById(R.id.tv_exit);
        tvExit.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.tv_exit) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SharedPreferences sp = getActivity().getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("password");
            editor.commit();
            startActivity(intent);
        } else if (viewId == R.id.tv_grade_test) {
            Toast.makeText(getContext(), "啥也没有！占位置的", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.tv_change_password) {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (viewId == R.id.tv_about) {
            if (alertDialog == null) {
                alertDialog = new AlertDialog.Builder(getContext()).
                        setMessage("茂名职业技术学院教务管理系统模拟登陆\n\n" +
                                "Android实训作业专用\n\n" +
                                "开发单位: 正方软件股份有限公司").
                        setPositiveButton(R.string.ok, null).
                        create();
            }
            alertDialog.show();
        }
    }
}
