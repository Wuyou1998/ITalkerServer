package com.wuyou.web.italker.push.bean.api.group;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.bean.api.group.GroupCreateModel
 * 用户: _VIEW
 * 时间: 2019/9/25,13:52
 * 描述: 群创建的Model
 */
public class GroupCreateModel {
    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private String picture;
    @Expose
    private Set<String> users = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    public static boolean check(GroupCreateModel model) {
        return !(Strings.isNullOrEmpty(model.name)
                || Strings.isNullOrEmpty(model.description)
                || Strings.isNullOrEmpty(model.picture)
                || model.users == null
                || model.users.size() == 0);
    }
}
