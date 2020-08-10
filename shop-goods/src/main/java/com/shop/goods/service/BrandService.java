package com.shop.goods.service;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.shop.goods.model.Brand;
import com.shop.goods.vo.SaveBrandVo;
import com.shop.goods.vo.UpdateBrandVo;

/**
 * 商品品牌接口层
 */
public interface BrandService {
	/**
	 * 创建商品品牌
	 */
	int saveBrand(SaveBrandVo saveBrandVo);

	/**
	 * 根据Id删除商品品牌
	 * @param brandName 商品品牌名称
	 */
	boolean removeBrandByName(String brandName);
	
	/**
	 * 根据Id获取商品品牌
	 * @param brandName 商品品牌名称
	 */
	int updateBrand(UpdateBrandVo updateBrandVo);
	
	/**
	 * 根据名称获取商品品牌
	 */
	Brand getBrandByName(String brandName);
	
	/**
	 * 根据Id获取商品品牌
	 */
	Brand getBrandById(Long brandId);
	
	/**
	 * 根据名称获取商品品牌分布锁
	 * @param brandName 品牌名称
	 */
	InterProcessMutex getBrandLockByName(String brandName);
	
	/**
	 * 根据Id获取商品品牌分布锁
	 * @param brandId 品牌Id
	 */
	InterProcessMutex getBrandLockById(Long brandId);
}
