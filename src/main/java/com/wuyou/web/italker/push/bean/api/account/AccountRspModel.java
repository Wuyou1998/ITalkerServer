package com.wuyou.web.italker.push.bean.api.account;

import com.google.gson.annotations.Expose;
import com.wuyou.web.italker.push.bean.card.UserCard;
import com.wuyou.web.italker.push.bean.db.User;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.bean.api.account.AccountRspModel
 * 用户: _VIEW
 * 时间: 2019/9/7,23:52
 * 描述: 账户部分返回的model
 */
public class AccountRspModel {
    //用户基本信息
    @Expose
    private UserCard userCard;
    //当前登录的账号
    @Expose
    private String account;
    //当前登录成功后获取的Token，可以通过Token获取用户的所有信息
    @Expose
    private String token;
    //表示是否绑定到了设备PushId
    @Expose
    private boolean isBind;

    public AccountRspModel(User user) {
        this(user, false);
    }

    public AccountRspModel(User user, boolean isBind) {
        this.account = user.getPhone();
        this.token = user.getToken();
        this.isBind = isBind;
        this.userCard = new UserCard(user);
    }

    public UserCard getUserCard() {
        return userCard;
    }

    public void setUserCard(UserCard userCard) {
        this.userCard = userCard;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }
}
