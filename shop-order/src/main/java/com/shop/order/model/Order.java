package com.shop.order.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 主订单实体类
 */
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long orderId;				//主订单Id
	private Long customerId;			//用户Id
	private Long orderNumber;			//主订单总商品数量
	private BigDecimal orderPrice;		//主订单总金额
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;	//创建时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;	//修改时间
	
	private Integer deleteStatus;		//1,未删除；2,伪删除；3,真的已删除
	
	private List<OrderItem> orderItemList;	//子订单列表
	
	public Order() {
		
	}
	
	public Long getOrderId() {
		return orderId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public BigDecimal getOrderPrice() {
		return orderPrice;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public Integer getDeleteStatus() {
		return deleteStatus;
	}

	public List<OrderItem> getOrderItemList() {
		return orderItemList;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public void setOrderPrice(BigDecimal orderPrice) {
		this.orderPrice = orderPrice;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	public void setDeleteStatus(Integer deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}

	@Override
	public String toString() {
		return "Order [orderId=" + orderId + ", customerId=" + customerId + ", orderPrice=" + orderPrice
				+ ", orderNumber=" + orderNumber + ", createTime=" + createTime + ", updateTime=" + updateTime
				+ ", deleteStatus=" + deleteStatus + ", orderItemList=" + orderItemList + "]";
	}
}
