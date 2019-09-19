package com.wuyou.web.italker.push.bean.card;

import com.google.gson.annotations.Expose;
import com.wuyou.web.italker.push.bean.db.Group;
import com.wuyou.web.italker.push.bean.db.GroupMember;


import java.time.LocalDateTime;

/**
 * 群信息Model
 *
 * @author qiujuer Email:qiujuer.live.cn
 */
public class GroupCard {
    @Expose
    private String id;// Id
    @Expose
    private String name;// 名称
    @Expose
    private String description;// 描述
    @Expose
    private String picture;// 群图片
    @Expose
    private String ownerId;// 创建者Id
    @Expose
    private int notifyLevel;// 对于当前用户的通知级别
    @Expose
    private LocalDateTime joinAt;// 加入时间
    @Expose
    private LocalDateTime modifyAt;// 最后修改时间

    public GroupCard(GroupMember member) {
        final Group group = member.getGroup();
        this.id = group.getId();
        this.name = group.getName();
        this.description = group.getDescription();
        this.picture = group.getPicture();
        this.ownerId = group.getOwner().getId();
        this.notifyLevel = member.getNotifyLevel();
        this.joinAt = member.getCreateAt();
        this.modifyAt = group.getUpdateAt();
    }

    public GroupCard(Group group, GroupMember member) {
        this.id = group.getId();
        this.name = group.getName();
        this.description = group.getDescription();
        this.picture = group.getPicture();
        this.ownerId = group.getOwner().getId();
        this.notifyLevel = member != null ? member.getNotifyLevel() : 0;
        this.joinAt = member != null ? member.getCreateAt() : null;
        this.modifyAt = group.getUpdateAt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getNotifyLevel() {
        return notifyLevel;
    }

    public void setNotifyLevel(int notifyLevel) {
        this.notifyLevel = notifyLevel;
    }

    public LocalDateTime getJoinAt() {
        return joinAt;
    }

    public void setJoinAt(LocalDateTime joinAt) {
        this.joinAt = joinAt;
    }

    public LocalDateTime getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(LocalDateTime modifyAt) {
        this.modifyAt = modifyAt;
    }
}
