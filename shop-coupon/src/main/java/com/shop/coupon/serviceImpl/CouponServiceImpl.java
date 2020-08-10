package com.shop.coupon.serviceImpl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.common.exception.DAOException;
import com.shop.common.utils.SnowflakeIdUtil;
import com.shop.coupon.dao.CouponDao;
import com.shop.coupon.model.Coupon;
import com.shop.coupon.service.CouponService;
import com.shop.coupon.vo.SaveCouponVo;
import com.shop.coupon.vo.UpdateCouponVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CouponServiceImpl implements CouponService {
	//@Autowired
	//CouponDao couponDao;
	
	@Autowired
	CuratorFramework client;
	
	private InterProcessMutex lock = null;
	
	SnowflakeIdUtil sf = new SnowflakeIdUtil(0,0);
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int saveCoupon(SaveCouponVo saveCouponVo) {
		Coupon coupon = new Coupon();
		int effectRow = 0;
		try {
			//couponDao.saveCoupon(coupon);
		} catch (DataAccessException e) {
			log.debug("新增数据库异常");
			throw new DAOException(e);
		}
		log.debug("新增coupon实体为：" + coupon.toString());
		return effectRow;
	}

	@Override
	public int removeCouponById(long couponId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int updateCoupon(UpdateCouponVo updateCouponVo) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Coupon getCouponById(long couponId) {
		// TODO Auto-generated method stub
		return null;
	}
}
