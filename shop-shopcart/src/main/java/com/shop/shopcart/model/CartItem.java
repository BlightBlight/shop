package com.shop.shopcart.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 与数据库中购物车对应的实体类
 */
/*
 * 优化：改为Builder设计模式进行初始化
 */
public class CartItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long cartItemId;			//购物车选项ID
	private Long customerId;			//用户ID
	private Long shopId;				//店铺ID
	private Long skuId;					//sku商品ID
	private Long number;				//sku商品数量
	private BigDecimal price;			//商品价格
	private Long specValueId;			//规格值ID
	private String specValueName;		//规格值名称
	private Long goodsPosition;			//商品顺序位置
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;	//创建时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;	//修改时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime deleteTime;	//删除时间
	
	private Integer selectStatus;		//1,未勾选；2,已勾选
	private Integer deleteStatus;		//1,未删除；2,已删除
	private Integer buyStatus;			//1,未购买；2,已购买
	
	public Long getCartItemId() {
		return cartItemId;
	}
	public Long getCustomerId() {
		return customerId;
	}
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
	public Long getGoodsPosition() {
		return goodsPosition;
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
	public Integer getSelectStatus() {
		return selectStatus;
	}
	public Integer getDeleteStatus() {
		return deleteStatus;
	}
	public Integer getBuyStatus() {
		return buyStatus;
	}
	public void setCartItemId(Long cartItemId) {
		this.cartItemId = cartItemId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
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
	public void setGoodsPosition(Long goodsPosition) {
		this.goodsPosition = goodsPosition;
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
	public void setSelectStatus(Integer selectStatus) {
		this.selectStatus = selectStatus;
	}
	public void setDeleteStatus(Integer deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	public void setBuyStatus(Integer buyStatus) {
		this.buyStatus = buyStatus;
	}
	
	@Override
	public String toString() {
		return "CartItem [cartItemId=" + cartItemId + ", customerId=" + customerId + ", shopId=" + shopId + ", skuId="
				+ skuId + ", number=" + number + ", price=" + price + ", specValueId=" + specValueId
				+ ", specValueName=" + specValueName + ", goodsPosition=" + goodsPosition + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + ", deleteTime=" + deleteTime + ", selectStatus=" + selectStatus
				+ ", deleteStatus=" + deleteStatus + ", buyStatus=" + buyStatus + "]";
	}
}