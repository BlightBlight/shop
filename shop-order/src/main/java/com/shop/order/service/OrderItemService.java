package com.shop.order.service;

import java.util.List;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.shop.order.model.OrderItem;

public interface OrderItemService {
	
	/**
	 * 新增子订单
	 */
	int saveOrderItem(OrderItem orderItem);
	
	/**
	 * 根据子订单Id删除子订单
	 * @param orderItemId  子订单Id
	 */
	int removeOrderItemById(Long orderItemId);
	
	/**
	 * 根据子订单Id修改子订单订单状态
	 * @param orderItemId 子订单Id
	 * @param orderItemStatus 0,下单(create)；1,付款(pay)；2,卖家发货(deliver)；3,买家收货(receive)；4,退货(rereturn)
	 */
    int updateOrderItemStatusById(Long orderItemId, Integer orderItemStatus);
    
	/**
	 * 根据子订单Id修改子订单退款状态
	 * @param orderItemId 子订单Id
	 * @param refundStatus 0,未退款；1,退款中；2,部分退款；3,全退款
	 */
    int updateOrderItemrefundStatusById(Long orderItemId, Integer refundStatus);
    
    /**
	 * 根据子订单Id查找子订单信息
	 * @param orderItemId 子订单Id
	 */
    OrderItem getOrderItemById(Long orderItemId);
    
    /**
	 * 根据子订单Id查找子订单是否存在
	 */
    OrderItem isOrderItemExists(Long orderItemId);
    
	/**
	 * 根据主订单Id查找所有子订单信息
	 * @param orderId 主订单Id
	 */
    List<OrderItem> listOrderItemByOrderId(Long orderId);
    
	/**
	 * 根据用户Id查找所有子订单信息
	 * @param customerId 用户Id
	 */
    List<OrderItem> listOrderItemByCustomerId(Long customerId);
    
    /**
	 * 发送延迟消息取消子订单
	 */
    int sendDelayMessageCancelOrderItem(Long orderItemId);
    
	/**
	 * 取消单个超时子订单
	 */
    int cancelOrderItem(Long orderItemId);
    
	/**
	 * 自动取消超时子订单
	 */
    int cancelTimeOutOrderItem();
    
    /**
	 * 支付成功后的回调
	 */
    int paySuccess(Long orderId, Integer payType);
    
	/**
	 * 确认收货
	 */
    int confirmReceiveOrder(Long orderId);
    
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * @param orderItemId 子订单Id
	 */
	InterProcessMutex setOrderItemLockById(Long orderItemId);
    
	/**
	 * 根据Id获取子订单分布锁
	 * @param orderItemId 子订单Id
	 */
	InterProcessMutex getOrderItemLockById(Long orderItemId);
    
}
