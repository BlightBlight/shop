package com.shop.goods.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 商品分类实体类
 */
public class Category implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long categoryId;				//分类ID
	private String categoryName;			//分类名称
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;		//创建时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;		//修改时间
	
	private Integer deleteStatus;			//1,未删除；2,已删除
	
	public Long getCategoryId() {
		return categoryId;
	}
	public String getCategoryName() {
		return categoryName;
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
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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
		return "Category [categoryId=" + categoryId + ", categoryName=" + categoryName + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + ", deleteStatus=" + deleteStatus + "]";
	}
}
