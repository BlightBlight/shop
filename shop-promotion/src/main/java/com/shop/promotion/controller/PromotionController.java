package com.shop.promotion.controller;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.result.CommonResult;
import com.shop.promotion.model.Promotion;
import com.shop.promotion.service.PromotionService;
import com.shop.promotion.vo.SavePromotionVo;
import com.shop.promotion.vo.UpdatePromotionVo;

import lombok.extern.slf4j.Slf4j;

/**
 * 活动控制层
 */
@Controller
@RequestMapping("/promotions")
@Slf4j
public class PromotionController {
	@Autowired
	PromotionService promotionService;
	
    @Autowired
    CuratorFramework client;
	
	private InterProcessMutex lock = null;
	
	/**
	 * 新增活动
	 */
	@PostMapping()
	@ResponseBody
	public CommonResult<String> savePromotion(SavePromotionVo savePromotionVo) {
		//获取Admin验证Id
		log.debug("新增活动Vo为：" + savePromotionVo.toString() + "管理员：");
		//验证前端传入参数
		int effectRow = promotionService.savePromotion(savePromotionVo);
		if (effectRow == 0) {
			log.info("数据库插入失败");
			return CommonResult.internalServerFailed();
		} else if (effectRow == 1) {
			log.info("成功新增活动");
			return CommonResult.success("成功新增活动");
		}
		return CommonResult.internalServerFailed();
	}
	
	/**
	 * 删除活动
	 */
	@DeleteMapping()
	@ResponseBody
	public CommonResult<String> removePromotion(long adminId, long promotionId) {
		//获取Admin验证Id
		log.debug("删除活动：" + promotionId + "管理员：" + adminId);
		//验证前端传入参数
		int effectRow = promotionService.removePromotionById(promotionId);
		if (effectRow == 0) {
			log.info("数据库删除失败");
			return CommonResult.internalServerFailed();
		} else if (effectRow == 1) {
			log.info("成功删除活动");
			return CommonResult.success("成功删除活动");
		}
		return CommonResult.internalServerFailed();
	}
	
	/**
	 * 修改活动
	 */
	@PutMapping()
	@ResponseBody
	public CommonResult<String> updatePromotion(UpdatePromotionVo updatePromotionVo) {
		//获取Admin验证Id
		log.debug("修改活动Vo：" + updatePromotionVo + "管理员：");
		//验证前端传入参数
		int effectRow = promotionService.updatePromotion(updatePromotionVo);
		if (effectRow == 0) {
			log.info("数据库修改失败");
			return CommonResult.internalServerFailed();
		} else if (effectRow == 1) {
			log.info("成功修改活动");
			return CommonResult.success("成功修改活动");
		}
		return CommonResult.internalServerFailed();
	}
	
	/**
	 * 查找活动
	 */
	@GetMapping()
	@ResponseBody
	public CommonResult getPromotionById(long promotionId) {
		//验证前端传入参数
		
		Promotion promotion = promotionService.getPromotionById(promotionId);
		return CommonResult.success(promotion);
	}
}
