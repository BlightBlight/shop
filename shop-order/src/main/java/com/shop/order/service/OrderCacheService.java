package com.shop.order.service;

import com.shop.order.model.Order;

public interface OrderCacheService {
	/**
	 * 设置商品缓存
     * @param orderId 总订单Id
	 */
	void setOrderById(Order Order);
	
    /**
     * 删除商品缓存
     * @param orderId 总订单Id
     */
    void delOrderById(Long orderId);

    /**
     * 获取商品缓存
     * @param orderId 总订单Id
     */
    Order getOrderById(Long orderId);
}
