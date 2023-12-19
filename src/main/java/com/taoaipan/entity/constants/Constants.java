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
     * Redis date
     */
    public static final Integer REDIS_KEY_EXPIRES_ONE_MIN = 60;
    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_KEY_EXPIRES_ONE_MIN * 60 * 24;
    public static final Integer REDIS_KEY_EXPIRES_ONE_HOUR = REDIS_KEY_EXPIRES_ONE_MIN * 60;
    public static final Long MB = 1024 * 1024L;
    public static final Integer REDIS_KEY_EXPIRES_FIVE_MIN = REDIS_KEY_EXPIRES_ONE_MIN * 5;

    /**
     * Session 相关
     */
    public static final String SESSION_KEY = "session_key";

    /**
     * 文件路径
     */
    public static final String FILE_FOLDER_FILE = "/file/";
    public static final String FILE_FOLDER_TEMP = "/temp/";
    /**
     * 头像文件文件夹
     */
    public static final String FILE_FOLDER_AVATAR_NAME = "avatar/";
    /**
     * 头像文件后缀, 默认头像
     */
    public static final String AVATAR_SUFFIX = ".jpg";
    public static final String AVATAR_DEFUALT = "default_avatar.jpg";
}
