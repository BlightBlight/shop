package com.shop.goods.service;

import com.shop.goods.model.Category;

/**
 * 商品分类缓存接口层
 */
public interface CategoryCacheService {
	/**
	 * 根据名称设置商品分类缓存
	 */
	void setCategoryByName(Category category);
	
	/**
	 * 根据Id设置商品分类Id缓存
	 * @param categoryId 商品分类Id
	 * @param categoryName 商品分类名称
	 */
	void setCategoryNameById(Long categoryId, String categoryName);
	
	/**
	 * 根据名称删除商品分类缓存
	 * @param categoryName 商品名称
	 */
    void delCategoryByName(String categoryName);
    
	/**
	 * 根据Id删除商品分类Id缓存
	 * @param categoryId 商品分类Id
	 */
    void delCategoryNameById(Long categoryId);
    
	/**
	 * 根据名称获取商品分类缓存
	 * @param categoryName 商品名称
	 */
    Category getCategoryByName(String categoryName);
    
	/**
	 * 根据名称获取商品分类Id缓存
	 * @param categoryId 商品分类Id
	 */
    String getCategoryNameById(Long categoryId);
    
}
