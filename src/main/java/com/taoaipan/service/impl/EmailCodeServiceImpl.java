package com.taoaipan.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

import com.taoaipan.component.RedisComponent;
import com.taoaipan.entity.config.AppConfig;
import com.taoaipan.entity.constants.Constants;
import com.taoaipan.entity.dto.SysSettingsDto;
import com.taoaipan.entity.po.UserInfo;
import com.taoaipan.entity.query.UserInfoQuery;
import com.taoaipan.exception.BusinessException;
import com.taoaipan.mappers.UserInfoMapper;
import com.taoaipan.service.UserInfoService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.taoaipan.entity.enums.PageSize;
import com.taoaipan.entity.query.EmailCodeQuery;
import com.taoaipan.entity.po.EmailCode;
import com.taoaipan.entity.vo.PaginationResultVO;
import com.taoaipan.entity.query.SimplePage;
import com.taoaipan.mappers.EmailCodeMapper;
import com.taoaipan.service.EmailCodeService;
import com.taoaipan.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * 邮箱验证码 业务接口实现
 */
@Service("emailCodeService")
public class EmailCodeServiceImpl implements EmailCodeService {

	private static final Logger logger = LoggerFactory.getLogger(EmailCodeServiceImpl.class);

	@Resource
	private EmailCodeMapper<EmailCode, EmailCodeQuery> emailCodeMapper;

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	@Resource
	private JavaMailSender javaMailSender;

	@Resource
	private AppConfig appConfig;

	@Resource
	private RedisComponent redisComponent;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<EmailCode> findListByParam(EmailCodeQuery param) {
		return this.emailCodeMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(EmailCodeQuery param) {
		return this.emailCodeMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<EmailCode> findListByPage(EmailCodeQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<EmailCode> list = this.findListByParam(param);
		PaginationResultVO<EmailCode> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(EmailCode bean) {
		return this.emailCodeMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<EmailCode> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.emailCodeMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<EmailCode> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.emailCodeMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(EmailCode bean, EmailCodeQuery param) {
		StringTools.checkParam(param);
		return this.emailCodeMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(EmailCodeQuery param) {
		StringTools.checkParam(param);
		return this.emailCodeMapper.deleteByParam(param);
	}

	/**
	 * 根据EmailAndCode获取对象
	 */
	@Override
	public EmailCode getEmailCodeByEmailAndCode(String email, String code) {
		return this.emailCodeMapper.selectByEmailAndCode(email, code);
	}

	/**
	 * 根据EmailAndCode修改
	 */
	@Override
	public Integer updateEmailCodeByEmailAndCode(EmailCode bean, String email, String code) {
		return this.emailCodeMapper.updateByEmailAndCode(bean, email, code);
	}

	/**
	 * 根据EmailAndCode删除
	 */
	@Override
	public Integer deleteEmailCodeByEmailAndCode(String email, String code) {
		return this.emailCodeMapper.deleteByEmailAndCode(email, code);
	}

	/**
	 * 邮箱验证码发送
	 * @param toEmail 邮箱
	 * @param type 邮箱验证码用途标记
	 */
    @Override
	@Transactional(rollbackFor = Exception.class)
    public void sendEmailCode(String toEmail, Integer type) {
		// 根据type判断验证码用途, 注册的话判断邮箱在数据库是否已经存在
		if (type == Constants.ZERO) {
			UserInfo userInfo = userInfoMapper.selectByEmail(toEmail);
			if (userInfo != null) {
				throw new BusinessException("邮箱已经存在");
			}
		}
		// 设置发送验证码的长度
		String code = StringTools.getRandomString(Constants.LENGTH_5);
		// 调用函数发送验证码
		sendEmailCode(toEmail, code);
		// 修改该账号之前验证码的状态
		emailCodeMapper.disableEmailCode(toEmail);
		// 创建邮件验证码对象
		EmailCode emailCode = new EmailCode();
		emailCode.setCode(code);
		emailCode.setEmail(toEmail);
		emailCode.setStatus(Constants.ZERO);
		emailCode.setCreateTime(new Date());
		// 将验证码存入数据库
		emailCodeMapper.insert(emailCode);
    }

	/**
	 * 邮件发送函数
	 * @param toEmail 发往的邮箱
	 * @param code 邮箱验证码
	 */
	private void sendEmailCode(String toEmail, String code){
		try {
			MimeMessage message = javaMailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			//邮件发件人
			helper.setFrom(appConfig.getSendUserName());
			//邮件收件人 1或多个
			helper.setTo(toEmail);

			SysSettingsDto sysSettingsDto = redisComponent.getSysSettingsDto();

			//邮件主题
			helper.setSubject(sysSettingsDto.getRegisterEmailTitle());
			//邮件内容
			helper.setText(String.format(sysSettingsDto.getRegisterEmailContent(), code));
			//邮件发送时间
			helper.setSentDate(new Date());
			javaMailSender.send(message);
		} catch (Exception e) {
			logger.error("邮件发送失败", e);
			throw new BusinessException("邮件发送失败");
		}
	}
}