package com.shop.promotion.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PromotionGoodsRelation implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long promotionId;					//活动Id
	private Long promotionSessionId;			//活动场次Id
	private Long promotionGoodsRelationId;		//活动商品联系表Id
	private Long goodsId;						//商品Id
	private Long adminId;						//管理员Id
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;			//创建时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;			//修改时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;			//开始时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;				//结束时间

	public Long getPromotionGoodsRelationId() {
		return promotionGoodsRelationId;
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public Long getPromotionSessionId() {
		return promotionSessionId;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public Long getAdminId() {
		return adminId;
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

	public void setPromotionGoodsRelationId(Long promotionGoodsRelationId) {
		this.promotionGoodsRelationId = promotionGoodsRelationId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public void setPromotionSessionId(Long promotionSessionId) {
		this.promotionSessionId = promotionSessionId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
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

	@Override
	public String toString() {
		return "PromotionGoodsRelation [promotionGoodsRelationId=" + promotionGoodsRelationId + ", promotionId="
				+ promotionId + ", promotionSessionId=" + promotionSessionId + ", goodsId=" + goodsId + ", adminId="
				+ adminId + ", createTime=" + createTime + ", updateTime=" + updateTime + ", startTime=" + startTime
				+ ", endTime=" + endTime + "]";
	}
}
