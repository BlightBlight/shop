package com.shop.goods.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 商品SPU实体类
 */
/*
 * 优化：改为Builder设计模式进行初始化
 */
public class SPUGoods implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long categoryId;			//分类ID
	private Long brandId;				//品牌ID
	private Long preId;					//上级分类ID
	private Integer level;				//分类级别
	private String type;				//型号
	private Long spuId;					//SPU商品ID
	private String spuName;				//SPU商品名称
	private Long specId;				//SPU规格ID
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;	//创建时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;	//修改时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime deleteTime;	//删除时间
	
	private Integer verifyStatus;		//1,未审核；2,正在审核；3,已审核
	private Integer deleteStatus;		//1,未删除；2,已删除
	
	private String specName;			//SPU规格名称，以JSON形势存储发送

	public Long getSpuId() {
		return spuId;
	}

	public Long getSpecId() {
		return specId;
	}

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

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public LocalDateTime getDeleteTime() {
		return deleteTime;
	}

	public Integer getVerifyStatus() {
		return verifyStatus;
	}

	public Integer getDeleteStatus() {
		return deleteStatus;
	}

	public void setSpuId(Long spuId) {
		this.spuId = spuId;
	}

	public void setSpecId(Long specId) {
		this.specId = specId;
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

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	public void setDeleteTime(LocalDateTime deleteTime) {
		this.deleteTime = deleteTime;
	}

	public void setVerifyStatus(Integer verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	public void setDeleteStatus(Integer deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	@Override
	public String toString() {
		return "SPUGoods [spuId=" + spuId + ", specId=" + specId + ", categoryId=" + categoryId + ", brandId=" + brandId
				+ ", preId=" + preId + ", level=" + level + ", type=" + type + ", spuName=" + spuName + ", specName="
				+ specName + ", createTime=" + createTime + ", updateTime=" + updateTime + ", deleteTime=" + deleteTime
				+ ", verifyStatus=" + verifyStatus + ", deleteStatus=" + deleteStatus + "]";
	}
}
