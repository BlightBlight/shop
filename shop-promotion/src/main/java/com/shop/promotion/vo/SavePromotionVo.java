package com.shop.promotion.vo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 新增活动Vo
 */
public class SavePromotionVo {
	@NotEmpty(message = "管理员不能为空")
	private long adminId = 123L;		//管理员Id
	
	@NotEmpty(message = "活动名称不能为空")
	private String promotionName;		//活动名称
	
	@NotNull(message = "活动创建时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;	//创建时间
	
	@NotNull(message = "活动开始时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;	//开始时间
	
	@NotNull(message = "活动结束时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;	//结束时间

	public long getAdminId() {
		return adminId;
	}

	public String getPromotionName() {
		return promotionName;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setAdminId(long adminId) {
		this.adminId = adminId;
	}

	public void setPromotionName(String promotionName) {
		this.promotionName = promotionName;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return "SavePromotionVo [adminId=" + adminId + ", promotionName=" + promotionName + ", createTime=" + createTime
				+ ", startTime=" + startTime + ", endTime=" + endTime + "]";
	}
}
