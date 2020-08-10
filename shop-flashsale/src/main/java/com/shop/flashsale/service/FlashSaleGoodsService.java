package com.shop.flashsale.service;

import java.util.List;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.shop.flashsale.model.FlashSaleGoods;
import com.shop.flashsale.vo.SaveFlashSaleGoodsVo;
import com.shop.flashsale.vo.UpdateFlashSaleGoodsVo;

public interface FlashSaleGoodsService {
	/**
	 * 新增秒杀商品
	 */
	boolean saveFlashSaleGoods(SaveFlashSaleGoodsVo saveFlashSaleGoodsVo);
	
	/**
	 * 根据名称删除秒杀商品
	 * @param flashSaleGoodsName 秒杀商品名称
	 */
	boolean removeFlashSaleGoodsByName(String flashSaleGoodsName);
	
	/**
	 * 修改秒杀商品
	 */
	boolean updateFlashSaleGoods(UpdateFlashSaleGoodsVo updateFlashSaleGoodsVo);
	
	/**
	 * 递减秒杀商品库存
	 * @param flashSaleGoodsId 秒杀商品Id
	 * @param delta 要减少几(正整数)
	 */
	boolean decrFlashSaleGoodsById(Long flashSaleGoodsId, Long delta);
	
	/**
	 * 根据Id查找秒杀商品
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
	FlashSaleGoods getFlashSaleGoodsById(Long flashSaleGoodsId);
	
	/**
	 * 根据名称查找秒杀商品
	 * @param flashSaleGoodsName 秒杀商品名称
	 */
	FlashSaleGoods getFlashSaleGoodsByName(String flashSaleGoodsName);
	
	/**
	 * 根据promotionId查找所有秒杀商品
	 * @param promotionId 秒杀活动Id
	 */
	List<FlashSaleGoods> listFlashSaleGoodsByPromotionId(Long promotionId);
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于需要对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param flashSaleGoodsName 秒杀商品名称
	 */
	InterProcessMutex setFlashSaleGoodsLockByName(String flashSaleGoodsName);
	
	/**
	 * 根据名称获取秒杀商品分布锁
	 * @param flashSaleGoodsName 秒杀商品名称
	 */
	InterProcessMutex getFlashSaleGoodsLockByName(String flashSaleGoodsName);
	
	/**
	 * 根Id称获取秒杀商品分布锁
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
	InterProcessMutex getFlashSaleGoodsLockById(Long flashSaleGoodsId);
}
