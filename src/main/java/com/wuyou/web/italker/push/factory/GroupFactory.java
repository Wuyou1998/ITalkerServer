package com.wuyou.web.italker.push.factory;

import com.google.common.base.Strings;
import com.wuyou.web.italker.push.bean.api.group.GroupCreateModel;
import com.wuyou.web.italker.push.bean.db.Group;
import com.wuyou.web.italker.push.bean.db.GroupMember;
import com.wuyou.web.italker.push.bean.db.User;
import com.wuyou.web.italker.push.utils.Hib;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.factory.GroupFactory
 * 用户: _VIEW
 * 时间: 2019/9/20,16:06
 * 描述: 群数据库处理类
 */
public class GroupFactory {
    //通过id拿群model
    public static Group findById(String groupId) {
        return Hib.query(session -> session.get(Group.class, groupId));
    }

    //查询一个群,同时该User必须为群的成员，否则返回null
    public static Group findById(User user, String receiverId) {
        GroupMember member = getMember(user.getId(), receiverId);
        if (member != null) {
            return member.getGroup();
        }
        return null;
    }

    //通过名字查找
    public static Group findByName(String name) {
        return Hib.query(session -> (Group) session.createQuery("from Group where LOWER(name)=:name ")
                .setParameter("name", name.toLowerCase())
                .uniqueResult());
    }

    //查询一个群的所有成员
    public static Set<GroupMember> getMembers(Group group) {

        return Hib.query(session -> {
            @SuppressWarnings("unchecked") List<GroupMember> members = session.createQuery("from GroupMember where group=:group")
                    .setParameter("group", group)
                    .list();
            return new HashSet<>(members);
        });
    }

    //查询一个人加入的所有群
    public static Set<GroupMember> getMembers(User user) {

        return Hib.query(session -> {
            @SuppressWarnings("unchecked") List<GroupMember> members = session.createQuery("from GroupMember where user=:user")
                    .setParameter("user", user)
                    .list();
            return new HashSet<>(members);
        });
    }

    //创建群
    public static Group create(User creator, GroupCreateModel model, List<User> userList) {
        return Hib.query(session -> {
            Group group = new Group(creator, model);
            session.save(group);
            GroupMember ownerMember = new GroupMember(creator, group);
            //设置创建者超级管理员权限
            ownerMember.setPermissionType(GroupMember.PERMISSION_TYPE_ADMIN_SU);
            //保存，还没有提交到数据库
            session.save(ownerMember);
            for (User user : userList) {
                GroupMember member = new GroupMember(user, group);
                //保存，还没有提交到数据库
                session.save(member);
            }
//            session.flush();
//            session.load(group, group.getId());
            return group;
        });
    }

    //获取一个群的成员
    public static GroupMember getMember(String userId, String groupId) {
        return Hib.query(session -> (GroupMember) session.createQuery("from GroupMember where userId=:userId and groupId=:groupId")
                .setParameter("userId", userId)
                .setParameter("groupId", groupId)
                .setMaxResults(1)
                .uniqueResult());
    }

    //查询
    @SuppressWarnings("unchecked")
    public static List<Group> search(String name) {
        if (Strings.isNullOrEmpty(name))
            name = "";
        String searchName = "%" + name + "%";
        return Hib.query(session -> {
            //name忽略大小写，采用（模糊）like查询
            //只有名称、头像、描述 三者均完善才能被查询的到
            return (List<Group>) session.createQuery("from Group where lower(name) like :name")
                    .setParameter("name", searchName)
                    .setMaxResults(20)
                    .list();
        });
    }

    //给群添加用户
    public static Set<GroupMember> addMembers(Group group, List<User> insertUsers) {
        return Hib.query(session -> {

            Set<GroupMember> members = new HashSet<>();

            for (User user : insertUsers) {
                GroupMember member = new GroupMember(user, group);
                // 保存，并没有提交到数据库
                session.save(member);
                members.add(member);
            }
            return members;
        });
    }
}
