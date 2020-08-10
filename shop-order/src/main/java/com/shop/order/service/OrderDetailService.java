package com.shop.order.service;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.shop.order.model.OrderDetail;

public interface OrderDetailService {
	/**
	 * 新增订单详情
	 */
	int saveOrderDetail(OrderDetail orderDetail);
	
	/**
	 * 根据订单详情Id修改订单详情评论状态
	 * @param orderDetailId 订单详情Id
	 * @param commentStatus false,未评论；true,已评论
	 */
    int updateOrderDetailCommentStatusById(Long orderDetailId, Boolean commentStatus);
    
    /**
	 * 根据订单详情Id获取订单详情
	 * @param orderDetailId 订单详情Id
	 */
    OrderDetail getOrderDetailById(Long orderDetailId);
    
    /**
	 * 根据子订单Id获取所有订单详情
	 * @param orderItemId 子订单Id
	 */
    OrderDetail getOrderDetailByOrderItemId(Long orderItemId);
    
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * @param orderDetailId 订单详情Id
	 */
	InterProcessMutex setOrderDetailLockById(Long orderDetailId);
    
	/**
	 * 根据Id获取订单详情分布锁
	 * @param orderDetailId 订单详情Id
	 */
	InterProcessMutex getOrderDetailLockById(Long orderDetailId);
}
