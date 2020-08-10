package com.shop.order.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shop.order.model.OrderItem;

/**
 * 新增订单Vo
 */
public class SaveOrderVo {
	@NotEmpty(message = "子订单不能为空")
	List<OrderItem> orderItemList;			//子订单列表
	
	@NotNull(message = "总商品数量不能为空")
	private Long orderNumber;				//总商品数量

	@NotNull(message = "总商品金额不能为空")
	private BigDecimal orderPrice;			//总商品金额
	
	@NotNull(message = "创建时间不能为空")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;		//创建时间

	public List<OrderItem> getOrderItemList() {
		return orderItemList;
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

	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
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

	@Override
	public String toString() {
		return "SaveOrderVo [orderItemList=" + orderItemList + ", orderPrice=" + orderPrice + ", orderNumber="
				+ orderNumber + ", createTime=" + createTime + "]";
	}
}
