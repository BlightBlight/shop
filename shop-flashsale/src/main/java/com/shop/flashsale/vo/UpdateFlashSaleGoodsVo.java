package com.shop.flashsale.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 修改秒杀商品Vo
 */
public class UpdateFlashSaleGoodsVo {
	@NotNull(message = "商铺不能为空")
	private Long shopId;					//商铺ID
	
	@NotNull(message = "SPU商品不能为空")
	private Long spuId;						//SPU商品ID
	
	@NotNull(message = "秒杀商品不能为空")
	private Long flashSaleGoodsId;			//秒杀商品ID
	
	@NotEmpty(message = "秒杀商品名称不能为空")
	private String flashSaleGoodsName;		//秒杀商品名称

	@NotNull(message = "库存不能为空")
	private Long nowStock;					//秒杀商品快照库存
	
	@NotNull(message = "库存不能为空")
	private Long stock;						//秒杀商品库存

	@NotNull(message = "秒杀价不能为空")
	private BigDecimal price;				//秒杀价
	
	@NotNull(message = "规格不能为空")
	private Long specId;					//规格Id
	
	@NotEmpty(message = "规格不能为空")
	private String specName;				//规格名称
	
	@NotNull(message = "规格值不能为空")
	private Long specValueId;				//规格值Id
	
	@NotEmpty(message = "规格值不能为空")
	private String specValueName;			//规格值名称
	
	@NotNull(message = "修改时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;		//修改时间

	public Long getShopId() {
		return shopId;
	}

	public Long getSpuId() {
		return spuId;
	}

	public Long getFlashSaleGoodsId() {
		return flashSaleGoodsId;
	}

	public String getFlashSaleGoodsName() {
		return flashSaleGoodsName;
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

	public String getSpecName() {
		return specName;
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

	public void setFlashSaleGoodsId(Long flashSaleGoodsId) {
		this.flashSaleGoodsId = flashSaleGoodsId;
	}

	public void setFlashSaleGoodsName(String flashSaleGoodsName) {
		this.flashSaleGoodsName = flashSaleGoodsName;
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

	public void setSpecName(String specName) {
		this.specName = specName;
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
		return "UpdateFlashSaleGoodsVo [shopId=" + shopId + ", spuId=" + spuId + ", flashSaleGoodsId="
				+ flashSaleGoodsId + ", flashSaleGoodsName=" + flashSaleGoodsName + ", nowStock=" + nowStock
				+ ", stock=" + stock + ", price=" + price + ", specId=" + specId + ", specName=" + specName
				+ ", specValueId=" + specValueId + ", specValueName=" + specValueName + ", updateTime=" + updateTime
				+ "]";
	}
}
