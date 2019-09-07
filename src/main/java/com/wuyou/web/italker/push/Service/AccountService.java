package com.wuyou.web.italker.push.Service;

import com.wuyou.web.italker.push.bean.api.account.RegisterModel;
import com.wuyou.web.italker.push.bean.card.UserCard;
import com.wuyou.web.italker.push.bean.db.User;
import com.wuyou.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.Service.AccountService
 * 用户: _VIEW
 * 时间: 2019/9/2,9:22
 * 描述: 账户相关服务
 */
@Path("/account")
public class AccountService {
    @GET
    @Path("/login")
    public String get() {
        return "you get the login";
    }

    @POST
    @Path("/register")
    //指定请求与返回的响应体格式为Json
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserCard register(RegisterModel model) {
        //先判断账户有没有注册过
        User user = UserFactory.findByPhone(model.getAccount().trim());
        UserCard userCard = null;
        //注册过就返回
        if (user != null) {
            userCard = new UserCard();
            userCard.setName("The account already exists!账号已经存在");
            return userCard;
        }

        //判断网名有没有被使用
        user = UserFactory.findByName(model.getName().trim());
        //注册过就返回
        if (user != null) {
            userCard = new UserCard();
            userCard.setName("The nickname has been used!网名被用了");
            return userCard;
        }

        //都没有就开始注册
        user = UserFactory.register(model.getAccount(), model.getPassword(), model.getName());
        if (user != null) {
            userCard = new UserCard();
            userCard.setName(user.getName());
            userCard.setPhone(user.getPhone());
            userCard.setSex(user.getSex());
            userCard.setModifyAt(user.getUpdateAt());
            userCard.setFollow(true);
        }
        return userCard;
    }
}
