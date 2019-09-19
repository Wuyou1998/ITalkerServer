package com.wuyou.web.italker.push.utils;

import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IIGtPush;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import com.google.common.base.Strings;
import com.wuyou.web.italker.push.bean.api.base.PushModel;
import com.wuyou.web.italker.push.bean.db.User;
import com.wuyou.web.italker.push.constant.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 名称: ITalkerServer.com.wuyou.web.italker.push.utils.PushDispatcher
 * 用户: _VIEW
 * 时间: 2019/9/19,16:54
 * 描述: 消息推送工具类
 */
public class PushDispatcher {
    private static final String appId = Constant.GE_TUI_APP_ID;
    private static final String appKey = Constant.GE_TUI_APP_KEY;
    private static final String masterSecret = Constant.GE_TUI_MASTER_SECRET;

    private static final String host = "http://sdk.open.api.igexin.com/apiex.htm";

    private final IIGtPush pusher;
    //要收到消息的人和内容的列表
    private List<BatchBean> beans = new ArrayList<>();

    public PushDispatcher() {
        //最根本的发送者
        pusher = new IGtPush(host, appKey, masterSecret);
    }

    /**
     * 添加一条消息
     *
     * @param receiver 接收者
     * @param model    接收的推送model
     * @return true 添加成功
     */
    public boolean add(User receiver, PushModel model) {
        //基础的检查，接收者不为空且其设备id存在，要发送的model也不为null
        if (receiver == null || Strings.isNullOrEmpty(receiver.getPushId()) || model == null) {
            return false;
        }
        String pushString = model.getPushString();
        if (Strings.isNullOrEmpty(pushString)) {
            return false;
        }
        //构建一个目标加内容
        BatchBean batchBean = buildBean(receiver.getPushId(), pushString);
        beans.add(batchBean);
        return true;
    }

    /**
     * 对要发送的数据进行格式化封装
     *
     * @param clientId 接收者的设备id
     * @param text     要接收的数据
     * @return BatchBean
     */
    private BatchBean buildBean(String clientId, String text) {
        SingleMessage message = new SingleMessage();
        //透传消息，不是通知栏显示，而是在MessageReceiver中收到
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(text);
        template.setTransmissionType(2); // 透传消息接受方式设置，1：立即启动APP，2：客户端收到消息后需要自行处理
        //把透传消息设置到单消息模版中
        message.setData(template);
        //是否允许离线发送
        message.setOffline(true);
        //离线时长 1 天
        message.setOfflineExpireTime(24 * 3600 * 1000);

        // 设置推送目标，填入appid和clientId
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(clientId);

        //返回一个封装
        return new BatchBean(message, target);
    }

    /**
     * 进行消息的最终发送
     */
    public boolean submit() {
        //打包的工具类
        IBatch batch = pusher.getBatch();
        //是否有数据需要发送
        boolean haveData = false;
        for (BatchBean bean : beans) {
            try {
                batch.add(bean.message, bean.target);
                haveData = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //没有数据就直接返回
        if (!haveData) {
            return false;
        }
        IPushResult result = null;
        try {
            result = batch.submit();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                //失败情况下尝试重复发送一次
                batch.retry();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (result != null) {
            try {
                Logger.getLogger("PushDispatcher")
                        .log(Level.INFO, (String) result.getResponse().get("result"));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Logger.getLogger("PushDispatcher")
                .log(Level.WARNING, "推送服务器响应异常");
        return false;
    }

    /**
     * 给每个人发送消息的bean封装
     */
    public static class BatchBean {
        SingleMessage message;
        Target target;

        public BatchBean(SingleMessage message, Target target) {
            this.message = message;
            this.target = target;
        }
    }


    //通知栏消息
    private static void constructClientLinkMsg(String cid, String msg, IBatch batch) throws Exception {

        SingleMessage message = new SingleMessage();
        LinkTemplate template = new LinkTemplate();
        // 设置APPID与APPKEY
        template.setAppId(appId);
        template.setAppkey(appKey);

        Style0 style = new Style0();
        // 设置通知栏标题与内容
        style.setTitle("请输入通知栏标题");
        style.setText("请输入通知栏内容");
        // 配置通知栏图标
        style.setLogo("icon.png");
        // 配置通知栏网络图标
        style.setLogoUrl("");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);

        message.setData(template);
        message.setOffline(true);
        message.setOfflineExpireTime(24 * 3600 * 1000);

        // 设置推送目标，填入appid和clientId
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(cid);
        batch.add(message, target);
    }
}
