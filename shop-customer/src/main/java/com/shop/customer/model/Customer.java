package com.shop.customer.model;

import java.io.Serializable;

/**
 * 用户实体类
 */
public class Customer implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long customerId;				//用户Id
	private String customermobileNum;		//用户手机号
	private String customernickName;		//用户昵称
	
	private Integer customerStatus;			//1,正常；2,冻结
	private Integer deleteStatus;			//1,未删除；2,伪删除；3，已删除
	
	public Long getCustomerId() {
		return customerId;
	}
	public String getCustomermobileNum() {
		return customermobileNum;
	}
	public String getCustomernickName() {
		return customernickName;
	}
	public Integer getCustomerStatus() {
		return customerStatus;
	}
	public Integer getDeleteStatus() {
		return deleteStatus;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public void setCustomermobileNum(String customermobileNum) {
		this.customermobileNum = customermobileNum;
	}
	public void setCustomernickName(String customernickName) {
		this.customernickName = customernickName;
	}
	public void setCustomerStatus(Integer customerStatus) {
		this.customerStatus = customerStatus;
	}
	public void setDeleteStatus(Integer deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	
	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", customermobileNum=" + customermobileNum + ", customernickName="
				+ customernickName + ", customerStatus=" + customerStatus + ", deleteStatus=" + deleteStatus + "]";
	}
}
