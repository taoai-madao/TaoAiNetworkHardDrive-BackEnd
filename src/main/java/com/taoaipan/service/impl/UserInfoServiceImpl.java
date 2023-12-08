package com.taoaipan.service.impl;

import com.taoaipan.component.RedisComponent;
import com.taoaipan.entity.config.AppConfig;
import com.taoaipan.entity.constants.Constants;
import com.taoaipan.entity.dto.SessionWebUserDto;
import com.taoaipan.entity.dto.SysSettingsDto;
import com.taoaipan.entity.dto.UserSpaceDto;
import com.taoaipan.entity.enums.PageSize;
import com.taoaipan.entity.enums.UserStatusEnum;
import com.taoaipan.entity.po.UserInfo;
import com.taoaipan.entity.query.SimplePage;
import com.taoaipan.entity.query.UserInfoQuery;
import com.taoaipan.entity.vo.PaginationResultVO;
import com.taoaipan.exception.BusinessException;
import com.taoaipan.mappers.UserInfoMapper;
import com.taoaipan.service.EmailCodeService;
import com.taoaipan.service.FileInfoService;
import com.taoaipan.service.UserInfoService;
import com.taoaipan.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * 用户信息 业务接口实现
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
	@Resource
	private EmailCodeService emailCodeService;
	@Resource
	private RedisComponent redisComponent;
	@Resource
	private AppConfig appConfig;
	@Resource
	private FileInfoService fileInfoService;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserInfo> findListByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(param);
		PaginationResultVO<UserInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserInfo bean, UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据UserId获取对象
	 */
	@Override
	public UserInfo getUserInfoByUserId(String userId) {
		return this.userInfoMapper.selectByUserId(userId);
	}

	/**
	 * 根据UserId修改
	 */
	@Override
	public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
		return this.userInfoMapper.updateByUserId(bean, userId);
	}

	/**
	 * 根据UserId删除
	 */
	@Override
	public Integer deleteUserInfoByUserId(String userId) {
		return this.userInfoMapper.deleteByUserId(userId);
	}

	/**
	 * 根据Email获取对象
	 */
	@Override
	public UserInfo getUserInfoByEmail(String email) {
		return this.userInfoMapper.selectByEmail(email);
	}

	/**
	 * 根据Email修改
	 */
	@Override
	public Integer updateUserInfoByEmail(UserInfo bean, String email) {
		return this.userInfoMapper.updateByEmail(bean, email);
	}

	/**
	 * 根据Email删除
	 */
	@Override
	public Integer deleteUserInfoByEmail(String email) {
		return this.userInfoMapper.deleteByEmail(email);
	}

	/**
	 * 根据NickName获取对象
	 */
	@Override
	public UserInfo getUserInfoByNickName(String nickName) {
		return this.userInfoMapper.selectByNickName(nickName);
	}

	/**
	 * 根据NickName修改
	 */
	@Override
	public Integer updateUserInfoByNickName(UserInfo bean, String nickName) {
		return this.userInfoMapper.updateByNickName(bean, nickName);
	}

	/**
	 * 根据NickName删除
	 */
	@Override
	public Integer deleteUserInfoByNickName(String nickName) {
		return this.userInfoMapper.deleteByNickName(nickName);
	}

	/**
	 * 根据QqOpenId获取对象
	 */
	@Override
	public UserInfo getUserInfoByQqOpenId(String qqOpenId) {
		return this.userInfoMapper.selectByQqOpenId(qqOpenId);
	}

	/**
	 * 根据QqOpenId修改
	 */
	@Override
	public Integer updateUserInfoByQqOpenId(UserInfo bean, String qqOpenId) {
		return this.userInfoMapper.updateByQqOpenId(bean, qqOpenId);
	}

	/**
	 * 根据QqOpenId删除
	 */
	@Override
	public Integer deleteUserInfoByQqOpenId(String qqOpenId) {
		return this.userInfoMapper.deleteByQqOpenId(qqOpenId);
	}

	/**
	 * 用户注册
	 * @param email 邮箱
	 * @param nickName 昵称
	 * @param password 密码
	 * @param emailCode 邮箱验证码
	 */
    @Override
	@Transactional(rollbackFor = Exception.class)
    public void register(String email, String nickName, String password, String emailCode) {
		UserInfo userInfo = userInfoMapper.selectByEmail(email);
		if (userInfo != null) {
			throw new BusinessException("邮箱账号已存在");
		}
		UserInfo userNickName = userInfoMapper.selectByNickName(nickName);
		if (userNickName != null){
			throw new BusinessException("用户昵称已存在");
		}
		// 校验邮箱验证码
		emailCodeService.checkCode(email, emailCode);
		// 校验信息没有问题后创建用户对象并存储它
		// 使用随机数方法生成用户id，并对这个id从数据库判断是否重复
		String userId = userIdCreate();
		userInfo = new UserInfo();
		userInfo.setUserId(userId);
		userInfo.setNickName(nickName);
		userInfo.setEmail(email);
		userInfo.setPassword(StringTools.encodeByMD5(password));
		userInfo.setJoinTime(new Date());
		userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
		SysSettingsDto sysSettingsDto = redisComponent.getSysSettingsDto();
		userInfo.setTotalSpace(sysSettingsDto.getUserInitUseSpace() * Constants.MB);
		userInfo.setUseSpace(0L);
		userInfoMapper.insert(userInfo);
    }

	/**
	 * 用户登录
	 * @param email 邮箱
	 * @param password 密码
	 * @return 脱敏后的用户对象
	 */
	@Override
	public SessionWebUserDto login(String email, String password) {
		UserInfo userInfo = userInfoMapper.selectByEmail(email);
		if (userInfo == null || !userInfo.getPassword().equals(password)){
			throw new BusinessException("账号或密码错误");
		}
		if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())){
			throw new BusinessException("该账号已禁用");
		}
		// 更新用户最后一次登录时间
		UserInfo updateUserInfo = new UserInfo();
		updateUserInfo.setLastLoginTime(new Date());
		userInfoMapper.updateByUserId(updateUserInfo, userInfo.getUserId());
		// 用户信息脱敏返回前端
		SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
		sessionWebUserDto.setNickName(userInfo.getNickName());
		sessionWebUserDto.setUserId(userInfo.getUserId());
		// 判断用户是否是管理员, 从管理员列表判断
		if (ArrayUtils.contains(appConfig.getAdminEmails().split(","), email)){
			sessionWebUserDto.setAdmin(true);
		} else {
			sessionWebUserDto.setAdmin(false);
		}
		// 用户空间设置
		UserSpaceDto userSpaceDto = new UserSpaceDto();
		// 用户使用空间总和
		userSpaceDto.setUseSpace(fileInfoService.getUserUseSpace(userInfo.getUserId()));
		// 用户总空间
		userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
		// 将用户空间情况存入Redis数据库
		redisComponent.saveUserSpaceUse(userInfo.getUserId(), userSpaceDto);
		return sessionWebUserDto;
	}

	/**
	 * 生成用户不重复id
	 * @return 用户id
	 */
	public String userIdCreate() {
		// TODO 可以使用 Stream 进行优化这段逻辑
		String id = StringTools.getRandomNumber(Constants.LENGTH_10);
		while (!checkVariableDuplicate(id)) {
			id = StringTools.getRandomString(Constants.LENGTH_10);
		}
		return id;
	}

	/**
	 * 判断用户 id 是否重复
	 * @return true / false
	 */
	boolean checkVariableDuplicate(String id){
		return userInfoMapper.selectByUserId(id) == null;
	}
}