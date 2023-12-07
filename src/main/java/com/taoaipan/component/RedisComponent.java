package com.taoaipan.component;

import com.taoaipan.entity.constants.Constants;
import com.taoaipan.entity.dto.SysSettingsDto;
import com.taoaipan.entity.po.FileInfo;
import com.taoaipan.entity.po.UserInfo;
import com.taoaipan.entity.query.FileInfoQuery;
import com.taoaipan.entity.query.UserInfoQuery;
import com.taoaipan.mappers.FileInfoMapper;
import com.taoaipan.mappers.UserInfoMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @version 1.0
 * @Author TaoAi
 * @Date 2023/12/7 11:21
 * @description redis组件类
 */
@Component("redisComponent")
public class RedisComponent {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    /**
     * 获取系统设置
     *
     * @return
     */
    public SysSettingsDto getSysSettingsDto() {
        SysSettingsDto sysSettingsDto = (SysSettingsDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        if (sysSettingsDto == null) {
            sysSettingsDto = new SysSettingsDto();
            redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
        }
        return sysSettingsDto;
    }

    /**
     * 保存设置
     *
     * @param sysSettingsDto
     */
    public void saveSysSettingsDto(SysSettingsDto sysSettingsDto) {
        redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
    }
}
