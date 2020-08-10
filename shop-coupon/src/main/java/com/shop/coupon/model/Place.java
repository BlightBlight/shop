package com.shop.coupon.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 渠道实体类
 */
public class Place implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long placeId;	//渠道Id
	private String placeName;	//渠道名称
	private LocalDateTime createTime;	//创建时间
	private LocalDateTime updateTIme;	//修改时间
	
	public long getPlaceId() {
		return placeId;
	}
	public String getPlaceName() {
		return placeName;
	}
	public LocalDateTime getCreateTime() {
		return createTime;
	}
	public LocalDateTime getUpdateTIme() {
		return updateTIme;
	}
	public void setPlaceId(long placeId) {
		this.placeId = placeId;
	}
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}
	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
	public void setUpdateTIme(LocalDateTime updateTIme) {
		this.updateTIme = updateTIme;
	}
	
	@Override
	public String toString() {
		return "Place [placeId=" + placeId + ", placeName=" + placeName + ", createTime=" + createTime + ", updateTIme="
				+ updateTIme + "]";
	}
}
