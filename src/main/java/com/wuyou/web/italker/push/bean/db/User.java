package com.wuyou.web.italker.push.bean.db;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.bean.db.User
 * 用户: _VIEW
 * 时间: 2019/9/6,21:19
 * 描述: 用户Model 对应数据库
 */
@Entity
@Table(name = "TB_USER")
public class User {
    //主键
    @Id
    @PrimaryKeyJoinColumn
    //主键生成存储的类型为UUID
    @GeneratedValue(generator = "uuid")
    //把uuid的生成器定义为uuid2，uuid2是常规的UUID toString
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(updatable = false, nullable = false)
    private String id;

    //用户名必须唯一
    @Column(nullable = false, length = 128, unique = true)
    private String name;

    //电话必须唯一
    @Column(nullable = false, length = 62, unique = true)
    private String phone;

    //密码不允许为空
    @Column(nullable = false)
    private String password;

    //头像允许为空
    @Column
    private String avatar;

    //个人描述
    @Column
    private String description;

    //性别有初始值，所以不能为空
    @Column(nullable = false)
    private int sex = 0;

    //token 可以拉取用户信息，所以token必须唯一
    @Column(unique = true)
    private String token;

    //用于推送的设备Id
    @Column
    private String pushId;

    //定义为创建时间戳，在创建时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //定义为更新时间戳，在创建时就已经写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //最后一次收到消息的时间
    @Column
    private LocalDateTime lastReceiveAt = LocalDateTime.now();

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getLastReceiveAt() {
        return lastReceiveAt;
    }

    public void setLastReceiveAt(LocalDateTime lastReceiveAt) {
        this.lastReceiveAt = lastReceiveAt;
    }
}
