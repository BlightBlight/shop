package com.shop.goods.service;

import com.shop.goods.model.Brand;

/**
 * 商品品牌缓存接口层
 */
public interface BrandCacheService {
	/**
	 * 根据名称设置商品品牌缓存
	 */
	void setBrandByName(Brand brand);
	
	/**
	 * 根据Id设置商品品牌名称缓存
	 */
	void setBrandNameById(Long brandId, String brandName);
	
	/**
	 * 根据名称删除商品品牌缓存
	 * @param brandName 商品品牌名称
	 */
    void delBrandByName(String brandName);
    
	/**
	 * 根据Id删除商品品牌名称缓存
	 * @param brandId 商品品牌Id
	 */
    void delBrandNameById(Long brandId);
    
	/**
	 * 根据名称获取商品品牌缓存
	 * @param brandName 商品品牌名称
	 */
    Brand getBrandByName(String brandName);
    
	/**
	 * 根据Id获取商品品牌缓存
	 * @param brandId 商品品牌Id
	 */
    String getBrandNameById(Long brandId);
}
