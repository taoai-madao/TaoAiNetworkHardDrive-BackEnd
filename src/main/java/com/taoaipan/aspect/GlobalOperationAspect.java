package com.taoaipan.aspect;

import com.taoaipan.annotation.GlobalInterceptor;
import com.taoaipan.annotation.VerifyParam;
import com.taoaipan.entity.config.AppConfig;
import com.taoaipan.entity.constants.Constants;
import com.taoaipan.entity.dto.SessionWebUserDto;
import com.taoaipan.entity.enums.ResponseCodeEnum;
import com.taoaipan.entity.po.UserInfo;
import com.taoaipan.entity.query.UserInfoQuery;
import com.taoaipan.exception.BusinessException;
import com.taoaipan.service.UserInfoService;
import com.taoaipan.utils.StringTools;
import com.taoaipan.utils.VerifyUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @version 1.0
 * @Author TaoAi
 * @Date 2023/12/8 14:30
 * @description 切面类
 */
@Aspect
@Component("globalOperationAspect")
public class GlobalOperationAspect {
    private static Logger logger = LoggerFactory.getLogger(GlobalOperationAspect.class);
    private static final String TYPE_STRING = "java.lang.String";
    private static final String TYPE_INTEGER = "java.lang.Integer";
    private static final String TYPE_LONG = "java.lang.Long";

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private AppConfig appConfig;

    /**
     * 切点匹配
     */
    @Pointcut("@annotation(com.taoaipan.annotation.GlobalInterceptor)")
    private void requestInterceptor(){
    }

    /**
     * 切点匹配前置通知
     * @param point 切点
     * @throws BusinessException 异常
     */
    @Before("requestInterceptor()")
    public void interceptorDo(JoinPoint point) throws BusinessException {
        try{
            // 获取目标对象
            Object target = point.getTarget();
            // 获取方法参数
            Object[] arguments = point.getArgs();
            // 获取方法名
            String methodName = point.getSignature().getName();
            // 获取方法参数类型
            Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
            // 获取目标方法
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            // 获取方法上的 @GlobalInterceptor
            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
            if (interceptor == null) {
                // 为空则返回不校验
                return;
            }
            // 校验登录
            if (interceptor.checkLogin() || interceptor.checkAdmin()){
                checkLogin(interceptor.checkAdmin());
            }
            // 校验参数
            if (interceptor.checkParams()){
                validateParams(method, arguments);
            }
        } catch (BusinessException e) {
            logger.error("全局拦截器异常", e);
            throw e;
        } catch (Exception e) {
            logger.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        } catch (Throwable e) {
            logger.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
    }

    /**
     * 从浏览器Session中判断用户是否登录，以及判断是否需要管理员权限
     * @param checkAdmin
     */
    private void checkLogin(Boolean checkAdmin){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        SessionWebUserDto sessionUser = (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
        if (sessionUser == null && appConfig.getDev() != null && appConfig.getDev()) {
            List<UserInfo> userInfoList = userInfoService.findListByParam(new UserInfoQuery());
            if (!userInfoList.isEmpty()) {
                UserInfo userInfo = userInfoList.get(0);
                sessionUser = new SessionWebUserDto();
                sessionUser.setUserId(userInfo.getUserId());
                sessionUser.setNickName(userInfo.getNickName());
                sessionUser.setAdmin(true);
                session.setAttribute(Constants.SESSION_KEY, sessionUser);
            }
        }
        if (null == sessionUser) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }

        if (checkAdmin && !sessionUser.getAdmin()) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
    }

    /**
     * 参数校验
     * @param m 方法名
     * @param arguments 参数列表
     */
    private void validateParams(Method m, Object[] arguments){
        Parameter[] parameters = m.getParameters();
        // 根据方法的参数数据与各个参数对应的数值来判断参数上是否有自定义的@VerifyParam注解
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object value = arguments[i];
            VerifyParam verifyParam = parameter.getAnnotation(VerifyParam.class);
            if (verifyParam == null) {
                continue;
            }
            //基本数据类型
            if (TYPE_STRING.equals(parameter.getParameterizedType().getTypeName()) || TYPE_LONG.equals(parameter.getParameterizedType().getTypeName()) || TYPE_INTEGER.equals(parameter.getParameterizedType().getTypeName())) {
                // 数值类型
                checkValue(value, verifyParam);
            } else {
                //如果传递的是对象
                checkObjValue(parameter, value);
            }
        }
    }

    /**
     * 数值类型参数校验
     * @param value 值
     * @param verifyParam 需要校验的类型
     * @throws BusinessException 抛出自定义异常
     */
    private void checkValue(Object value, VerifyParam verifyParam) throws BusinessException {
        Boolean isEmpty = value == null || StringTools.isEmpty(value.toString());
        Integer length = value == null ? 0 : value.toString().length();

        // 校验空
        if (isEmpty && verifyParam.required()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 校验长度
        if (!isEmpty && (verifyParam.max() != -1 && verifyParam.max() < length || verifyParam.min() != -1 && verifyParam.min() > length)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 校验正则
        if (!isEmpty && !StringTools.isEmpty(verifyParam.regex().getRegex()) && !VerifyUtils.verify(verifyParam.regex(), String.valueOf(value))) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

    /**
     * 对象类型参数校验
     * @param parameter 健
     * @param value 值
     */
    private void checkObjValue(Parameter parameter, Object value) {
        try {
            String typeName = parameter.getParameterizedType().getTypeName();
            Class classz = Class.forName(typeName);
            Field[] fields = classz.getDeclaredFields();
            for (Field field : fields) {
                VerifyParam fieldVerifyParam = field.getAnnotation(VerifyParam.class);
                if (fieldVerifyParam == null) {
                    continue;
                }
                field.setAccessible(true);
                Object resultValue = field.get(value);
                checkValue(resultValue, fieldVerifyParam);
            }
        } catch (BusinessException e) {
            logger.error("校验参数失败", e);
            throw e;
        } catch (Exception e) {
            logger.error("校验参数失败", e);
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }
}
