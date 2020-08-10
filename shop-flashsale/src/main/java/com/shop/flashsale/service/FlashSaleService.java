package com.shop.flashsale.service;

import java.awt.image.BufferedImage;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.shop.flashsale.vo.FlashSaleGoodsVo;
import com.shop.order.model.Order;

public interface FlashSaleService {
	/**
	 * 根据flashSaleGoodsId和verifyCode新增秒杀地址
	 * @param flashSaleGoodsId 秒杀商品Id
	 * @param verifyCode 验证码
	 */
	void saveFlashSaleGoodsPath(Long flashSaleGoodsId, String verifyCode);
	
	/**
	 * 根据customerId和flashSaleGoodsId刷新图片验证码
	 * @param customerId 用户Id
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
	BufferedImage refreshVertifyBufferedImage(Long customerId, Long flashSaleGoodsId);
	
	/**
	 * 新增秒杀订单
	 * @param customerId 用户Id
	 * @param orderItemList 订单列表
	 */
	void saveFlashSaleOrder(Long customerId, FlashSaleGoodsVo flashSaleGoodsVo);
	
	/**
	 * 删除秒杀地址
	 * @param flashSaleGoodsId 秒杀商品Id
	 * @param verifyCode 验证码
	 */
	void removeFlashSaleGoodsPathById(Long flashSaleGoodsId);
	
	/**
	 * 删除验证码
	 * @param customerId 用户Id
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
	void removeFlashSaleGoodsVerifyCodeById(Long customerId, Long flashSaleGoodsId);
	
	/**
	 * 删除订单缓存
	 */
	void removeFlashSaleOrderById(Long customerId);
	
	/**
	 * 获取秒杀地址
	 * @param flashSaleGoodsId 秒杀商品Id
	 * @param verifyCode 验证码
	 */
	String getFlashSaleGoodsPathById(Long flashSaleGoodsId);
	
	/**
	 * 获取验证码
	 * @param customerId 用户Id
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
	String getFlashSaleGoodsVerifyCodeById(Long customerId, Long flashSaleGoodsId);
	
	/**
	 * 根据customerId获取订单
	 * @param customerId 用户Id
	 */
	Order getFlashSaleOrderById(Long customerId);
	
	/**
	 * 根据customerId和flashSaleGoodsId获取用户已秒杀标志
	 * @param customerId 用户Id
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
	boolean getCustomerFlagById(Long customerId, Long flashSaleGoodsId);
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于需要对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
	InterProcessMutex setFlashSaleGoodsLockById(Long flashSaleGoodsId);
	
	/**
	 * 根据Id获取秒杀商品分布锁
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
	InterProcessMutex getFlashSaleGoodsLockById(Long flashSaleGoodsId);
	
}
