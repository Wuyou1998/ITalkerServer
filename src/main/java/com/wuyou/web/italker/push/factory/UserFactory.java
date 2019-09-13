package com.wuyou.web.italker.push.factory;

import com.google.common.base.Strings;
import com.wuyou.web.italker.push.bean.db.User;
import com.wuyou.web.italker.push.bean.db.UserFollow;
import com.wuyou.web.italker.push.utils.Hib;
import com.wuyou.web.italker.push.utils.TextUtil;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.factory.UserFactory
 * 用户: _VIEW
 * 时间: 2019/9/7,21:16
 * 描述: 注册操作
 */
public class UserFactory {
    //根据账号（即手机号）找到user
    public static User findByPhone(String phone) {
        return Hib.query(session -> (User) session
                .createQuery("from User where phone=:inPhone")
                .setParameter("inPhone", phone)
                .uniqueResult());
    }

    //根据昵称找到user
    public static User findByName(String name) {
        return Hib.query(session -> (User) session
                .createQuery("from User where name=:inName")
                .setParameter("inName", name)
                .uniqueResult());
    }

    //根据Token找到user
    //只能自己使用，查询的信息时个人信息，非他人信息
    public static User findByToken(String Token) {
        return Hib.query(session -> (User) session
                .createQuery("from User where token=:Token")
                .setParameter("Token", Token)
                .uniqueResult());
    }

    //根据id找到user
    public static User findById(String id) {
        //通过id查询更方便
        return Hib.query(session -> (User) session
                .get(User.class, id));
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

    /**
     * 获取我的联系人列表
     *
     * @param self 我 user
     * @return 联系人列表
     */
    public static List<User> contacts(User self) {
        return Hib.query(session -> {
            //重新加载一次用户信息到self中，和当前的session绑定
            session.load(self, self.getId());
            Set<UserFollow> follows = self.getFollowing();
            //java8简写
            return follows.stream().map(UserFollow::getTarget).collect(Collectors.toList());
        });
    }

    /**
     * 关注人的操作
     *
     * @param origin 发起者
     * @param target 被关注人
     * @param alias  备注名
     * @return 被关注人的信息
     */
    public static User follow(User origin, User target, String alias) {
        UserFollow follow = getUserFollow(origin, target);
        if (follow != null) {
            //已关注直接返回
            return follow.getTarget();
        }
        return Hib.query(session -> {
            //想要操作懒加载的数据需要重新load一次
            session.load(origin, origin.getId());
            session.load(target, target.getId());

            //我关注人的时候，同时他也关注我
            UserFollow originFollow = new UserFollow();
            originFollow.setOrigin(origin);
            originFollow.setTarget(target);
            originFollow.setAlias(alias);
            //发起者是他，关注我的记录
            UserFollow targetFollow = new UserFollow();
            targetFollow.setOrigin(target);
            targetFollow.setTarget(origin);

            //保存数据库
            session.save(originFollow);
            session.save(targetFollow);

            return target;
        });
    }

    /**
     * 查询两个人是否已经关注
     *
     * @param origin 发起者
     * @param target 被关注人
     * @return 返回中间类 UserFollow
     */
    public static UserFollow getUserFollow(User origin, User target) {
        return Hib.query(session -> (UserFollow) session
                .createQuery("from UserFollow where originId=:originId and targetId=:targetId")
                .setParameter("originId", origin.getId())
                .setParameter("targetId", target.getId())
                .setMaxResults(1)
                //唯一查询
                .uniqueResult()
        );
    }

    /**
     * 搜索联系人的实现
     *
     * @param name 用于查询的那么 可以为空
     * @return 查询到的用户集合，如果name为空则返回最近的用户
     */
    @SuppressWarnings("unchecked")
    public static List<User> search(String name) {
        if (Strings.isNullOrEmpty(name))
            name = "";
        String searchName = "%" + name + "%";
        return Hib.query(session -> {
            //name忽略大小写，采用（模糊）like查询
            //只有名称、头像、描述 三者均完善才能被查询的到
            return (List<User>) session.createQuery("from User where lower(name) like :name and avatar is not null and description is not null")
                    .setParameter("name", searchName)
                    .setMaxResults(20)
                    .list();
        });
    }
}
