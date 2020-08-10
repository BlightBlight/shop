package com.shop.goods.vo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 新增商品品牌Vo
 */
public class SaveBrandVo {
	@NotNull(message = "分类不能为空")
	private Long categoryId;		//分类Id
	
	@NotEmpty(message = "品牌名称不能为空")
	private String brandName;		//品牌名称
	
	@NotNull(message = "创建时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;	//创建时间

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "SaveBrandVo [categoryId=" + categoryId + ", brandName=" + brandName + ", createTime=" + createTime
				+ "]";
	}
}
