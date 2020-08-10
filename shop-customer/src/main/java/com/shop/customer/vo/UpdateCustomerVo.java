package com.shop.customer.vo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 修改用户页面Vo
 */
public class UpdateCustomerVo {
	@NotNull(message = "用户Id不能为空")
	private Long customerId;					//用户Id
	
	@NotEmpty(message = "用户昵称不能为空")
	private String nickName;					//用户昵称
	
	@NotEmpty(message = "用户手机不能为空")
	private String mobileNum;					//用户手机
	
	@NotNull(message = "修改时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;			//修改时间

	public Long getCustomerId() {
		return customerId;
	}

	public String getNickName() {
		return nickName;
	}

	public String getMobileNum() {
		return mobileNum;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "UpdateCustomerVo [customerId=" + customerId + ", nickName=" + nickName + ", mobileNum=" + mobileNum
				+ ", updateTime=" + updateTime + "]";
	}
}
