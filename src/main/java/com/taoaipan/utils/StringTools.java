package com.taoaipan.utils;
import com.taoaipan.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class StringTools {

    /**
     * 字符转md5
     * @param originString 字符串
     * @return 转换后的值
     */
    public static String encodeByMD5(String originString) {
        return StringTools.isEmpty(originString) ? null : DigestUtils.md5Hex(originString);
    }

    /**
     * 参数校验
     * @param param 参数
     */
    public static void checkParam(Object param) {
        try {
            Field[] fields = param.getClass().getDeclaredFields();
            boolean notEmpty = false;
            for (Field field : fields) {
                String methodName = "get" + StringTools.upperCaseFirstLetter(field.getName());
                Method method = param.getClass().getMethod(methodName);
                Object object = method.invoke(param);
                if (object != null && object instanceof java.lang.String && !StringTools.isEmpty(object.toString())
                        || object != null && !(object instanceof java.lang.String)) {
                    notEmpty = true;
                    break;
                }
            }
            if (!notEmpty) {
                throw new BusinessException("多参数更新，删除，必须有非空条件");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("校验参数是否为空失败");
        }
    }

    /**
     * 首字符大写
     * @param field 字符串
     * @return 字符串
     */
    public static String upperCaseFirstLetter(String field) {
        if (isEmpty(field)) {
            return field;
        }
        //如果第二个字母是大写，第一个字母不大写
        if (field.length() > 1 && Character.isUpperCase(field.charAt(1))) {
            return field;
        }
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    /**
     * 判断是否为空
     * @param str 字符串
     * @return 布尔
     */
    public static boolean isEmpty(String str) {
        if (null == str || "".equals(str) || "null".equals(str) || "\u0000".equals(str)) {
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 生成随机字符串
     * @param count 长度
     * @return 随机字符串吧
     */
    public static final String getRandomString(Integer count) {
        return RandomStringUtils.random(count, true, true);
    }

    /**
     * 生成随机数
     * @param count 长度
     * @return 随机数
     */
    public static final String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count, false, true);
    }

    /**
     * 判断路径是否合法
     * @param path 路径
     * @return 布尔值
     */
    public static boolean pathIsOk(String path) {
        if (StringTools.isEmpty(path)) {
            return true;
        }
        if (path.contains("../") || path.contains("..\\")) {
            return false;
        }
        return true;
    }
}
