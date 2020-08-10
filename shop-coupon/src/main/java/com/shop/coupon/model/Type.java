package com.shop.coupon.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 作用类型实体类
 */
public class Type implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long typeId;	//作用类型Id
	private String typeName;	//作用类型名称（目前只有满减、折扣、传递减）
	private LocalDateTime createTime;	//创建时间
	private LocalDateTime updateTime;	//修改时间
	
	public long getTypeId() {
		return typeId;
	}
	public String getTypeName() {
		return typeName;
	}
	public LocalDateTime getCreateTime() {
		return createTime;
	}
	public LocalDateTime getUpdateTime() {
		return updateTime;
	}
	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}
	
	@Override
	public String toString() {
		return "Type [typeId=" + typeId + ", typeName=" + typeName + ", createTime=" + createTime + ", updateTime="
				+ updateTime + "]";
	}
}
