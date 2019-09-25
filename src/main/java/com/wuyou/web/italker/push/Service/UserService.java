package com.wuyou.web.italker.push.Service;

import com.google.common.base.Strings;
import com.wuyou.web.italker.push.bean.api.base.ResponseModel;
import com.wuyou.web.italker.push.bean.api.user.UpdateInfoModel;
import com.wuyou.web.italker.push.bean.card.UserCard;
import com.wuyou.web.italker.push.bean.db.User;
import com.wuyou.web.italker.push.factory.PushFactory;
import com.wuyou.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.Service.UserService
 * 用户: _VIEW
 * 时间: 2019/9/8,21:40
 * 描述: 用户信息处理
 */
@Path("/user")
public class UserService extends BaseService {

    //用户信息修改接口,返回自己的个人信息
    @PUT//不需要写@Path，就是当前目录//
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> update(UpdateInfoModel model) {
        if (!UpdateInfoModel.check(model))
            return ResponseModel.buildParameterError();
        //拿到自己的个人信息
        User self = getSelf();
        // 更新用户信息
        self = model.updateToUser(self);
        self = UserFactory.update(self);
        UserCard userCard = new UserCard(self, true);
        return ResponseModel.buildOk(userCard);

    }

    //拉取联系人
    @GET
    @Path("/contact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> contact() {
        User self = getSelf();
        //拿到我的联系人
        List<User> userList = UserFactory.contacts(self);
        List<UserCard> userCards = userList.stream()
                //map操作相当于转置操作，User->UserCard
                .map(user -> new UserCard(user, true)).collect(Collectors.toList());
        //返回
        return ResponseModel.buildOk(userCards);
    }

    //关注人,修改操作使用put
    //简化：关注人的操作其实是双方同时关注
    @PUT
    @Path("/follow/{followId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> follow(@PathParam("followId") String followId) {
        User self = getSelf();
        //不能关注自己
        if (self.getId().equalsIgnoreCase(followId) || Strings.isNullOrEmpty(followId)) {
            return ResponseModel.buildParameterError();
        }
        //找到我要关注的人
        User followUser = UserFactory.findById(followId);
        if (followUser == null) {
            return ResponseModel.buildNotFoundUserError(followId);
        }
        //备注默认没有，后面可以扩展
        followUser = UserFactory.follow(self, followUser, null);
        if (followUser == null) {
            //关注失败，返回服务器异常
            return ResponseModel.buildServiceError();
        }
        //通知我关注的人我关注了他
        //给他发送一个我的信息过去
        PushFactory.pushFollow(followUser, new UserCard(self));
        //返回关注人信息
        return ResponseModel.buildOk(new UserCard(followUser, true));
    }

    //获取某人的信息
    @GET
    @Path("{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> getUser(@PathParam("userId") String id) {
        if (Strings.isNullOrEmpty(id)) {
            //参数异常
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        if (self.getId().equalsIgnoreCase(id)) {
            //返回自己，不必查询数据库
            return ResponseModel.buildOk(new UserCard(self, true));
        }

        User user = UserFactory.findById(id);
        if (user == null) {
            //没找到
            return ResponseModel.buildNotFoundUserError(id);
        }
        //如果我们之间而有关注的信息，则表示我已经关注了你
        boolean isFollow = UserFactory.getUserFollow(self, user) != null;
        return ResponseModel.buildOk(new UserCard(user, isFollow));
    }

    //搜索人的接口实现，不涉及数据更改，是一个get请求
    //为了简化分页，每次只返回20条数据
    @GET
    //名字可以为任意字符，也可以为空
    @Path("search/{name:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> search(@DefaultValue("") @PathParam("name") String name) {
        User self = getSelf();
        //先查询数据
        List<User> searchUsers = UserFactory.search(name);
        //把查询到的user封装为userCard,判断这些人中是否有我已经关注的人
        //如果有，则返回的关注状态中应该已经设置好状态

        //拿出我的联系人
        List<User> contacts = UserFactory.contacts(self);
        //User->UserCards
        List<UserCard> userCards = searchUsers.stream()
                .map(user -> {
                    //判断这个人是不是我，或者是否在我的联系人中
                    boolean isFollow = user.getId().equalsIgnoreCase(self.getId()) ||
                            //进行联系人的任意匹配，匹配其中的Id字段
                            contacts.stream().anyMatch(contactUser -> contactUser.getId().equalsIgnoreCase(user.getId()));
                    return new UserCard(user, isFollow);
                }).collect(Collectors.toList());

        return ResponseModel.buildOk(userCards);
    }
}
