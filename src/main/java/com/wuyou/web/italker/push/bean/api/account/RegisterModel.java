package com.wuyou.web.italker.push.bean.api.account;

import com.google.gson.annotations.Expose;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.bean.api.account.RegisterModel
 * 用户: _VIEW
 * 时间: 2019/9/7,20:42
 * 描述: 注册模块
 */
public class RegisterModel {
    @Expose
    private String account;
    @Expose
    private String password;
    @Expose
    private String name;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
