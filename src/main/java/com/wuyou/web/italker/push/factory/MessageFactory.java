package com.wuyou.web.italker.push.factory;

import com.wuyou.web.italker.push.bean.db.Group;
import com.wuyou.web.italker.push.bean.db.Message;
import com.wuyou.web.italker.push.bean.db.User;
import com.wuyou.web.italker.push.message.MessageCreateModel;
import com.wuyou.web.italker.push.utils.Hib;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.factory.MessageFactory
 * 用户: _VIEW
 * 时间: 2019/9/20,14:46
 * 描述: 消息数据存储的类
 */
public class MessageFactory {
    //查询某一个消息
    public static Message findById(String id) {
        return Hib.query(session -> session.get(Message.class, id));
    }

    // 添加一条普通消息
    public static Message add(User sender, User receiver, MessageCreateModel model) {
        Message message = new Message(sender, receiver, model);
        return save(message);
    }

    // 添加一条群消息
    public static Message add(User sender, Group group, MessageCreateModel model) {
        Message message = new Message(sender, group, model);
        return save(message);
    }

    private static Message save(Message message) {
        return Hib.query(session -> {
            session.save(message);

            // 写入到数据库
            session.flush();

            // 紧接着从数据库中查询出来
            session.refresh(message);
            return message;
        });
    }
}
