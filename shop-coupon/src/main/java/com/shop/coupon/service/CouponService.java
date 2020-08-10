package com.shop.coupon.service;

import com.shop.coupon.model.Coupon;
import com.shop.coupon.vo.SaveCouponVo;
import com.shop.coupon.vo.UpdateCouponVo;

/**
 * 优惠券接口层
 */
public interface CouponService {
	/**
	 * 创建优惠券
	 */
	int saveCoupon(SaveCouponVo saveCouponVo);

	/**
	 * 根据Id删除优惠券
	 */
	int removeCouponById(long couponId);
	
	/**
	 * 修改优惠券
	 */
	int updateCoupon(UpdateCouponVo updateCouponVo);
	
	/**
	 * 根据Id获取优惠券
	 */
	Coupon getCouponById(long couponId);
}
