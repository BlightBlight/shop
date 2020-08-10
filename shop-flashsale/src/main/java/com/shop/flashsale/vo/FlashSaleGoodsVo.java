package com.shop.flashsale.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 展示秒杀商品Vo
 */
public class FlashSaleGoodsVo {
	@NotNull(message = "商铺不能为空")
	private Long shopId;					//商铺Id

	@NotEmpty(message = "商铺不能为空")
	private String shopName;				//商铺名称
	
	@NotNull(message = "SPU商品不能为空")
	private Long spuId;						//SPU商品Id

	@NotNull(message = "秒杀商品Id不能为空")
	private Long flashSaleGoodsId;			//秒杀商品Id
	
	@NotEmpty(message = "秒杀商品名称不能为空")
	private String flashSaleGoodsName;		//秒杀商品名称
	
	@NotNull(message = "商品数量不能为空")
	private Long number;					//秒杀商品数量

	@NotNull(message = "秒杀价不能为空")
	private BigDecimal price;				//秒杀价
	
	@NotEmpty(message = "规格不能为空")
	private String specName;				//秒杀SKU规格名称
	
	@NotNull(message = "规格值不能为空")
	private Long specValueId;				//规格值表Id
	
	@NotEmpty(message = "规格值不能为空")
	private String specValueName;				//秒杀SKU商品规格值
	
	@NotNull(message = "秒杀开始时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;		//秒杀开始时间
	
	@NotNull(message = "秒杀开始时间倒计时不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime remailSeconds;		//秒杀开始时间倒计时
	
	@NotNull(message = "创建时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;			//秒杀开始时间倒计时
	
	public Long getShopId() {
		return shopId;
	}

	public String getShopName() {
		return shopName;
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

	public Long getNumber() {
		return number;
	}

	public BigDecimal getPrice() {
		return price;
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

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getRemailSeconds() {
		return remailSeconds;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
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

	public void setNumber(Long number) {
		this.number = number;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
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

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public void setRemailSeconds(LocalDateTime remailSeconds) {
		this.remailSeconds = remailSeconds;
	}

	@Override
	public String toString() {
		return "FlashSaleGoodsVo [shopId=" + shopId + ", shopName=" + shopName + ", spuId=" + spuId
				+ ", flashSaleGoodsId=" + flashSaleGoodsId + ", flashSaleGoodsName=" + flashSaleGoodsName + ", number="
				+ number + ", price=" + price + ", specName=" + specName + ", specValueId=" + specValueId
				+ ", specValueName=" + specValueName + ", startTime=" + startTime + ", remailSeconds=" + remailSeconds
				+ "]";
	}
}
