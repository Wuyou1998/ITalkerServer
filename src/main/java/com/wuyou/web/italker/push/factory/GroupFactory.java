package com.wuyou.web.italker.push.factory;

import com.wuyou.web.italker.push.bean.db.Group;
import com.wuyou.web.italker.push.bean.db.GroupMember;
import com.wuyou.web.italker.push.bean.db.User;

import java.util.Set;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.factory.GroupFactory
 * 用户: _VIEW
 * 时间: 2019/9/20,16:06
 * 描述: 群数据库处理类
 */
public class GroupFactory {
    public static Group findById(String groupId) {
        //TODO 查询一个群
        return null;
    }

    public static Set<GroupMember> getMembers(Group group) {
        //TODO 查询一个群的成员
        return null;
    }

    public static Group findById(User sender, String receiverId) {
        //TODO 查询一个群,同时该User必须为群的成员，否则返回null
        return null;
    }
}
