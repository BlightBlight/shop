package com.shop.goods.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 规格值表实体类
 */
public class SKUGoodsSpecValue implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long specValueId;			//规格值表ID
	private Long specId;				//规格表ID
	private String specValueName;		//规格值，JSON形式
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;	//创建时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;	//修改时间
	
	private Integer deleteStatus;		//0,未删除；1,已删除
	
	public Long getSpecValueId() {
		return specValueId;
	}
	public Long getSpecId() {
		return specId;
	}
	public String getSpecValueName() {
		return specValueName;
	}
	public LocalDateTime getCreateTime() {
		return createTime;
	}
	public LocalDateTime getUpdateTime() {
		return updateTime;
	}
	public Integer getDeleteStatus() {
		return deleteStatus;
	}
	public void setSpecValueId(Long specValueId) {
		this.specValueId = specValueId;
	}
	public void setSpecId(Long specId) {
		this.specId = specId;
	}
	public void setSpecValueName(String specValueName) {
		this.specValueName = specValueName;
	}
	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}
	public void setDeleteStatus(Integer deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	
	@Override
	public String toString() {
		return "GoodsSpecValue [specValueId=" + specValueId + ", specId=" + specId + ", specValueName=" + specValueName
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + ", deleteStatus=" + deleteStatus + "]";
	}
}
