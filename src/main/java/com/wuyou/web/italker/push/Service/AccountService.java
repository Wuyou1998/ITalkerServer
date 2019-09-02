package com.wuyou.web.italker.push.Service;

import com.wuyou.web.italker.push.bean.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.Service.AccountService
 * 用户: _VIEW
 * 时间: 2019/9/2,9:22
 * 描述: ToDo
 */
@Path("/account")
public class AccountService {
    @GET
    @Path("/login")
    public String get() {
        return "you get the login";
    }

    @POST
    @Path("/login")
    //指定请求与返回的响应体格式为Json
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User post() {
        User user = new User();
        user.setName("wuyou");
        user.setSex(1);
        return user;
    }
}
