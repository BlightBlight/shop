package com.shop.flashsale.service;

import com.shop.flashsale.model.FlashSaleGoods;

public interface FlashSaleGoodsCacheService {
	/**
	 * 根据名称设置秒杀商品缓存
	 */
	void setFlashSaleGoodsByName(FlashSaleGoods flashSaleGoods);
	
	/**
	 * 根据Id设置秒杀商品名称缓存
	 * @param flashSaleGoodsId 秒杀商品Id
	 * @param flashSaleGoodsName 秒杀商品名称
	 */
	void setFlashSaleGoodsNameById(Long flashSaleGoodsId, String flashSaleGoodsName);
	
	/**
	 * 根据Id设置秒杀商品库存缓存
	 * @param flashSaleGoodsId 秒杀商品Id
	 * @param stock 库存
	 */
	void setFlashSaleGoodsStockById(Long flashSaleGoodsId, Long stock);
	
	/**
	 * 根据名称删除秒杀商品缓存
	 * @param flashSaleGoodsName 秒杀商品名称
	 */
    void delFlashSaleGoodsByName(String flashSaleGoodsName);

	/**
	 * 根据Id删除秒杀商品名称缓存
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
    void delFlashSaleGoodsNameById(Long flashSaleGoodsId);
    
	/**
	 * 根据Id删除秒杀商品库存缓存
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
    void delFlashSaleGoodsStockById(Long flashSaleGoodsId);
    
	/**
	 * 根据名称获取秒杀商品
	 * @param flashSaleGoodsName 秒杀商品名称
	 */
    FlashSaleGoods getFlashSaleGoodsByName(String flashSaleName);
    
	/**
	 * 根据Id获取秒杀商品缓存
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
    String getFlashSaleGoodsNameById(Long flashSaleGoodsId);
    
	/**
	 * 根据Id获取秒杀商品库存缓存
	 * @param flashSaleGoodsId 秒杀商品Id
	 */
    Long getFlashSaleGoodsStockById(Long flashSaleGoodsId);
    
}
