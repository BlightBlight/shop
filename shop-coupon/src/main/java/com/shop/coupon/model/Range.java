package com.shop.coupon.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 范围实体类
 */
public class Range implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long rangeId;	//范围Id（Id须有意义，由相应范围类型的Id组成）
	private String rangeName;	//范围名称（可为Category、Brand、Shop、邮费及其四种任意组合）
	private LocalDateTime createTime;	//创建时间
	private LocalDateTime updateTIme;	//修改时间
	
	public long getRangeId() {
		return rangeId;
	}
	public String getRangeName() {
		return rangeName;
	}
	public LocalDateTime getCreateTime() {
		return createTime;
	}
	public LocalDateTime getUpdateTIme() {
		return updateTIme;
	}
	public void setRangeId(long rangeId) {
		this.rangeId = rangeId;
	}
	public void setRangeName(String rangeName) {
		this.rangeName = rangeName;
	}
	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
	public void setUpdateTIme(LocalDateTime updateTIme) {
		this.updateTIme = updateTIme;
	}
	
	@Override
	public String toString() {
		return "Range [rangeId=" + rangeId + ", rangeName=" + rangeName + ", createTime=" + createTime + ", updateTIme="
				+ updateTIme + "]";
	}
}
