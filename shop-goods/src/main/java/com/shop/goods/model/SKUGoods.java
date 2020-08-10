package com.shop.goods.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 商品SKU实体类
 */
/*
 * 优化：改为Builder设计模式进行初始化
 */
public class SKUGoods implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long shopId;				//店铺ID
	private Long spuId;					//SPU商品ID
	private Long skuId;					//SKU商品ID
	private String skuName;				//SKU商品名称
	private Long stock;					//库存
	private BigDecimal price;			//价格
	private Long specValueId;			//SKU规格值ID
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;	//创建时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;	//修改时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime deleteTime;	//删除时间
	
	private Integer verifyStatus;		//1,未审核；2,审核中；3,已审核
	private Integer skuStatus;			//1,未上架；2,已上架；3，,已下架
	private Integer deleteStatus;		//1,未删除；2,已删除
	
	private String specName;			//SKU规格名称
	private String specValueName;		//SKU规格值

	public Long getSkuId() {
		return skuId;
	}
	public Long getSpuId() {
		return spuId;
	}
	public Long getSpecValueId() {
		return specValueId;
	}
	public Long getShopId() {
		return shopId;
	}
	public String getSkuName() {
		return skuName;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public Long getStock() {
		return stock;
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
	public Integer getSkuStatus() {
		return skuStatus;
	}
	public Integer getDeleteStatus() {
		return deleteStatus;
	}
	public String getSpecName() {
		return specName;
	}
	public String getSpecValueName() {
		return specValueName;
	}
	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}
	public void setSpuId(Long spuId) {
		this.spuId = spuId;
	}
	public void setSpecValueId(Long specValueId) {
		this.specValueId = specValueId;
	}
	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}
	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public void setStock(Long stock) {
		this.stock = stock;
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
	public void setSkuStatus(Integer skuStatus) {
		this.skuStatus = skuStatus;
	}
	public void setDeleteStatus(Integer deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	public void setSpecName(String specName) {
		this.specName = specName;
	}
	public void setSpecValueName(String specValueName) {
		this.specValueName = specValueName;
	}
	@Override
	public String toString() {
		return "SKUGoods [skuId=" + skuId + ", spuId=" + spuId + ", specValueId=" + specValueId + ", shopId=" + shopId
				+ ", skuName=" + skuName + ", price=" + price + ", stock=" + stock + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + ", deleteTime=" + deleteTime + ", verifyStatus=" + verifyStatus
				+ ", skuStatus=" + skuStatus + ", deleteStatus=" + deleteStatus + ", specName=" + specName
				+ ", specValueName=" + specValueName + "]";
	}
	
	
}
