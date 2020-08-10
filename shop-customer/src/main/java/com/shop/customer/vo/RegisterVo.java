package com.shop.customer.vo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shop.common.utils.IsMobile;

/**
 * 注册页面Vo
 */
public class RegisterVo {
	@NotEmpty(message = "手机号不能为空")
	@IsMobile(message = "手机号格式不正确")
	private String mobileNum;				//用户电话
	
	@NotEmpty(message = "用户昵称不能为空")
	private String nickName;				//用户昵称
	
	@NotEmpty(message = "密码不能为空")
	private String pwd;						//密码
	
	@NotEmpty(message = "盐值不能为空")
	private String salt;					//MD5盐值
	
	@NotNull(message = "注册时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime registerTime;				//注册时间

	public String getMobileNum() {
		return mobileNum;
	}

	public String getNickName() {
		return nickName;
	}

	public String getPwd() {
		return pwd;
	}

	public String getSalt() {
		return salt;
	}

	public LocalDateTime getRegisterTime() {
		return registerTime;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public void setRegisterTime(LocalDateTime registerTime) {
		this.registerTime = registerTime;
	}

	@Override
	public String toString() {
		return "RegisterVo [mobileNum=" + mobileNum + ", nickName=" + nickName + ", pwd=" + pwd + ", salt=" + salt
				+ ", registerTime=" + registerTime + "]";
	}
}
