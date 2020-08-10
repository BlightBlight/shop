package com.shop.customer.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 用户信息实体类
 */
public class CustomerInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long customerId;				//用户Id
	private String customermobileNum;		//用户手机号
	private String customerPwd;				//密码
	private String customerSalt;			//MD5盐值
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime registerTime;		//注册时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime loginTime;		//登录时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;		//修改时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime deleteTime;		//删除时间

	public Long getCustomerId() {
		return customerId;
	}

	public String getCustomermobileNum() {
		return customermobileNum;
	}

	public String getCustomerPwd() {
		return customerPwd;
	}

	public String getCustomerSalt() {
		return customerSalt;
	}

	public LocalDateTime getRegisterTime() {
		return registerTime;
	}

	public LocalDateTime getLoginTime() {
		return loginTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public LocalDateTime getDeleteTime() {
		return deleteTime;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public void setCustomermobileNum(String customermobileNum) {
		this.customermobileNum = customermobileNum;
	}

	public void setCustomerPwd(String customerPwd) {
		this.customerPwd = customerPwd;
	}

	public void setCustomerSalt(String customerSalt) {
		this.customerSalt = customerSalt;
	}

	public void setRegisterTime(LocalDateTime registerTime) {
		this.registerTime = registerTime;
	}

	public void setLoginTime(LocalDateTime loginTime) {
		this.loginTime = loginTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	public void setDeleteTime(LocalDateTime deleteTime) {
		this.deleteTime = deleteTime;
	}

	@Override
	public String toString() {
		return "CustomerInfo [customerId=" + customerId + ", customermobileNum=" + customermobileNum + ", customerPwd="
				+ customerPwd + ", customerSalt=" + customerSalt + ", registerTime=" + registerTime + ", loginTime="
				+ loginTime + ", updateTime=" + updateTime + ", deleteTime=" + deleteTime + "]";
	}
}
