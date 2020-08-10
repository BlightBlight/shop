package com.shop.customer.vo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shop.common.utils.IsMobile;

/**
 * 手机号登录页面Vo
 */
public class MobileLoginVo {
	@IsMobile
	@NotEmpty(message = "手机号码不能为空")
	private String mobileNum;			//手机号码
	
	@NotEmpty(message = "验证码不能为空")
	private String verifyCode;		//验证码
	
	@NotNull(message = "登录时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime loginTime;	//登录时间
	
	public String getMobileNum() {
		return mobileNum;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	@Override
	public String toString() {
		return "MobileLoginVo [mobileNum=" + mobileNum + ", verifyCode=" + verifyCode + "]";
	}
}
