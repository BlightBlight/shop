package com.shop.goods.service;

import com.shop.goods.model.SPUGoodsSpec;
import com.shop.goods.model.SPUGoods;

/**
 * SPU商品缓存接口层
 */
public interface SPUGoodsCacheService {
	/**
	 * 根据名称设置SPU商品缓存
	 */
	void setSPUGoodsByName(SPUGoods spuGoods);
	
	/**
	 * 根据Id设置SPU商品名称缓存
	 * @param spuId spu商品Id
	 * @param spuName spu商品名称
	 */
	void setSPUGoodsNameById(Long spuId, String spuName);
	
	/**
	 * 根据specId设置SPU商品名称缓存
	 * @param specId 规格Id
	 * @param spuName spu商品名称
	 */
	void setSPUGoodsNameBySpecId(Long specId, String spuName);
	
	/**
	 * 根据Id设置规格缓存
	 */
	void setSPUGoodsSpec(SPUGoodsSpec spuGoodsSpec);
	
	/**
	 * 根据名称删除SPU商品名称缓存
	 * @param spuName spu商品名称
	 */
    void delSPUGoodsByName(String spuName);

	/**
	 * 根据Id删除SPU商品名称缓存
	 * @param spuId spu商品Id
	 */
    void delSPUGoodsNameById(Long spuId);
    
	/**
	 * 根据specId删除SPU商品名称缓存
	 * @param specId 规格Id
	 */
    void delSPUGoodsNameBySpecId(Long specId);
    
	/**
	 * 根据Id删除规格缓存
	 * @param specId 规格Id
	 */
    void delSPUGoodsSpecById(Long specId);
    
	/**
	 * 根据名称获取SPU商品缓存
	 * @param spuName spu商品名称
	 */
    SPUGoods getSPUGoodsByName(String spuName);
    
	/**
	 * 根据Id获取SPU商品名称缓存
	 * @param spuId spu商品Id
	 */
    String getSPUGoodsNameById(Long spuId);
    
	/**
	 * 根据specId获取SPU商品名称缓存
	 * @param specId 规格Id
	 */
    String getSPUGoodsNameBySpecId(Long specId);
    
	/**
	 * 根据Id获取规格缓存
	 * @param specId 规格Id
	 */
    SPUGoodsSpec getSPUGoodsSpecById(Long specId);
}
