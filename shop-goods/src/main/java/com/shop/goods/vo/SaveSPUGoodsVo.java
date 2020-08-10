package com.shop.goods.vo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 新增SPU商品Vo
 */
public class SaveSPUGoodsVo {
	@NotNull(message = "分类不能为空")
	private Long categoryId;		//分类ID
	
	@NotNull(message = "品牌不能为空")
	private Long brandId;			//品牌ID
	
	@NotNull(message = "上级分类ID不能为空")
	private Long preId;				//上级分类ID
	
	@NotNull(message = "分类级别不能为空")
	private Integer level;				//分类级别
	
	@NotEmpty(message = "型号不能为空")
	private String type;			//型号
	
	@NotEmpty(message = "商品名称不能为空")
	private String spuName;			//商品名称
	
	@NotEmpty(message = "规格不能为空")
	private String specName;		//规格
	
	@NotNull(message = "创建时间不能为空")	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;	//创建时间

	public Long getCategoryId() {
		return categoryId;
	}

	public Long getBrandId() {
		return brandId;
	}

	public Long getPreId() {
		return preId;
	}

	public Integer getLevel() {
		return level;
	}

	public String getType() {
		return type;
	}

	public String getSpuName() {
		return spuName;
	}

	public String getSpecName() {
		return specName;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public void setPreId(Long preId) {
		this.preId = preId;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSpuName(String spuName) {
		this.spuName = spuName;
	}

	public void setSpecName(String specName) {
		this.specName = specName;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "SaveSPUGoodsVo [categoryId=" + categoryId + ", brandId=" + brandId + ", preId=" + preId + ", level="
				+ level + ", type=" + type + ", spuName=" + spuName + ", specName=" + specName + ", createTime="
				+ createTime + "]";
	}

	
}
