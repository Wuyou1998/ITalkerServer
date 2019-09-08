package com.wuyou.web.italker.push.Service;

import com.google.common.base.Strings;
import com.wuyou.web.italker.push.bean.api.base.ResponseModel;
import com.wuyou.web.italker.push.bean.api.user.UpdateInfoModel;
import com.wuyou.web.italker.push.bean.card.UserCard;
import com.wuyou.web.italker.push.bean.db.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.Service.UserService
 * 用户: _VIEW
 * 时间: 2019/9/8,21:40
 * 描述: 用户信息处理
 */
@Path("/user")
public class UserService extends BaseService {

    //用户信息修改接口,返回自己的个人信息
    @POST//不需要写@Path，就是当前目录//
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> update(UpdateInfoModel model) {
        if (!UpdateInfoModel.check(model))
            return ResponseModel.buildParameterError();
        //拿到自己的个人信息
        User self = getSelf();
        UserCard userCard = new UserCard(self, true);
        return ResponseModel.buildOk(userCard);

    }
}
