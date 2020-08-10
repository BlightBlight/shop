package com.shop.goods.vo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 修改商品分类Vo
 */
public class UpdateCategoryVo {
	@NotNull(message = "分类ID不能为空")
	private Long categoryId;			//分类Id
	
	@NotEmpty(message = "分类名称不能为空")
	private String categoryName;		//分类名称
	
	@NotNull(message = "修改时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;	//修改时间

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "UpdateCategoryVo [categoryId=" + categoryId + ", categoryName=" + categoryName + ", updateTime="
				+ updateTime + "]";
	}
}
