package com.shop.goods.controller;

import javax.validation.Valid;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.result.CommonResult;
import com.shop.common.utils.JacksonUtil;
import com.shop.common.utils.LockUtils;
import com.shop.goods.model.Category;
import com.shop.goods.service.CategoryService;
import com.shop.goods.vo.SaveCategoryVo;
import com.shop.goods.vo.UpdateCategoryVo;

import lombok.extern.slf4j.Slf4j;

/**
 * 商品分类控制层
 */
@Controller
@RequestMapping("/categorys")
@Slf4j
public class CategoryController {
	@Autowired
	CategoryService categoryService;
	
    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.categorys}")
    private String lockScope;
    
	/**
	 * 新增商品分类
	 * @throws Exception 
	 */
	@PostMapping()
	@ResponseBody
	public CommonResult<String> saveCategory(@RequestBody @Valid SaveCategoryVo saveCategoryVo) throws Exception {
		log.debug("待新增商品分类实体：" + saveCategoryVo.toString());
		
		InterProcessMutex lock = getCategoryLockByName(saveCategoryVo.getCategoryName());
		try {
			lock.acquire();
			
			@SuppressWarnings("rawtypes")
			CommonResult result = isCategoryExistsByName(saveCategoryVo.getCategoryName());
			if (result.getCode() == 200) {
				log.debug("该名称已有对应分类");
				return CommonResult.validateFailed("该名称已有对应分类");
			}
			
			categoryService.saveCategory(saveCategoryVo);
			log.info("成功新增商品分类");
			return CommonResult.success("成功新增商品分类");
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
	}
	
	/**
	 * 根据名称删除商品分类
	 * @param categoryId 分类Id
	 */
	@DeleteMapping("/{categoryId}")
	@ResponseBody
	public CommonResult<String> removeCategoryByName(@RequestBody String jsonObject) throws Exception {
		String categoryName = JacksonUtil.parseObject(jsonObject, String.class);
		log.debug("商品分类待删除：" + categoryName);
		/*
		 * 对商品分类名称锁住，分三种情况
		 * 1.找不到该商品分类，说明前端传输名称有问题，返回删除失败刷新重试提示。
		 * 2.找到该商品分类，且已删除，返回已删除提示。
		 * 3.找到该商品分类，无法删除，返回服务器内部错误提示。
		 * 所以这里要找所有的商品分类，包括删除和未删除的
		 * 或者直接返回未删除的商品分类就行，用户如果删除失败就统一返回删除失败刷新重试提示，看情况吧。
		 */
		InterProcessMutex lock = getCategoryLockByName(categoryName);
		try {
			lock.acquire();
			
			@SuppressWarnings("rawtypes")
			CommonResult result = isCategoryExistsByName(categoryName);
			if (result.getCode() != 200) {
				return result;
			}
			
			if (categoryService.removeCategoryByName(categoryName)) {
				log.info("成功删除商品分类：" + categoryName);
				return CommonResult.success("成功删除商品分类");
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
	 * 修改商品分类
	 * @param categoryVo
	 * @throws Exception 
	 */
	@PutMapping()
	@ResponseBody
	public CommonResult<String> updateCategory(@RequestBody @Valid UpdateCategoryVo updateCategoryVo) throws Exception {
		log.debug("待修改商品分类：" + updateCategoryVo.toString());
		InterProcessMutex lock = getCategoryLockByName(updateCategoryVo.getCategoryName());
		try {
			lock.acquire();
			
			@SuppressWarnings("rawtypes")
			CommonResult result = isCategoryExistsById(updateCategoryVo.getCategoryId());
			if (result.getCode() != 200) {
				return result;
			}
			Category category = (Category) result.getData();
			result = isCategoryExistsByName(updateCategoryVo.getCategoryName());
			//商品分类名称已存在且不是待修改商品分类原名称
			if (result.getCode() == 200 && (!category.getCategoryName().equals(updateCategoryVo.getCategoryName()))) {
				log.debug("该商品分类" + updateCategoryVo.getCategoryName() + "已存在");
				return CommonResult.failed("该商品分类" + updateCategoryVo.getCategoryName() + "已存在");
			}
			
			categoryService.updateCategory(updateCategoryVo);
			log.info("成功修改商品分类");
			return CommonResult.success("成功修改商品分类");
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
	}
	
	/**
	 * 根据Name查找商品分类
	 * @param categoryName 商品分类Name
	 * @return
	 */
	@GetMapping("/{categoryName}")
	@ResponseBody
	public CommonResult getCategoryByName(@PathVariable(value = "categoryName") String categoryName){
		//TODO:搜索业务放到Elasticsearch中
		return CommonResult.internalServerFailed();
	}
	
	@SuppressWarnings("rawtypes")
	public CommonResult isCategoryExistsById(Long categoryId) {
		Category category = categoryService.getCategoryById(categoryId);
		if (category == null) {
			log.debug("该Id找不到对应分类");
			return CommonResult.validateFailed("该Id找不到对应分类");
		}
		return CommonResult.success(category);
	}
	
	@SuppressWarnings("rawtypes")
	public CommonResult isCategoryExistsByName(String categoryName) {
		Category category = categoryService.getCategoryByName(categoryName);
		if (category == null) {
			log.debug("该名称找不到对应分类");
			return CommonResult.validateFailed("该名称找不到对应分类");
		}
		return CommonResult.success(category);
	}
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于需要对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param categoryName 商品分类名称
	 */
	public InterProcessMutex setCategoryLockByName(String categoryName) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/categorys/getCategory/" + categoryName);
		if (LockUtils.getLock(lockScope, "/categorys/getCategory/" + categoryName) == null) {
			LockUtils.putLock(lockScope, "/categorys/getCategory/" + categoryName, lock);
		}
		return lock;
	}
	
	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * @param categoryName 商品分类名称
	 */
	public InterProcessMutex getCategoryLockByName(String categoryName) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/categorys/getCategory/" + categoryName);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/categorys/getCategory/" + categoryName);
			LockUtils.putLock(lockScope, "/categorys/getCategory/" + categoryName, lock);
		}
		return lock;
	}
}
