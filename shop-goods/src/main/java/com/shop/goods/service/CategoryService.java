package com.shop.goods.service;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.shop.goods.model.Category;
import com.shop.goods.vo.SaveCategoryVo;
import com.shop.goods.vo.UpdateCategoryVo;

/**
 * 商品分类接口层
 */
public interface CategoryService {
	/**
	 * 新增商品分类
	 */
	int saveCategory(SaveCategoryVo saveCategoryVo);

	/**
	 * 根据名称删除商品分类
	 * @param categoryName 商品分类名称
	 */
	boolean removeCategoryByName(String categoryName);
	
	/**
	 * 修改商品分类
	 */
	int updateCategory(UpdateCategoryVo updateCategoryVo);
	
	/**
	 * 根据名称查找商品分类
	 * @param categoryName 商品分类名称
	 */
	public Category getCategoryByName(String categoryName);
	
	/**
	 * 根据Id查找商品分类
	 * @param categoryId 商品分类Id
	 */
	public Category getCategoryById(Long categoryId);
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * @param categoryId 商品分类Id
	 */
	InterProcessMutex setCategoryLockById(Long categoryId);
	
	/**
	 * 根据Id获取商品分类分布锁
	 * @param categoryId 商品分类Id
	 */
	InterProcessMutex getCategoryLockById(Long categoryId);
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * @param categoryName 商品分类名称
	 */
	InterProcessMutex setCategoryLockByName(String categoryName);
	
	/**
	 * 根据名称获取商品分类分布锁
	 * @param categoryName 商品分类名称
	 */
	InterProcessMutex getCategoryLockByName(String categoryName);
}
