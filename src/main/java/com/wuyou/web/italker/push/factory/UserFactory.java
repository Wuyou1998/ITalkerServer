package com.wuyou.web.italker.push.factory;

import com.google.common.base.Strings;
import com.wuyou.web.italker.push.bean.db.User;
import com.wuyou.web.italker.push.utils.Hib;
import com.wuyou.web.italker.push.utils.TextUtil;

import java.util.List;
import java.util.UUID;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.factory.UserFactory
 * 用户: _VIEW
 * 时间: 2019/9/7,21:16
 * 描述: 注册操作
 */
public class UserFactory {
    //根据账号（即手机号）找到user
    public static User findByPhone(String phone) {
        return Hib.query(session -> (User) session.createQuery("from User where phone=:inPhone").setParameter("inPhone", phone).uniqueResult());
    }

    //根据昵称找到user
    public static User findByName(String name) {
        return Hib.query(session -> (User) session.createQuery("from User where name=:inName").setParameter("inName", name).uniqueResult());
    }

    //根据Token找到user
    //只能自己使用，查询的信息时个人信息，非他人信息
    public static User findByToken(String Token) {
        return Hib.query(session -> (User) session.createQuery("from User where token=:Token").setParameter("Token", Token).uniqueResult());
    }

    /**
     * 更新用户信息到数据库
     *
     * @param user User
     * @return User
     */
    public static User update(User user) {
        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
    }

    /**
     * 使用账户和密码进行登录
     */
    public static User login(String account, String password) {
        final String accountStr = account.trim();
        //把原文进行同样的加密处理，才能匹配的上
        final String passwordStr = encodePassword(password.trim());
        //查询
        User user = Hib.query(session ->
                (User) session.createQuery("from User where phone=:phone and password=:password")
                        .setParameter("phone", accountStr)
                        .setParameter("password", passwordStr)
                        .uniqueResult()
        );

        if (user != null) {
            user = login(user);
        }
        return user;

    }

    /**
     * 用户注册
     * 注册操作需要写入数据库，并返回数据库中的User信息
     *
     * @param account  账户
     * @param password 密码
     * @param name     昵称
     * @return User
     */
    public static User register(String account, String password, String name) {
        account = account.trim();//去除空格
        password = encodePassword(password);//密码MD5加密
        User user = createUser(account, password, name);
        if (user != null) {
            user = login(user);
        }
        return user;
    }

    /**
     * 注册部分新建用户逻辑
     *
     * @param account  手机号
     * @param password 加密后的密码
     * @param name     网名
     * @return User
     */
    private static User createUser(String account, String password, String name) {
        User user = new User();
        user.setName(name);
        //手机号当账号
        user.setPhone(account);
        user.setPassword(password);
        //数据库存储
        return Hib.query(session -> {
            session.save(user);
            return user;
        });
    }

    /**
     * 把一个User进行登录操作
     * 本质上是对Toke进行操作
     *
     * @param user User
     * @return User
     */
    private static User login(User user) {
        //使用一个随机的UUID值充当Token
        String newToken = UUID.randomUUID().toString();
        //进行一次base64格式化
        newToken = TextUtil.encodeBase64(newToken);
        user.setToken(newToken);
        return update(user);
    }

    /**
     * 给当前账户绑定pushId
     *
     * @param user   user
     * @param pushId id
     * @return user
     */
    public static User bindPushId(User user, String pushId) {
        if (Strings.isNullOrEmpty(pushId))
            return null;
        //查询是否有其他账号绑定了这个id
        //取消绑定，避免推送混乱
        //查询的列表不能包括自己
        Hib.queryOnly(session -> {
            String userId = user.getId();
            @SuppressWarnings("unchecked")
            List<User> userList = session
                    .createQuery("from User where lower(pushId) =:pushId and id!=:userId")
                    .setParameter("pushId", pushId)
                    .setParameter("userId", userId)
                    .list();
            for (User u : userList) {
                //更新为null
                u.setPushId(null);
                session.saveOrUpdate(u);
            }
        });
        //如果之前已经绑定过了，不需要额外绑定
        if (pushId.equalsIgnoreCase(user.getPushId()))
            return user;
        else {
            /*
             * 如果当前账户之前的设备id和现在需要绑定的id不同
             * 那么需要单点登录，让之前的设备退出账户，并推送一条退出消息
             */
            if (Strings.isNullOrEmpty(pushId)) {
                //TODO 推送一个退出消息
            }
            //更新新的设备id
            user.setPushId(pushId);
            return update(user);
        }

    }

    /**
     * 对密码进行加密操作
     *
     * @param password 原文
     * @return 密文
     */
    private static String encodePassword(String password) {
        //去除空格
        password = password.trim();
        //MD5加密，加盐会更安全，但盐也要存储
        password = TextUtil.getMD5(password);
        //再来一次对称Base64加密，也可以采取加盐的方式
        return TextUtil.encodeBase64(password);
    }
}
