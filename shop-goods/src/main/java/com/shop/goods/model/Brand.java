package com.shop.goods.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 商品品牌实体类
 */
public class Brand implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long categoryId;				//分类ID
	private Long brandId;					//品牌ID
	private String brandName;				//品牌名称
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;		//创建时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;		//修改时间
	
	private Integer deleteStatus;			//1,未删除；2,已删除
	
	public Long getBrandId() {
		return brandId;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public String getBrandName() {
		return brandName;
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
	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
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
		return "Brand [brandId=" + brandId + ", categoryId=" + categoryId + ", brandName=" + brandName + ", createTime="
				+ createTime + ", updateTime=" + updateTime + ", deleteStatus=" + deleteStatus + "]";
	}
}
