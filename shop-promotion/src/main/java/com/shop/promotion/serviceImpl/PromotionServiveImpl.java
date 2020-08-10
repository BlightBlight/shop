package com.shop.promotion.serviceImpl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.common.exception.DAOException;
import com.shop.common.utils.SnowflakeIdUtil;
import com.shop.promotion.dao.PromotionDao;
import com.shop.promotion.model.Promotion;
import com.shop.promotion.service.PromotionService;
import com.shop.promotion.vo.SavePromotionVo;
import com.shop.promotion.vo.UpdatePromotionVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PromotionServiveImpl implements PromotionService {
	@Autowired
	PromotionDao promotionDao;
	
	@Autowired
	CuratorFramework client;

	private InterProcessMutex lock = null;
	
	SnowflakeIdUtil sf = new SnowflakeIdUtil(0,0);
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int savePromotion(SavePromotionVo savePromotionVo) {
		Promotion promotion = new Promotion();
		promotion.setPromotionId(sf.nextId());
		promotion.setAdminId(123L);
		promotion.setPromotionName(savePromotionVo.getPromotionName());
		promotion.setCreateTime(savePromotionVo.getCreateTime());
		promotion.setStartTime(savePromotionVo.getStartTime());
		promotion.setEndTime(savePromotionVo.getEndTime());
		log.debug("新增promotion实体：" + promotion.toString());
		int effectRow = 0;
		try {
			effectRow = promotionDao.savePromotion(promotion);
		} catch (DataAccessException e) {
			log.error("插入活动表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}

	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int removePromotionById(long promotionId) {
		int effectRow = 0;
		try {
			effectRow = promotionDao.removeCategoryById(promotionId);
		} catch (DataAccessException e) {
			log.error("修改活动表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}

	@Override
	public int updatePromotion(UpdatePromotionVo updatePromotionVo) {
		Promotion promotion = new Promotion();
		promotion.setPromotionId(updatePromotionVo.getPromotionId());
		promotion.setAdminId(updatePromotionVo.getAdminId());
		promotion.setUpdateTime(updatePromotionVo.getUpdateTime());
		promotion.setStartTime(updatePromotionVo.getStartTime());
		promotion.setEndTime(updatePromotionVo.getEndTime());
		log.debug("修改promotion实体为：" + promotion.toString());
		int effectRow = 0;
		try {
			effectRow = promotionDao.updatePromotion(promotion);
		} catch (DataAccessException e) {
			log.error("修改活动表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}

	@Override
	public int updatePromotionStatusById(long promotionId, int promotionStatus) {
		int effectRow = 0;
		try {
			effectRow = promotionDao.updatePromotionStatusById(promotionId, promotionStatus);
		} catch (DataAccessException e) {
			log.error("修改活动表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}

	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public Promotion getPromotionById(long promotionId) {
		Promotion promotion = promotionDao.getPromotionById(promotionId, 0);
		return promotion;
	}
	
	@Recover
	public int recover(DAOException e) {
		log.error("活动查找重试3次失败！！！");
		throw new DAOException(e);
	}

}
