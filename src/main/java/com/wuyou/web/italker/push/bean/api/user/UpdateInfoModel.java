package com.wuyou.web.italker.push.bean.api.user;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;
import com.wuyou.web.italker.push.bean.db.User;

/**
 * 用户更新信息，完善信息的Model
 */
public class UpdateInfoModel {
    @Expose
    private String name;
    @Expose
    private String avatar;
    @Expose
    private String description;
    @Expose
    private int sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    /**
     * 把当前的信息，填充到用户Model中
     * 方便UserModel进行写入
     *
     * @param user User Model
     * @return User Model
     */
    public User updateToUser(User user) {
        if (!Strings.isNullOrEmpty(name))
            user.setName(name);

        if (!Strings.isNullOrEmpty(avatar))
            user.setAvatar(avatar);

        if (!Strings.isNullOrEmpty(description))
            user.setDescription(description);

        if (sex != 0)
            user.setSex(sex);

        return user;
    }

    public static boolean check(UpdateInfoModel model) {
        // Model 不允许为null，
        // 并且只需要具有一个及其以上的参数即可
        return model != null
                && (!Strings.isNullOrEmpty(model.name) ||
                !Strings.isNullOrEmpty(model.avatar) ||
                !Strings.isNullOrEmpty(model.description) ||
                model.sex != 0);
    }

}
