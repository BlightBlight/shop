package com.shop.promotion.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Promotion implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long promotionId;			//活动Id
	private Long adminId;				//管理员Id
	private String promotionName;		//活动名称
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;	//创建时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;	//修改时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;	//开始时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;		//结束时间
	
	private Integer promotionStatus;	//活动状态

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public Long getAdminId() {
		return adminId;
	}

	public String getPromotionName() {
		return promotionName;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public Integer getPromotionStatus() {
		return promotionStatus;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public void setPromotionName(String promotionName) {
		this.promotionName = promotionName;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public void setPromotionStatus(Integer promotionStatus) {
		this.promotionStatus = promotionStatus;
	}

	@Override
	public String toString() {
		return "Promotion [promotionId=" + promotionId + ", adminId=" + adminId + ", promotionName=" + promotionName
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", promotionStatus=" + promotionStatus + "]";
	}
}
