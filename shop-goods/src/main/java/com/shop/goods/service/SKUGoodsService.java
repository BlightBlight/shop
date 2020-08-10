package com.shop.goods.service;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.shop.goods.model.SKUGoods;
import com.shop.goods.model.SKUGoodsSpecValue;
import com.shop.goods.vo.SaveSKUGoodsVo;
import com.shop.goods.vo.UpdateSKUGoodsVo;

/**
 * SKU商品接口层
 */
public interface SKUGoodsService {
	/**
	 * 创建SKU商品
	 */
	boolean saveSKUGoods(SaveSKUGoodsVo saveSKUGoodsVo);

	/**
	 * 删除SKU商品
	 * @param skuName sku商品名称
	 */
	boolean removeSKUGoodsByName(String skuName);
	
	/**
	 * 递减SKU商品库存
	 * @param skuName sku商品名称
	 * @param delta 要减少几(正整数)
	 */
    boolean decrSKUGoodsByName(String skuName, Long delta);
	
	/**
	 * 修改SKU商品
	 */
    boolean updateSKUGoods(UpdateSKUGoodsVo updateSKUGoodsVo);
    
	/**
	 * 根据名称获取SKU商品
	 * @param skuName sku商品名称
	 */
	SKUGoods getSKUGoodsByName(String skuName);
    
	/**
	 * 根据Id获取SKU商品
	 * @param skuId sku商品Id
	 */
	SKUGoods getSKUGoodsById(Long skuId);
	
	/**
	 * 根据specValueId获取SKU商品
	 * @param specValueId 规格值Id
	 */
	SKUGoods getSKUGoodsBySpecValueId(Long specValueId);
	
	/**
	 * 根据Id获取规格值
	 * @param specValueId 规格值Id
	 */
	SKUGoodsSpecValue getSKUGoodsSpecValueById(Long specValueId);
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * @param skuName sku商品名称
	 */
	InterProcessMutex setSKUGoodsLockByName(String skuName);
	
	/**
	 * 根据名称获取SKU商品分布锁
	 * @param skuName sku商品名称
	 */
	InterProcessMutex getSKUGoodsLockByName(String skuName);
	
	/**
	 * 根据Id获取SKU商品分布锁
	 * @param skuId sku商品Id
	 */
	InterProcessMutex getSKUGoodsLockById(Long skuId);
	
	/**
	 * 根据specValueId获取SKU商品分布锁
	 * @param specValueId 规格值Id
	 */
	InterProcessMutex getSKUGoodsLockBySpecValueId(Long specValueId);
	
	/**
	 * 根据Id获取规格值分布锁
	 * @param specValueId 规格值Id
	 */
	InterProcessMutex getSKUGoodsSpecValueLockById(Long specValueId);

}
