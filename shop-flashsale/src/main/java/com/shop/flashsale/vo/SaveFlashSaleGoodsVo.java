package com.shop.flashsale.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 新增秒杀商品Vo
 */
public class SaveFlashSaleGoodsVo {
	@NotNull(message = "商品不能为空")
	private Long shopId;					//商铺Id

	@NotNull(message = "SPU商品不能为空")
	private Long spuId;						//SPU商品Id
	
	@NotEmpty(message = "秒杀商品名称不能为空")
	private String flashSaleGoodsName;		//秒杀商品名称

	@NotNull(message = "库存不能为空")
	private Long stock;						//秒杀商品库存

	@NotNull(message = "秒杀价不能为空")
	private BigDecimal price;				//秒杀价
	
	@NotNull(message = "规格不能为空")
	private Long specId;					//规格Id
	
	@NotEmpty(message = "规格不能为空")
	private String specName;				//规格名称
	
	@NotEmpty(message = "规格值不能为空")
	private String specValueName;			//规格值名称
	
	@NotNull(message = "创建时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;		//创建时间

	public Long getSpecId() {
		return specId;
	}

	public void setSpecId(Long specId) {
		this.specId = specId;
	}

	public Long getShopId() {
		return shopId;
	}

	public Long getSpuId() {
		return spuId;
	}

	public String getFlashSaleGoodsName() {
		return flashSaleGoodsName;
	}

	public Long getStock() {
		return stock;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getSpecName() {
		return specName;
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

	public void setFlashSaleGoodsName(String flashSaleGoodsName) {
		this.flashSaleGoodsName = flashSaleGoodsName;
	}

	public void setStock(Long stock) {
		this.stock = stock;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setSpecName(String specName) {
		this.specName = specName;
	}

	public void setSpecValueName(String specValueName) {
		this.specValueName = specValueName;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "SaveFlashSaleGoodsVo [shopId=" + shopId + ", spuId=" + spuId + ", flashSaleGoodsName="
				+ flashSaleGoodsName + ", stock=" + stock + ", price=" + price + ", specId=" + specId + ", specName="
				+ specName + ", specValueName=" + specValueName + ", createTime="
				+ createTime + "]";
	}
}
