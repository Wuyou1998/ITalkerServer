package com.wuyou.web.italker.push.Service;

import com.google.common.base.Strings;
import com.wuyou.web.italker.push.bean.api.base.ResponseModel;
import com.wuyou.web.italker.push.bean.api.group.GroupCreateModel;
import com.wuyou.web.italker.push.bean.api.group.GroupMemberAddModel;
import com.wuyou.web.italker.push.bean.api.group.GroupMemberUpdateModel;
import com.wuyou.web.italker.push.bean.card.ApplyCard;
import com.wuyou.web.italker.push.bean.card.GroupCard;
import com.wuyou.web.italker.push.bean.card.GroupMemberCard;
import com.wuyou.web.italker.push.bean.db.Group;
import com.wuyou.web.italker.push.bean.db.GroupMember;
import com.wuyou.web.italker.push.bean.db.User;
import com.wuyou.web.italker.push.factory.GroupFactory;
import com.wuyou.web.italker.push.factory.PushFactory;
import com.wuyou.web.italker.push.factory.UserFactory;
import com.wuyou.web.italker.push.provider.LocalDateTimeConverter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.Service.GroupService
 * 用户: _VIEW
 * 时间: 2019/9/25,13:47
 * 描述: 群聊接口的入口
 */
@Path("/group")
public class GroupService extends BaseService {
    /**
     * 创建群
     *
     * @param model 基本参数
     * @return 群信息
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> create(GroupCreateModel model) {
        if (!GroupCreateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        //创建者
        User creator = getSelf();
        //创建者并不在表中
        model.getUsers().remove(creator.getId());
        if (model.getUsers().size() == 0)
            return ResponseModel.buildParameterError();
        //检查是否已有相同名字的群聊
        if (GroupFactory.findByName(model.getName()) != null) {
            return ResponseModel.buildHaveNameError();
        }

        List<User> userList = new ArrayList<>();
        for (String s : model.getUsers()) {
            User user = UserFactory.findById(s);
            if (user == null) {
                continue;
            }
            userList.add(user);
        }
        //没有一个成员
        if (userList.size() == 0)
            return ResponseModel.buildParameterError();

        Group group = GroupFactory.create(creator, model, userList);
        if (group == null) {
            //服务器异常
            return ResponseModel.buildServiceError();
        }

        //拿到管理员，即自己的信息
        GroupMember creatorMember = GroupFactory.getMember(creator.getId(), group.getId());
        if (creatorMember == null) {
            //服务器异常
            return ResponseModel.buildServiceError();
        }
        //拿到群成员，给所有的群成员发送信息：已经被添加到了XX群中
        Set<GroupMember> members = GroupFactory.getMembers(group);
        if (members == null) {
            //服务器异常
            return ResponseModel.buildServiceError();
        }
        members = members.stream().filter(groupMember -> !groupMember.getId().equalsIgnoreCase(creator.getId()))
                .collect(Collectors.toSet());
        //开始发起推送
        PushFactory.pushJoinGroup(members);
        return ResponseModel.buildOk(new GroupCard(creatorMember));

    }

    /**
     * 查找群，不传参即代表搜索最近的所有群
     *
     * @param name 搜索的名字参数
     * @return 群信息列表
     */
    @GET
    @Path("/search/{name:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> search(@PathParam("name") @DefaultValue("") String name) {
        User self = getSelf();
        List<Group> groups = GroupFactory.search(name);
        if (groups != null && groups.size() > 0) {
            List<GroupCard> groupCards = groups.stream()
                    .map(group -> {
                        GroupMember member = GroupFactory.getMember(self.getId(), group.getId());
                        return new GroupCard(group, member);
                    }).collect(Collectors.toList());
            return ResponseModel.buildOk(groupCards);
        }
        return ResponseModel.buildOk();
    }

    /**
     * 拉取自己当前的群列表
     *
     * @param dateStr 时间字段，不传则代表返回当前全部群，传入时间则返回该时间之后加入的群
     * @return 群列表
     */
    @GET
    @Path("/list/{date:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> list(@DefaultValue("") @PathParam("date") String dateStr) {
        User self = getSelf();
        //拿时间
        LocalDateTime dateTime = null;
        if (!Strings.isNullOrEmpty(dateStr)) {
            try {
                dateTime = LocalDateTime.parse(dateStr, LocalDateTimeConverter.FORMATTER);
            } catch (Exception e) {
                dateTime = null;
            }
        }
        Set<GroupMember> members = GroupFactory.getMembers(self);
        if (members == null || members.size() == 0)
            return ResponseModel.buildOk();
        final LocalDateTime localDateTime = dateTime;
        List<GroupCard> groupCards = members.stream()
                .filter(groupMember -> localDateTime == null //时间为null就不做限制
                        || groupMember.getUpdateAt().isAfter(localDateTime))//时间不为null需要在这个时间之后
                .map(GroupCard::new).collect(Collectors.toList());
        return ResponseModel.buildOk(groupCards);
    }

    /**
     * 申请加入一个群，此时会创建一个加入的申请，并写入表，然后给管理员发送消息
     * 管理员同意，即调用添加成员的接口把该成员添加进去
     *
     * @param groupId 群id
     * @return 申请的信息
     */
    @POST
    @Path("/applyJoin/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<ApplyCard> join(@PathParam("groupId") String groupId) {
        return null;
    }

    /**
     * 获取一个群的信息,你必须是群的成员
     *
     * @param groupId 群的id
     * @return 群的信息card
     */
    @GET
    @Path("/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> getGroup(@PathParam("groupId") String groupId) {
        if (Strings.isNullOrEmpty(groupId))
            return ResponseModel.buildParameterError();
        User self = getSelf();
        GroupMember member = GroupFactory.getMember(self.getId(), groupId);
        if (member == null)
            return ResponseModel.buildNotFoundGroupError(null);
        return ResponseModel.buildOk(new GroupCard(member));
    }

    /**
     * 拉取一个群的所有群成员，必须是群成员之一才行
     *
     * @param groupId 群id
     * @return 成员列表
     */
    @GET
    @Path("/{groupId}/members")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> members(@PathParam("groupId") String groupId) {
        User self = getSelf();

        // 没有这个群
        Group group = GroupFactory.findById(groupId);
        if (group == null)
            return ResponseModel.buildNotFoundGroupError(null);

        // 检查权限
        GroupMember selfMember = GroupFactory.getMember(self.getId(), groupId);
        if (selfMember == null)
            return ResponseModel.buildNoPermissionError();

        // 所有的成员,必须是群成员之一
        Set<GroupMember> members = GroupFactory.getMembers(group);
        if (members == null)
            return ResponseModel.buildServiceError();

        // 返回
        List<GroupMemberCard> memberCards = members
                .stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());

        return ResponseModel.buildOk(memberCards);
    }

    /**
     * 给群添加成员，必须为群的管理者之一才行
     *
     * @param groupId        群id
     * @param model 添加成员的model
     * @return 群的成员列表
     */
    @POST
    @Path("/{groupId}/member")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> memberAdd(@PathParam("groupId") String groupId, GroupMemberAddModel model) {
        if (Strings.isNullOrEmpty(groupId) || !GroupMemberAddModel.check(model))
            return ResponseModel.buildParameterError();

        // 拿到我的信息
        User self = getSelf();

        // 移除我之后再进行判断数量
        model.getUsers().remove(self.getId());
        if (model.getUsers().size() == 0)
            return ResponseModel.buildParameterError();

        // 没有这个群
        Group group = GroupFactory.findById(groupId);
        if (group == null)
            return ResponseModel.buildNotFoundGroupError(null);

        // 我必须是成员, 同时是管理员及其以上级别
        GroupMember selfMember = GroupFactory.getMember(self.getId(), groupId);
        if (selfMember == null || selfMember.getPermissionType() == GroupMember.PERMISSION_TYPE_NONE)
            return ResponseModel.buildNoPermissionError();


        // 已有的成员
        Set<GroupMember> oldMembers = GroupFactory.getMembers(group);
        Set<String> oldMemberUserIds = oldMembers.stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toSet());


        List<User> insertUsers = new ArrayList<>();
        for (String s : model.getUsers()) {
            // 找人
            User user = UserFactory.findById(s);
            if (user == null)
                continue;
            // 已经在群里了
            if(oldMemberUserIds.contains(user.getId()))
                continue;

            insertUsers.add(user);
        }
        // 没有一个新增的成员
        if (insertUsers.size() == 0) {
            return ResponseModel.buildParameterError();
        }

        // 进行添加操作
        Set<GroupMember> insertMembers =  GroupFactory.addMembers(group, insertUsers);
        if(insertMembers==null)
            return ResponseModel.buildServiceError();


        // 转换
        List<GroupMemberCard> insertCards = insertMembers.stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());

        // 通知，两部曲
        // 1.通知新增的成员，你被加入了XXX群
        PushFactory.pushJoinGroup(insertMembers);

        // 2.通知群中老的成员，有XXX，XXX加入群
        PushFactory.pushGroupMemberAdd(oldMembers, insertCards);

        return ResponseModel.buildOk(insertCards);
    }

    /**
     * 更改成员信息，请求的人要么是管理员，要么是成员本人
     *
     * @param memberId 成员id，可以查询对应的的群，和人
     * @param model    修改的model
     * @return 当前成员的信息
     */
    @PUT
    @Path("/member/{memberId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupMemberCard> modifyMember(@PathParam("memberId") String memberId, GroupMemberUpdateModel model) {
        return null;
    }

}
