package com.shop.goods.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 修改SKU商品Vo
 */
public class UpdateSKUGoodsVo {
	@NotNull(message = "商铺不能为空")
	private Long shopId;		//商铺ID

	@NotNull(message = "spu商品Id不能为空")
	private Long spuId;		//spu商品ID
	
	@NotNull(message = "sku商品Id不能为空")
	private Long skuId;		//sku商品ID
	
	@NotEmpty(message = "商品名称不能为空")
	private String skuName;		//sku商品名称
	
	@NotNull(message = "现库存不能为空")
	private Long nowStock;		//快照库存
	
	@NotNull(message = "库存不能为空")
	private Long stock;			//库存
	
	@NotNull(message = "价格不能为空")
	private BigDecimal price;	//价格

	@NotNull(message = "规格不能为空")
	private Long specId;		//规格ID
	
	@NotNull(message = "规格值不能为空")
	private Long specValueId;		//规格值ID
	
	@NotEmpty(message = "规格值不能为空")
	private String specValueName;	//规格值，JSON形式

	@NotNull(message = "修改时间不能为空")	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;	//修改时间

	public Long getShopId() {
		return shopId;
	}

	public Long getSpuId() {
		return spuId;
	}

	public Long getSkuId() {
		return skuId;
	}

	public String getSkuName() {
		return skuName;
	}

	public Long getNowStock() {
		return nowStock;
	}

	public Long getStock() {
		return stock;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Long getSpecId() {
		return specId;
	}

	public Long getSpecValueId() {
		return specValueId;
	}

	public String getSpecValueName() {
		return specValueName;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public void setSpuId(Long spuId) {
		this.spuId = spuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public void setNowStock(Long nowStock) {
		this.nowStock = nowStock;
	}

	public void setStock(Long stock) {
		this.stock = stock;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setSpecId(Long specId) {
		this.specId = specId;
	}

	public void setSpecValueId(Long specValueId) {
		this.specValueId = specValueId;
	}

	public void setSpecValueName(String specValueName) {
		this.specValueName = specValueName;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "UpdateSKUGoodsVo [shopId=" + shopId + ", spuId=" + spuId + ", skuId=" + skuId + ", skuName=" + skuName
				+ ", nowStock=" + nowStock + ", stock=" + stock + ", price=" + price + ", specId=" + specId
				+ ", specValueId=" + specValueId + ", specValueName=" + specValueName + ", updateTime=" + updateTime
				+ "]";
	}
}
