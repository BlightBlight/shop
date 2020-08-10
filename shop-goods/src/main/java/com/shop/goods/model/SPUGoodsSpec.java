package com.shop.goods.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 规格表实体类
 */
public class SPUGoodsSpec implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long specId;				//规格表ID
	private String specName;			//规格名称
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;	//创建时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;	//修改时间
	
	private Integer deleteStatus;		//1,未删除；2,已删除
	
	public Long getSpecId() {
		return specId;
	}
	public String getSpecName() {
		return specName;
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
	public void setSpecId(Long specId) {
		this.specId = specId;
	}
	public void setSpecName(String specName) {
		this.specName = specName;
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
		return "GoodsSpec [specId=" + specId + ", specName=" + specName + ", createTime=" + createTime + ", updateTime="
				+ updateTime + ", deleteStatus=" + deleteStatus + "]";
	}
}
