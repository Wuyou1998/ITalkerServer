package com.wuyou.web.italker.push.Service;

import com.wuyou.web.italker.push.bean.db.User;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.Service.BaseService
 * 用户: _VIEW
 * 时间: 2019/9/8,23:32
 * 描述: 服务公共部分
 */
public class BaseService {
    //上下文注解，会自动给context赋值，具体值为拦截器中返回的
    @Context
    protected SecurityContext context;

    /**
     * 从上下文中直接获取自己的信息
     * @return user
     */
    protected User getSelf() {

        return (User) context.getUserPrincipal();
    }

}
