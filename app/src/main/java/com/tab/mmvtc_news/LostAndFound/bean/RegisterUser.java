package com.tab.mmvtc_news.LostAndFound.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/9/18.
 */

public class RegisterUser extends BmobObject {
    private String registerName;
    private String registerPassword;

    public String getRegisterName() {
        return registerName;
    }

    public void setRegisterName(String registerName) {
        this.registerName = registerName;
    }

    public String getRegisterPassword() {
        return registerPassword;
    }

    public void setRegisterPassword(String registerPassword) {
        this.registerPassword = registerPassword;
    }
}
