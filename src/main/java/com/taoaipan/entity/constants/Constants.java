package com.taoaipan.entity.constants;

/**
 * @version 1.0
 * @Author TaoAi
 * @Date 2023/12/5 16:48
 * @description 常量类
 */
public class Constants {
    /**
     * 验证码在 HttpSession 中存储的属性名
     */
    public static final String CHECK_CODE_KEY = "check_code_key";

    /**
     * 邮箱验证码在 HTTPSession 中存储的属性名
     */
    public static final String CHECK_CODE_KEY_EMAIL = "check_code_key_email";

    /**
     * 出现的数字常量
     */
    public static final Integer ZERO = 0;
    public static final Integer LENGTH_5 = 5;
    public static final Integer LENGTH_10 = 10;
    public static final Integer LENGTH_15 = 15;

    /**
     * Redis 缓存设置
     */
    public static final String REDIS_KEY_DOWNLOAD = "taoaipan:download:";
    public static final String REDIS_KEY_SYS_SETTING = "taoaipan:syssetting:";
    public static final String REDIS_KEY_USER_SPACE_USE = "taoaipan:user:spaceuse:";
    public static final String REDIS_KEY_USER_FILE_TEMP_SIZE = "taoaipan:user:file:temp:";

    /**
     * Redis Key
     */
    /**
     * 过期时间 1分钟
     */
    public static final Integer REDIS_KEY_EXPIRES_ONE_MIN = 60;
    /**
     * 过期时间 1天
     */
    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_KEY_EXPIRES_ONE_MIN * 60 * 24;
    public static final Integer REDIS_KEY_EXPIRES_ONE_HOUR = REDIS_KEY_EXPIRES_ONE_MIN * 60;
    public static final Long MB = 1024 * 1024L;
    /**
     * 过期时间5分钟
     */
    public static final Integer REDIS_KEY_EXPIRES_FIVE_MIN = REDIS_KEY_EXPIRES_ONE_MIN * 5;
}
