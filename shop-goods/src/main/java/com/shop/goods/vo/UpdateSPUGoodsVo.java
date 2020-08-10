package com.shop.goods.vo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 修改SPU商品Vo
 */
public class UpdateSPUGoodsVo {
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
	
	@NotNull(message = "商品Id不能为空")
	private Long spuId;				//商品Id
	
	@NotEmpty(message = "商品名称不能为空")
	private String spuName;			//商品名称
	
	@NotNull(message = "规格Id不能为空")
	private Long specId;			//规格Id
	
	@NotEmpty(message = "规格不能为空")
	private String specName;		//规格
	
	@NotNull(message = "修改时间不能为空")	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;	//修改时间

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

	public Long getSpuId() {
		return spuId;
	}

	public String getSpuName() {
		return spuName;
	}

	public Long getSpecId() {
		return specId;
	}

	public String getSpecName() {
		return specName;
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

	public void setPreId(Long preId) {
		this.preId = preId;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSpuId(Long spuId) {
		this.spuId = spuId;
	}

	public void setSpuName(String spuName) {
		this.spuName = spuName;
	}

	public void setSpecId(Long specId) {
		this.specId = specId;
	}

	public void setSpecName(String specName) {
		this.specName = specName;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "UpdateSPUGoodsVo [categoryId=" + categoryId + ", brandId=" + brandId + ", preId=" + preId + ", level="
				+ level + ", type=" + type + ", spuId=" + spuId + ", spuName=" + spuName + ", specId=" + specId
				+ ", specName=" + specName + ", updateTime=" + updateTime + "]";
	}

	
}
