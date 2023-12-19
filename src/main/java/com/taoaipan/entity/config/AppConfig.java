package com.taoaipan.entity.config;

import com.taoaipan.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Author TaoAi
 * @Date 2023/12/7 11:13
 * @description 邮件配置,获取配置文件中的各种参数
 */
@Component("appConfig")
public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    /**
     * 文件目录
     */
    @Value("${project.folder:}")
    private String projectFolder;

    /**
     * 发送人
     */
    @Value("${spring.mail.username:}")
    private String sendUserName;


    @Value("${admin.emails:}")
    private String adminEmails;

    public String getAdminEmails() {
        return adminEmails;
    }

    @Value("${dev:false}")
    private Boolean dev;


    @Value("${qq.app.id:}")
    private String qqAppId;

    @Value("${qq.app.key:}")
    private String qqAppKey;


    @Value("${qq.url.authorization:}")
    private String qqUrlAuthorization;


    @Value("${qq.url.access.token:}")
    private String qqUrlAccessToken;


    @Value("${qq.url.openid:}")
    private String qqUrlOpenId;

    @Value("${qq.url.user.info:}")
    private String qqUrlUserInfo;

    @Value("${qq.url.redirect:}")
    private String qqUrlRedirect;


    public String getProjectFolder() {
        if (!StringTools.isEmpty(projectFolder) && !projectFolder.endsWith("/")) {
            projectFolder = projectFolder + "/";
        }
        return projectFolder;
    }

    public static Logger getLogger() {
        return logger;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public Boolean getDev() {
        return dev;
    }

    public String getQqAppId() {
        return qqAppId;
    }

    public String getQqAppKey() {
        return qqAppKey;
    }

    public String getQqUrlAuthorization() {
        return qqUrlAuthorization;
    }

    public String getQqUrlAccessToken() {
        return qqUrlAccessToken;
    }

    public String getQqUrlOpenId() {
        return qqUrlOpenId;
    }

    public String getQqUrlUserInfo() {
        return qqUrlUserInfo;
    }

    public String getQqUrlRedirect() {
        return qqUrlRedirect;
    }
}
