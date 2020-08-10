package com.shop.goods.controller;

import javax.validation.Valid;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.exception.DAOException;
import com.shop.common.result.CommonResult;
import com.shop.common.utils.JacksonUtil;
import com.shop.common.utils.LockUtils;
import com.shop.goods.model.Brand;
import com.shop.goods.service.BrandService;
import com.shop.goods.vo.SaveBrandVo;
import com.shop.goods.vo.UpdateBrandVo;

import lombok.extern.slf4j.Slf4j;

/**
 * 商品品牌控制层
 */
@Controller
@RequestMapping("/brands")
@Slf4j
public class BrandController {
	@Autowired
	BrandService brandService;
	
	@Autowired
	CategoryController categoryController;
	
    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.brands}")
    private String lockScope;
    
    /**
     * 新增商品品牌
     * @throws Exception
     */
	@PostMapping()
	@ResponseBody
	public CommonResult<String> saveBrand(@RequestBody @Valid SaveBrandVo saveBrandVo) throws Exception {
		log.debug("待新增商品品牌实体：" + saveBrandVo.toString());
		
		//是否存在categoryId
		@SuppressWarnings("rawtypes")
		CommonResult result = categoryController.isCategoryExistsById(saveBrandVo.getCategoryId());
		if (result.getCode() != 200) {
			return result;
		}
		
		InterProcessMutex lock = getBrandLockByName(saveBrandVo.getBrandName());
		try {
			lock.acquire();
			
			//是否存在brandName
			result = isBrandExistsByName(saveBrandVo.getBrandName());
			if (result.getCode() == 200) {
				log.debug("此商品品牌已存在:" + saveBrandVo.getBrandName());
				return CommonResult.failed("此商品品牌已存在");
			}
			
			brandService.saveBrand(saveBrandVo);
			log.info("成功新增商品品牌");
			return CommonResult.success("成功新增商品品牌");
		} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new DAOException(e);
		} finally {
			try {
				if (lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放写锁出现BUG");
			}
		}
	}
	
	/**
	 * 根据名称删除商品品牌
	 * @param brandName 品牌名称
	 */
	@DeleteMapping("/{brandId}")
	@ResponseBody
	public CommonResult<String> removeBrandByName(@RequestBody String jsonObject) throws Exception {
		String brandName = JacksonUtil.parseObject(jsonObject, String.class);
		log.debug("商品品牌待删除：" + brandName);
		
		InterProcessMutex lock = getBrandLockByName(brandName);
		try {
			lock.acquire();
			
			@SuppressWarnings("rawtypes")
			CommonResult result = isBrandExistsByName(brandName);
			if (result.getCode() != 200) {
				return result;
			}
			
			if (brandService.removeBrandByName(brandName)) {
				log.info("成功删除商品品牌：" + brandName);
				return CommonResult.success("成功删除商品品牌");
			}
		} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new Exception(e);
		} finally {
			try {
				if (lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
				throw new Exception(e);
			}
		}
		return CommonResult.internalServerFailed();
	}
	
	/**
	 * 修改商品品牌
	 */
	@PutMapping()
	@ResponseBody
	public CommonResult<String> updateBrand(@RequestBody @Valid UpdateBrandVo updateBrandVo) throws Exception {
		log.debug("待修改商品品牌实体：" + updateBrandVo.toString());
		
		InterProcessMutex lock = getBrandLockByName(updateBrandVo.getBrandName());
		try {
			lock.acquire();
			
			@SuppressWarnings("rawtypes")
			CommonResult result = isBrandExistsById(updateBrandVo.getBrandId());
			if (result.getCode() != 200) {
				return result;
			}
			Brand brand = (Brand) result.getData();
			
			result = isBrandExistsByName(updateBrandVo.getBrandName());
			//商品品牌名称数据库已有且不是待修改的品牌原名称
			if (result.getCode() == 200 && (!brand.getBrandName().equals(updateBrandVo.getBrandName()))) {
				log.debug("此商品品牌已存在:" + updateBrandVo.getBrandName());
				return CommonResult.failed("此商品品牌已存在");
			}
			
			brandService.updateBrand(updateBrandVo);
			log.info("成功修改商品品牌");
			return CommonResult.success("成功修改商品品牌");
		} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new DAOException(e);
		} finally {
			try {
				if (lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
			}
		}
	}
	
	/**
	 * 根据Id获取商品品牌是否存在
	 * @param brandId 商品品牌Id
	 */
	@SuppressWarnings("rawtypes")
	public CommonResult isBrandExistsById(Long brandId) {
		Brand brand = brandService.getBrandById(brandId);
		if (brand == null) {
			log.debug("此商品品牌不存在：" + brandId);
			return CommonResult.failed("此商品品牌不存在");
		}
		return CommonResult.success(brand);
	}
	
	/**
	 * 根据名称获取商品品牌是否存在
	 * @param brandName 商品品牌名称
	 */
	@SuppressWarnings("rawtypes")
	public CommonResult isBrandExistsByName(String brandName) {
		Brand brand = brandService.getBrandByName(brandName);
		if (brand == null) {
			log.debug("此商品品牌不存在：" + brandName);
			return CommonResult.failed("此商品品牌不存在");
		}
		return CommonResult.success(brand);
	}
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于需要对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param brandName 商品品牌名称
	 */
	public InterProcessMutex setBrandLockByName(String brandName) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/brands/getBrand/" + brandName);
		if (LockUtils.getLock(lockScope, "/brands/getBrand/" + brandName) == null) {
			LockUtils.putLock(lockScope, "/brands/getBrand/" + brandName, lock);
		}
		return lock;
	}
	
	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * @param brandName 商品品牌名称
	 */
	public InterProcessMutex getBrandLockByName(String brandName) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/brands/getBrand/" + brandName);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/brands/getBrand/" + brandName);
			LockUtils.putLock(lockScope, "/brands/getBrand/" + brandName, lock);
		}
		return lock;
	}
}
