package com.wuyou.web.italker.push.factory;

import com.wuyou.web.italker.push.bean.db.User;
import com.wuyou.web.italker.push.utils.Hib;
import com.wuyou.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.factory.UserFactory
 * 用户: _VIEW
 * 时间: 2019/9/7,21:16
 * 描述: 注册操作
 */
public class UserFactory {
    //根据账号（即手机号）查用户是否存在
    public static User findByPhone(String phone) {
        return Hib.query(session -> (User) session.createQuery("from User where phone=:inPhone").setParameter("inPhone", phone).uniqueResult());
    }

    //查询昵称是否已经有人用了
    public static User findByName(String name) {
        return Hib.query(session -> (User) session.createQuery("from User where name=:inName").setParameter("inName", name).uniqueResult());
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
        User user = new User();
        user.setName(name);
        //手机号当账号
        user.setPhone(account);
        user.setPassword(password);
        //创建会话
        Session session = Hib.session();
        //开始事务
        session.beginTransaction();
        try {
            //保存操作
            session.save(user);
            //提交
            session.getTransaction().commit();
            return user;
        } catch (Exception e) {
            //操作失败，回滚
            session.getTransaction().rollback();
            System.out.println("something wrong,rollback");
            return null;
        }

    }

    private static String encodePassword(String password) {
        //去除空格
        password = password.trim();
        //MD5加密，加盐会更安全，但盐也要存储
        password = TextUtil.getMD5(password);
        //再来一次对称Base64加密，也可以采取加盐的方式
        return TextUtil.encodeBase64(password);
    }
}
