package com.taoaipan.controller;

import com.taoaipan.annotation.GlobalInterceptor;
import com.taoaipan.annotation.VerifyParam;
import com.taoaipan.component.RedisComponent;
import com.taoaipan.entity.config.AppConfig;
import com.taoaipan.entity.constants.Constants;
import com.taoaipan.entity.dto.CreateImageCode;
import com.taoaipan.entity.dto.SessionWebUserDto;
import com.taoaipan.entity.enums.VerifyRegexEnum;
import com.taoaipan.entity.vo.ResponseVO;
import com.taoaipan.exception.BusinessException;
import com.taoaipan.service.EmailCodeService;
import com.taoaipan.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @version 1.0
 * @Author TaoAi
 * @Date 2023/12/5 14:26
 * @description 账户接口
 */
@RestController("accountController")
public class AccountController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";

    @Resource
    private EmailCodeService emailCodeService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private AppConfig appConfig;
    @Resource
    private RedisComponent redisComponent;

    /**
     * 图片验证码生成
     * @param response 响应头
     * @param session session
     * @param type 类型
     */
    @RequestMapping( "/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session, Integer type) throws IOException {
        CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);
        // 写两遍为了确保兼容性（Http 1.0 与 1.1）
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        String code = vCode.getCode();
        if (type == null || type == 0) {
            session.setAttribute(Constants.CHECK_CODE_KEY, code);
        } else {
            session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, code);
        }
        vCode.write(response.getOutputStream());
    }

    /**
     * 邮箱验证码发送
     * @param session session
     * @param email 邮箱
     * @param checkCode 图片验证码
     * @param type 验证码用途标记
     * @return 封装VO对象
     */
    @RequestMapping("/sendEmailCode")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO sendEmailCode(HttpSession session,
                                    @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                                    @VerifyParam(required = true) String checkCode,
                                    @VerifyParam(required = true) Integer type) {
        try{
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))){
                throw new BusinessException("图片验证码不正确");
            }
            emailCodeService.sendEmailCode(email, type);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY_EMAIL);
        }
    }

    /**
     * 用户注册
     * @param session session
     * @param email 邮箱
     * @param nickName 昵称
     * @param password 密码
     * @param checkCode 验证啊
     * @param emailCode 邮箱验证码
     * @return VO对象
     */
    @RequestMapping("/register")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO register(HttpSession session,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                               @VerifyParam(required = true, max = 20) String nickName,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 198) String password,
                               @VerifyParam(required = true) String checkCode,
                               @VerifyParam(required = true) String emailCode){
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))){
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.register(email, nickName, password, emailCode);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * 用户登录
     * @param session session对象
     * @param email 邮箱
     * @param password 密码
     * @param checkCode 验证码
     * @return VO对象
     */
    @RequestMapping("/login")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO login(HttpSession session,
                            @VerifyParam(required = true) String email,
                            @VerifyParam(required = true) String password,
                            @VerifyParam(required = true) String checkCode){
        try{
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))){
                throw new BusinessException("图片验证码不正确");
            }
            SessionWebUserDto sessionWebUserDto =  userInfoService.login(email, password);
            // 将用户脱敏DTO对象存入 Session
            session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
            return getSuccessResponseVO(sessionWebUserDto);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * 密码重置
     * @param session session
     * @param email email
     * @param password pwd
     * @param checkCode 图片验证码
     * @param emailCode 邮箱验证码
     * @return 响应结果
     */
    @RequestMapping("/resetPwd")
    @GlobalInterceptor(checkLogin = true, checkParams = true)
    public ResponseVO resetPwd(HttpSession session,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
                               @VerifyParam(required = true) String checkCode,
                               @VerifyParam(required = true) String emailCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))){
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.resetPwd(email, password, emailCode);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * 获取用户头像
     * @param response Http响应对象
     * @param userId 用户id
     */
    @RequestMapping("/getAvatar/{userId}")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public void getAvatar(HttpServletResponse response,
                          @VerifyParam(required = true) @PathVariable("userId") String userId){
        String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR_NAME;
        File folder = new File(appConfig.getProjectFolder() + avatarFolderName);
        if (!folder.exists()){
            folder.mkdirs();
        }
        String avatarPath = appConfig.getProjectFolder() + avatarFolderName + userId + Constants.AVATAR_SUFFIX;
        File file = new File(avatarPath);
        if (!file.exists()) {
            if (!new File(appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFUALT).exists()) {
                printNoDefaultImage(response);
                return;
            }
            avatarPath = appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFUALT;
        }
        response.setContentType("image/jpg");
        readFile(response, avatarPath);
    }

    /**
     * 输出默认头像
     * @param response 响应对象
     */
    private void printNoDefaultImage(HttpServletResponse response) {
        response.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        response.setStatus(HttpStatus.OK.value());
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.print("请在头像目录下放置默认头像default_avatar.jpg");
            writer.close();
        } catch (Exception e) {
            logger.error("输出无默认图失败", e);
        } finally {
            writer.close();
        }
    }

    /**
     * 获取用户信息
     * @param session Session
     * @return 用户信息DTO对象
     */
    @RequestMapping("/getUserInfo")
    @GlobalInterceptor
    public ResponseVO getUserInfo(HttpSession session) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        return getSuccessResponseVO(sessionWebUserDto);
    }

    /**
     * 获取用户空间
     * @param session session
     * @return 用户信息
     */
    @RequestMapping("/getUseSpace")
    @GlobalInterceptor
    public ResponseVO getUseSpace(HttpSession session) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        return getSuccessResponseVO(redisComponent.getUserSpaceUse(sessionWebUserDto.getUserId()));
    }

    /**
     * 用户退出
     * @param session Session
     * @return 响应对象
     */
    @RequestMapping("/logout")
    public ResponseVO logout(HttpSession session) {
        session.invalidate();
        return getSuccessResponseVO(null);
    }
}
