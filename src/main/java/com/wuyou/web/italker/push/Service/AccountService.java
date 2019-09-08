package com.wuyou.web.italker.push.Service;

import com.google.common.base.Strings;
import com.wuyou.web.italker.push.bean.api.account.AccountRspModel;
import com.wuyou.web.italker.push.bean.api.account.LoginModel;
import com.wuyou.web.italker.push.bean.api.account.RegisterModel;
import com.wuyou.web.italker.push.bean.api.base.ResponseModel;
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
public class AccountService extends BaseService {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> login(LoginModel model) {
        if (!LoginModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        User user = UserFactory.login(model.getAccount(), model.getPassword());
        if (user != null) {
            //如果携带了pushId，则绑定
            if (!Strings.isNullOrEmpty(model.getPushId())) {
                return bind(user, model.getPushId());
            }
            //登录成功返回当前账户
            AccountRspModel accountRspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(accountRspModel);
        } else {
            //登录失败
            return ResponseModel.buildLoginError();
        }
    }

    @POST
    @Path("/register")
    //指定请求与返回的响应体格式为Json
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> register(RegisterModel model) {
        if (!RegisterModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        //先判断账户有没有注册过
        User user = UserFactory.findByPhone(model.getAccount().trim());
        //注册过就返回
        if (user != null) {
            //账户已存在
            return ResponseModel.buildHaveAccountError();
        }

        //判断网名有没有被使用
        user = UserFactory.findByName(model.getName().trim());
        //注册过就返回
        if (user != null) {
            return ResponseModel.buildHaveNameError();
        }

        //都没有就开始注册逻辑
        user = UserFactory.register(model.getAccount(), model.getPassword(), model.getName());
        if (user != null) {
            //如果携带了pushId，则绑定
            if (!Strings.isNullOrEmpty(model.getPushId())) {
                return bind(user, model.getPushId());
            }
            //返回当前账户
            AccountRspModel accountRspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(accountRspModel);
        } else {
            //注册异常
            return ResponseModel.buildRegisterError();
        }
    }

    //绑定，pushId从url地址中获取
    @POST
    @Path("/bind/{pushId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> bind(@HeaderParam("token") String token,
                                               @PathParam("pushId") String pushId) {
        if (Strings.isNullOrEmpty(token) || Strings.isNullOrEmpty(pushId))
            return ResponseModel.buildParameterError();

        //拿到自己的个人信息
        User user = getSelf();
        return bind(user, pushId);

    }

    /**
     * 绑定操作
     *
     * @param self   自己
     * @param pushId pushId
     * @return user
     */
    private ResponseModel<AccountRspModel> bind(User self, String pushId) {
        User user = UserFactory.bindPushId(self, pushId);
        if (user == null) {
            //绑定失败返回异常
            return ResponseModel.buildServiceError();
        } else {
            //返回当前账户，并且已经绑定了
            return ResponseModel.buildOk(new AccountRspModel(user, true));
        }
    }
}
