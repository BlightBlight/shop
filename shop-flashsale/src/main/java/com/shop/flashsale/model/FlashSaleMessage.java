package com.shop.flashsale.model;

import java.io.Serializable;

import com.shop.order.model.Order;

public class FlashSaleMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long customerId;
	private Order order;
	
	public FlashSaleMessage() {
	}
	
	public FlashSaleMessage(Long customerId, Order order) {
		this.customerId = customerId;
		this.order = order;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public Order getOrder() {
		return order;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "FlashSaleMessage [customerId=" + customerId + ", order=" + order.toString() + "]";
	}
}
