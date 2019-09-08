package com.wuyou.web.italker.push.provider;

import com.google.common.base.Strings;
import com.wuyou.web.italker.push.bean.api.base.ResponseModel;
import com.wuyou.web.italker.push.bean.db.User;
import com.wuyou.web.italker.push.factory.UserFactory;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.provider.AuthRequestFilter
 * 用户: _VIEW
 * 时间: 2019/9/8,22:57
 * 描述: 用于所有请求的接口的过滤和拦截
 */
@Provider
public class AuthRequestFilter implements ContainerRequestFilter {
    //过滤方法
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        //检测是否是登录注册接口
        String relationPath = ((ContainerRequest) requestContext).getPath(false);
        if (relationPath.startsWith("account/login") || relationPath.startsWith("account/register")) {
            //直接走正常逻辑，不做拦截
            return;
        }
        //从header中获取第一个Token节点
        String token = requestContext.getHeaders().getFirst("token");
        //查询自己的信息
        if (!Strings.isNullOrEmpty(token)) {
            final User self = UserFactory.findByToken(token);
            if (self != null) {
                //给当前请求添加一个上下文
                requestContext.setSecurityContext(new SecurityContext() {
                    //主体部分
                    @Override
                    public Principal getUserPrincipal() {
                        //User 实现 Principal 接口
                        return self;
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        //在这里写入权限，role是权限名，可以管理管理员权限
                        return true;
                    }

                    @Override
                    public boolean isSecure() {
                        //检查是否是https
                        return false;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return null;
                    }
                });
                //写入上下文信息后就返回
                return;
            }
        }
        //直接返回一个账户需要登录的model
        ResponseModel responseModel = ResponseModel.buildAccountError();
        //构建一个返回
        Response response = Response.status(Response.Status.OK).entity(responseModel).build();
        //停止一个请求的继续下发，调用该方法后直接返回请求，不会走到service
        requestContext.abortWith(response);
    }
}
