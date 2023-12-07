package com.taoaipan.annotation;

import com.taoaipan.entity.enums.VerifyRegexEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0
 * @Author TaoAi
 * @Date 2023/12/7 10:30
 * @description 参数或字段校验拦截器
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyParam {
    /**
     * 使用正则表达式进行校验
     * @return 校验类型
     */
    VerifyRegexEnum regex() default VerifyRegexEnum.NO;

    /**
     * 最小长度
     * @return 最小长度
     */
    int min() default -1;

    /**
     * 最大长度
     * @return 最大长度
     */
    int max() default -1;

    /**
     * 是否为必须参数
     * @return 默认为不是
     */
    boolean required() default false;
}
