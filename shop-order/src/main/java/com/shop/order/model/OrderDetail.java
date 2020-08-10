package com.shop.order.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 订单详情实体类
 */
/*
 * 优化：改为Builder设计模式进行初始化
 */
public class OrderDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long orderDetailId;					//订单详情Id
	private Long orderItemId;					//子订单Id
	private Long customerId;					//用户Id
	private Long shopId;						//商铺Id
	private String shopName;					//商铺名称
	private Long skuId;							//sku商品Id
	private String skuName;						//sku商品名称
	private Long number;						//sku商品数量
	private BigDecimal price;					//sku商品价格
	private Long specValueId;					//规格值Id
	private String specValueName;				//规格值名称
	private BigDecimal orderDetailPrice;		//实付价格
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;			//创建时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;			//修改时间
	
	private Integer commentStatus;				//1,未评论；2,已评论

	public Long getOrderDetailId() {
		return orderDetailId;
	}

	public Long getOrderItemId() {
		return orderItemId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public Long getShopId() {
		return shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public Long getSkuId() {
		return skuId;
	}

	public String getSkuName() {
		return skuName;
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

	public BigDecimal getOrderDetailPrice() {
		return orderDetailPrice;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public Integer getCommentStatus() {
		return commentStatus;
	}

	public void setOrderDetailId(Long orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
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

	public void setOrderDetailPrice(BigDecimal orderDetailPrice) {
		this.orderDetailPrice = orderDetailPrice;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	public void setCommentStatus(Integer commentStatus) {
		this.commentStatus = commentStatus;
	}

	@Override
	public String toString() {
		return "OrderDetail [orderDetailId=" + orderDetailId + ", orderItemId=" + orderItemId + ", customerId="
				+ customerId + ", shopId=" + shopId + ", shopName=" + shopName + ", skuId=" + skuId + ", skuName="
				+ skuName + ", number=" + number + ", price=" + price + ", specValueId=" + specValueId
				+ ", specValueName=" + specValueName + ", orderDetailPrice=" + orderDetailPrice + ", createTime="
				+ createTime + ", updateTime=" + updateTime + ", commentStatus=" + commentStatus + "]";
	}
}
