package com.shop.shopcart.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 新增购物车商品Vo
 */
public class SaveCartItemVo {
	@NotNull(message = "商铺不能为空")
	private Long shopId;				//店铺ID

	@NotNull(message = "sku商品不能为空")
	private Long skuId;					//sku商品ID
	
	@NotNull(message = "购买数量不能为空")
	private Long  number;				//sku商品数量

	@NotNull(message = "商品价格不能为空")
	private BigDecimal price;			//sku商品价格
	
	@NotNull(message = "请选择规格值")
	private Long specValueId;			//规格值Id
	
	@NotEmpty(message = "请选择规格值")
	private String specValueName;		//规格值名称
	
	@NotNull(message = "创建时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;	//创建时间

	public Long getShopId() {
		return shopId;
	}

	public Long getSkuId() {
		return skuId;
	}

	public Long getNumber() {
		return number;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Long getSpecValueId() {
		return specValueId;
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

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setSpecValueId(Long specValueId) {
		this.specValueId = specValueId;
	}

	public void setSpecValueName(String specValueName) {
		this.specValueName = specValueName;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "SaveCartItemVo [shopId=" + shopId + ", skuId=" + skuId + ", number=" + number + ", price=" + price
				+ ", specValueId=" + specValueId + ", specValueName=" + specValueName + ", createTime=" + createTime
				+ "]";
	}
}
