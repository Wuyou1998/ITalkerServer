package com.wuyou.web.italker.push.bean.card;

import com.google.gson.annotations.Expose;
import com.wuyou.web.italker.push.bean.db.Apply;


import java.time.LocalDateTime;

/**
 * 申请请求的Card, 用于推送一个申请请求
 *
 * @author qiujuer Email:qiujuer.live.cn
 */
public class ApplyCard {
    // 申请Id
    @Expose
    private String id;
    // 附件
    @Expose
    private String attach;
    // 描述
    @Expose
    private String desc;
    // 目标的类型
    @Expose
    private int type;
    // 目标（群／人...的ID）
    @Expose
    private String targetId;
    // 申请人的Id
    @Expose
    private String applicantId;
    // 创建时间
    @Expose
    private LocalDateTime createAt;

    public ApplyCard(Apply apply) {
        this.id = apply.getId();
        this.attach = apply.getAttach();
        this.desc = apply.getDescription();
        this.type = apply.getType();
        this.targetId = apply.getTargetId();
        this.applicantId = apply.getApplicantId();
        this.createAt = apply.getCreateAt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
