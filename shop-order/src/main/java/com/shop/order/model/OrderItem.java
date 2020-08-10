package com.shop.order.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 子订单实体类
 */
/*
 * 优化：改为Builder设计模式进行初始化
 */
public class OrderItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long orderItemId;			//子订单ID
	private Long orderId;				//主订单ID
	private Long customerId;			//用户ID
	private Long shopId;				//店铺ID
	private Long orderItemNumber;		//子订单总商品数量
	private BigDecimal orderItemPrice;	//子订单总金额
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;	//创建时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;	//修改时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime deleteTime;	//删除时间
	
	private Integer orderItemStatus;	//1,下单(create)；2,付款(pay)；3,卖家发货(deliver)；4,买家收货(receive)；5,退货(rereturn)
	private Integer deleteStatus;		//1,未删除；2,删除中；3,已删除
	private Integer cancelStatus;		//1,未取消；2,取消中；3,已取消
	private Integer refundStatus;		//1,无退款；2,退款中；3,部分退款；4,全退款
	
	private List<OrderDetail> orderDetailList;	//订单详情列表

	public Long getOrderItemId() {
		return orderItemId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public Long getShopId() {
		return shopId;
	}

	public BigDecimal getOrderItemPrice() {
		return orderItemPrice;
	}

	public Long getOrderItemNumber() {
		return orderItemNumber;
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

	public Integer getOrderItemStatus() {
		return orderItemStatus;
	}

	public Integer getDeleteStatus() {
		return deleteStatus;
	}

	public Integer getCancelStatus() {
		return cancelStatus;
	}

	public Integer getRefundStatus() {
		return refundStatus;
	}

	public List<OrderDetail> getOrderDetailList() {
		return orderDetailList;
	}

	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public void setOrderItemPrice(BigDecimal orderItemPrice) {
		this.orderItemPrice = orderItemPrice;
	}

	public void setOrderItemNumber(Long orderItemNumber) {
		this.orderItemNumber = orderItemNumber;
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

	public void setOrderItemStatus(Integer orderItemStatus) {
		this.orderItemStatus = orderItemStatus;
	}

	public void setDeleteStatus(Integer deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public void setCancelStatus(Integer cancelStatus) {
		this.cancelStatus = cancelStatus;
	}

	public void setRefundStatus(Integer refundStatus) {
		this.refundStatus = refundStatus;
	}

	public void setOrderDetailList(List<OrderDetail> orderDetailList) {
		this.orderDetailList = orderDetailList;
	}

	@Override
	public String toString() {
		return "OrderItem [orderItemId=" + orderItemId + ", orderId=" + orderId + ", customerId=" + customerId
				+ ", shopId=" + shopId + ", orderItemPrice=" + orderItemPrice + ", orderItemNumber=" + orderItemNumber
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + ", deleteTime=" + deleteTime
				+ ", orderItemStatus=" + orderItemStatus + ", deleteStatus=" + deleteStatus + ", cancelStatus="
				+ cancelStatus + ", refundStatus=" + refundStatus + ", orderDetailList=" + orderDetailList + "]";
	}
}
