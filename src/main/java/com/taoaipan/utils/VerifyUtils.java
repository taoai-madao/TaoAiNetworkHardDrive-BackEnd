package com.taoaipan.utils;

import com.taoaipan.entity.enums.VerifyRegexEnum;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version 1.0
 * @Author TaoAi
 * @Date 2023/12/9 20:20
 * @description 校验工具类
 */
public class VerifyUtils {
    public static boolean verify(String regex, String value) {
        if (StringTools.isEmpty(value)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean verify(VerifyRegexEnum regex, String value) {
        return verify(regex.getRegex(), value);
    }

    public static void main(String[] args) {
        System.out.println(new File("E:\\代码生成\\..\\workspace-java").exists());

    }
}
