package com.taoaipan.component;

import com.taoaipan.entity.constants.Constants;
import com.taoaipan.entity.dto.SysSettingsDto;
import com.taoaipan.entity.dto.UserSpaceDto;
import com.taoaipan.entity.po.FileInfo;
import com.taoaipan.entity.po.UserInfo;
import com.taoaipan.entity.query.FileInfoQuery;
import com.taoaipan.entity.query.UserInfoQuery;
import com.taoaipan.mappers.FileInfoMapper;
import com.taoaipan.mappers.UserInfoMapper;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 保存用户已使用空间
     * @param userId 用户id
     * @param userSpaceDto 用户空间对象
     */
    public void saveUserSpaceUse(String userId, UserSpaceDto userSpaceDto) {
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, userSpaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
    }

    /**
     * 获取用户空间
     * @param userId
     * @return
     */
    public UserSpaceDto getUserSpaceUse(String userId) {
        UserSpaceDto spaceDto = (UserSpaceDto) redisUtils.get(Constants.REDIS_KEY_USER_SPACE_USE + userId);
        if (spaceDto == null) {
            spaceDto = new UserSpaceDto();
            Long useSpace = this.fileInfoMapper.selectUseSpace(userId);
            spaceDto.setUseSpace(useSpace);
            spaceDto.setTotalSpace(getSysSettingsDto().getUserInitUseSpace() * Constants.MB);
            // 用户路径+用户id 用户总空间，有效期
            redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, spaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
        }
        return spaceDto;
    }
}
