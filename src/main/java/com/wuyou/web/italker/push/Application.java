package com.wuyou.web.italker.push;

import com.wuyou.web.italker.push.Service.AccountService;
import com.wuyou.web.italker.push.provider.GsonProvider;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.Application
 * 用户: _VIEW
 * 时间: 2019/9/1,23:42
 * 描述: 入口
 */
public class Application extends ResourceConfig {
    public Application() {
        //注册逻辑处理的包名
        packages(AccountService.class.getPackage().getName());

        //注册Json和Logger
        register(GsonProvider.class);
        register(Logger.class);
    }
}
