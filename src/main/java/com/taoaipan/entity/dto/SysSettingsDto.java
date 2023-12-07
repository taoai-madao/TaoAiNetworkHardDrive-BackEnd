package com.taoaipan.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author TaoAi
 * @Date 2023/12/7 11:15
 * @description 邮件DTO对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysSettingsDto implements Serializable {
    /**
     * 注册发送邮件标题
     */
    private String registerEmailTitle = "邮箱验证码";

    /**
     * 注册发送邮件内容
     */
    private String registerEmailContent = "您好！感谢您使用本产品，您的账号正在进行邮箱验证，验证码为：%s，有效期：15分钟，此为系统邮件，请勿回复 —— Please do not reply to this system email";

    /**
     * 用户初始化空间大小 5M
     */
    private Integer userInitUseSpace = 5;

    public String getRegisterEmailTitle() {
        return registerEmailTitle;
    }

    public void setRegisterEmailTitle(String registerEmailTitle) {
        this.registerEmailTitle = registerEmailTitle;
    }

    public String getRegisterEmailContent() {
        return registerEmailContent;
    }

    public void setRegisterEmailContent(String registerEmailContent) {
        this.registerEmailContent = registerEmailContent;
    }

    public Integer getUserInitUseSpace() {
        return userInitUseSpace;
    }

    public void setUserInitUseSpace(Integer userInitUseSpace) {
        this.userInitUseSpace = userInitUseSpace;
    }
}