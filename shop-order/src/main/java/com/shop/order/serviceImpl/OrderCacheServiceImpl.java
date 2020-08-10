package com.shop.order.serviceImpl;

import org.springframework.stereotype.Service;

import com.shop.order.model.Order;
import com.shop.order.service.OrderCacheService;

@Service
public class OrderCacheServiceImpl implements OrderCacheService{

	@Override
	public void setOrderById(Order Order) {
		
	}

	@Override
	public void delOrderById(Long orderId) {
		
	}

	@Override
	public Order getOrderById(Long orderId) {
		
		return null;
	}

}
