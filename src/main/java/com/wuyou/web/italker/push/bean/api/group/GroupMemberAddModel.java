package com.wuyou.web.italker.push.bean.api.group;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.bean.api.group.GroupMemberAddModel
 * 用户: _VIEW
 * 时间: 2019/9/25,14:12
 * 描述: 群聊添加成员的model
 */
public class GroupMemberAddModel {
    @Expose
    private Set<String> users = new HashSet<>();

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    public static boolean check(GroupMemberAddModel model) {
        return !(model.users == null
                || model.users.size() == 0);
    }
}
