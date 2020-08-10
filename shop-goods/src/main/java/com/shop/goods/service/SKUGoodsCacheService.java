package com.shop.goods.service;

import com.shop.goods.model.SKUGoodsSpecValue;
import com.shop.goods.model.SKUGoods;

/**
 * SKU商品缓存接口层
 */
public interface SKUGoodsCacheService {
	/**
	 * 根据名称设置SKU商品缓存
	 */
	void setSKUGoodsByName(SKUGoods skuGoods);
	
	/**
	 * 根据Id设置SKU商品名称缓存
	 * @param skuId sku商品Id
	 * @param skuName sku商品名称
	 */
	void setSKUGoodsNameById(Long skuId, String skuName);
	
	/**
	 * 根据specValueId设置SKU商品名称缓存
	 * @param specValueId 规格值Id
	 * @param skuName sku商品名称
	 */
	void setSKUGoodsNameBySpecValueId(Long specValueId, String skuName);
	
	/**
	 * 根据Id设置规格值缓存
	 */
	void setSKUGoodsSpecValueById(SKUGoodsSpecValue skuGoodsSpecValue);
	
	/**
	 * 根据名称删除SKU商品缓存
	 * @param skuName sku商品名称
	 */
    void delSKUGoodsByName(String skuName);
    
	/**
	 * 根据Id删除规格值称缓存
	 * @param skuId sku商品Id
	 */
	void delSKUGoodsNameById(Long skuId);
    
	/**
	 * 根据specValueId删除规格值称缓存
	 * @param specValueId 规格值Id
	 */
	void delSKUGoodsNameBySpecValueId(Long specValueId);
	
	/**
	 * 根据Id删除规格值缓存
	 * @param specValueId 规格值Id
	 */
	void delSKUGoodsSpecValueById(Long specValueId);
	
	/**
	 * 递减SKU商品库存
	 * @param skuName sku商品名称
	 * @param delta 要减少几(正整数)
	 */
    void decrSKUGoodsByName(String skuName, Long delta);
    
	/**
	 * 根据名称获取SKU商品缓存
	 * @param skuName sku商品名称
	 */
    SKUGoods getSKUGoodsByName(String skuName);
    
	/**
	 * 根据Id获取SKU商品名称
	 * @param skuId sku商品Id
	 */
    String getSKUGoodsNameById(Long skuId);
    
	/**
	 * 根据specValueId获取SKU商品名称
	 * @param specValueId 规格值Id
	 */
    String getSKUGoodsNameBySpecValueId(Long specValueId);
    
	/**
	 * 根据Id获取规格值缓存
	 * @param specValueId 规格值Id
	 */
    SKUGoodsSpecValue getSKUGoodsSpecValueById(Long specValueId);
}
