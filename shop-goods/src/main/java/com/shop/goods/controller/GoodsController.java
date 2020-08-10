package com.shop.goods.controller;

import javax.validation.Valid;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.result.CommonResult;
import com.shop.goods.service.GoodsService;
import com.shop.goods.service.SKUGoodsService;
import com.shop.goods.service.SPUGoodsService;
import com.shop.goods.vo.SaveGoodsVo;
import com.shop.goods.vo.SaveSKUGoodsVo;

import lombok.extern.slf4j.Slf4j;

/**
 * 商品控制层
 */
@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {
    @Autowired
    SPUGoodsController spuGoodsController;
    
    @Autowired
    SKUGoodsController skuGoodsController;
	
	@Autowired
    GoodsService goodsService;
    
    @Autowired
    SPUGoodsService spuGoodsService;
    
    @Autowired
    SKUGoodsService skuGoodsService;
    
    @Autowired
    CuratorFramework client;
	
	/**
	 * 新增商品
	 * @throws Exception 
	 */
    @PostMapping()
    @ResponseBody
    public CommonResult<String> saveGoods(@RequestBody @Valid SaveGoodsVo saveGoodsVo) throws Exception {
    	log.debug("待创建商品Vo：" + saveGoodsVo);
    		
		for (SaveSKUGoodsVo saveSKUGoodsVo : saveGoodsVo.getSkuList()) {
    		/*
    		 * 验证SPU商品是否已存在
    		 */
			@SuppressWarnings("rawtypes")
			CommonResult result = spuGoodsController.isSPUGoodsExistsById(saveSKUGoodsVo.getSpuId());
			if (result.getCode() != 200) {
				return result;
			}
			
			InterProcessMutex lock = skuGoodsController.getSKUGoodsLockByName(saveSKUGoodsVo.getSkuName());
			try {
				lock.acquire();
				
				result = skuGoodsController.isSKUGoodsExistsByName(saveSKUGoodsVo.getSkuName());
				if (result.getCode() != 200) {
					return result;
				}

				// 新增商品
				if (goodsService.saveGoods(saveGoodsVo)) {
					log.info("创建商品成功，待审核");
					return CommonResult.success("创建商品成功，待审核");
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "出BUG了");
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
    	return CommonResult.internalServerFailed();
	}
}
