package com.shop.flashsale.service;

import com.shop.order.model.Order;

public interface FlashSaleCacheService {
	/**
	 * 根据customerId和flashSaleGoodsId新增验证码
	 * @param customerId 用户Id
	 * @param flashSaleGoodsId 秒杀商品Id
	 * @param result 验证码结果
	 */
	void setFlashSaleGoodsVerifyCode(Long customerId, Long flashSaleGoodsId, String verifyCode);
	
	/**
	 * 根据Id新增秒杀地址
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
	void setFlashSaleGoodsPath(Long flashSaleGoodsId, String url);
	
	/**
	 * 根据customerId新增order订单
	 * @param customerId 用户Id
	 * @param order 订单实体
	 */
	void setFlashSaleOrderById(Order order);
	
	/**
	 * 根据customerId和flashSaleGoodsId设置用户秒杀标志缓存
	 * @param customerId 用户Id
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
	void setCustomerFlagById(Long customerId, Long flashSaleGoodsId);
	
	/**
	 * 根据customerId和flashSaleGoodsId删除验证码
	 * @param customerId 用户Id
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
	void delFlashSaleGoodsVerifyCodeById(Long customerId, Long flashSaleGoodsId);
	
	/**
	 * 根据Id删除秒杀地址
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
	void delFlashSaleGoodsPathById(Long flashSaleGoodsId);
	
	/**
	 * 根据customerId删除order订单
	 * @param customerId 用户Id
	 * @param order 订单实体
	 */
	void delFlashSaleOrderById(Long customerId);
	
	/**
	 * 根据customerId和flashSaleGoodsId删除用户秒杀标志缓存
	 * @param customerId 用户Id
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
    void delCustomerFlagById(Long customerId, Long flashSaleGoodsId);
	
	/**
	 * 根据Id查找验证码
	 * @param customerId 用户Id
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
	String getFlashSaleGoodsVerifyCodeById(Long customerId, Long flashSaleGoodsId);
	
	/**
	 * 根据customerId查找秒杀地址
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
	String getFlashSaleGoodsPathById(Long flashSaleGoodsId);
	
	/**
	 * 根据customerId查找order订单
	 * @param customerId 用户Id
	 * @param order 订单实体
	 */
	Order getFlashSaleOrderById(Long customerId);
	
	/**
	 * 根据customerId和flashSaleGoodsId获取用户秒杀标志缓存
	 * @param customerId 用户Id
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
    boolean getCustomerFlagById(Long customerId, Long flashSaleGoodsId);
}
