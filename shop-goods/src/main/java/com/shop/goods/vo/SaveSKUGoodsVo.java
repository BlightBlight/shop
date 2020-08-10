package com.shop.goods.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 新增SKU商品Vo
 */
public class SaveSKUGoodsVo {
	@NotNull(message = "商铺不能为空")
	private Long shopId;		//商铺ID

	@NotNull(message = "spu商品Id不能为空")
	private Long spuId;		//spu商品ID
	
	@NotEmpty(message = "商品名称不能为空")
	private String skuName;		//sku商品名称
	
	@NotNull(message = "库存不能为空")
	private Long stock;			//库存

	@NotNull(message = "价格不能为空")
	private BigDecimal price;	//价格
	
	@NotNull(message = "规格不能为空")
	private Long specId;		//规格ID
	
	@NotEmpty(message = "规格值不能为空")
	private String specValueName;	//规格值名称，JSON形式
	
	@NotNull(message = "创建时间不能为空")	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;	//创建时间

	public Long getShopId() {
		return shopId;
	}

	public Long getSpuId() {
		return spuId;
	}

	public String getSkuName() {
		return skuName;
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

	public String getSpecValueName() {
		return specValueName;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public void setSpuId(Long spuId) {
		this.spuId = spuId;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
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

	public void setSpecValueName(String specValueName) {
		this.specValueName = specValueName;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "SaveSKUGoodsVo [shopId=" + shopId + ", spuId=" + spuId + ", skuName=" + skuName + ", stock=" + stock
				+ ", price=" + price + ", specId=" + specId + ", specValueName="
				+ specValueName + ", createTime=" + createTime + "]";
	}
}
