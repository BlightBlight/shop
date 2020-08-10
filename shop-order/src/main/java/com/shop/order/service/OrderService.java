package com.shop.order.service;

import java.util.List;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.shop.order.model.Order;
import com.shop.order.vo.SaveOrderVo;

public interface OrderService {
	/**
	 * 新增主订单
	 * @param customerId 用户Id
	 * @param saveOrderVo 新增订单Vo
	 */
    boolean saveOrder(Long customerId, SaveOrderVo saveOrderVo);
    
	/**
	 * 新增主订单
	 */
    boolean saveOrder(Order order);
    
	/**
	 * 根据主订单Id删除主订单
	 * @param orderId 主订单Id
	 */
    int removeOrderById(Long orderId);
    
	/**
	 * 根据主订单Id修改主订单状态
	 * @param orderId     主订单Id
	 * @param orderStatus 0,未删除；1,伪删除；2,真的已删除
	 */
    int updateOrderStatus(Long orderId, Integer orderStatus);
    
	/**
	 * 根据主订单Id查找主订单
	 */
    Order getOrderById(Long orderId);
    
	/**
	 * 根据用户Id查找所有主订单
	 */
    List<Order> listOrderById(Long customerId);
    
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * @param customerId 用户Id
	 */
	InterProcessMutex setOrderLockById(Long customerId);
    
	/**
	 * 根据Id获取订单分布锁
	 * @param customerId 用户Id
	 */
	InterProcessMutex getOrderLockById(Long customerId);
}
