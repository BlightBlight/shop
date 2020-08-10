package com.shop.goods.vo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 修改商品品牌Vo
 */
public class UpdateBrandVo {
	@NotNull(message = "分类ID不能为空")
	private Long categoryId;			//分类Id
	
	@NotNull(message = "品牌ID不能为空")
	private Long brandId;				//品牌Id
	
	@NotEmpty(message = "品牌名称不能为空")
	private String brandName;		//品牌名称
	
	@NotNull(message = "修改时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;	//修改时间

	public Long getCategoryId() {
		return categoryId;
	}

	public Long getBrandId() {
		return brandId;
	}

	public String getBrandName() {
		return brandName;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "UpdateBrandVo [categoryId=" + categoryId + ", brandId=" + brandId + ", brandName=" + brandName
				+ ", updateTime=" + updateTime + "]";
	}
}
