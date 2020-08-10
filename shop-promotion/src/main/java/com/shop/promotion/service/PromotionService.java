package com.shop.promotion.service;

import com.shop.promotion.model.Promotion;
import com.shop.promotion.vo.SavePromotionVo;
import com.shop.promotion.vo.UpdatePromotionVo;

public interface PromotionService {
	/**
	 * 添加活动
	 */
	int savePromotion(SavePromotionVo savePromotionVo);
	
	/**
	 * 删除活动
	 * @param promotionId 活动Id
	 */
	int removePromotionById(long promotionId);
	
	/**
	 * 修改指定活动
	 */
	int updatePromotion(UpdatePromotionVo updatePromotionVo);
	
	/**
	 * 修改上下线状态
	 * @param promotionId 活动Id
	 * @param promotionStatus 0,未上线；1,已上线
	 */
	int updatePromotionStatusById(long promotionId, int promotionStatus);
	
	/**
	 * 获取活动信息
	 * @param promotionId 活动Id
	 */
	Promotion getPromotionById(long promotionId);
	
}
