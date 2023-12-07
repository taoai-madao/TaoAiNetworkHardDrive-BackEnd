package com.taoaipan.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * @version 1.0
 * @Author TaoAi
 * @Date 2023/12/7 10:17
 * @description 全局拦截器
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface GlobalInterceptor {

    /**
     * 校验是否登录
     * @return true/false
     */
    boolean checkLogin() default true;

    /**
     * 是否进行参数校验
     * @return true/false
     */
    boolean checkParams() default false;

    /**
     * 校验是否是管理员
     * @return true/false
     */
    boolean checkAdmin() default false;
}
