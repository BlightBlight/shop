package com.shop.coupon.controller;

import javax.validation.Valid;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.result.CommonResult;
import com.shop.coupon.model.Coupon;
import com.shop.coupon.service.CouponService;
import com.shop.coupon.vo.SaveCouponVo;
import com.shop.coupon.vo.UpdateCouponVo;
import com.shop.customer.service.CustomerService;

import lombok.extern.slf4j.Slf4j;

/**
 * 优惠券控制层
 */
@Controller
@RequestMapping("/coupons")
@Slf4j
public class CouponController {
	@Autowired
	CustomerService customerService;
	
	@Autowired
	CouponService couponService;
	
    @Autowired
    CuratorFramework client;
	
	private InterProcessMutex lock = null;
	
	/**
	 * 新增优惠券
	 * @param saveCouponVo
	 * @throws Exception
	 */
	@PostMapping()
	@ResponseBody
	public CommonResult<String> saveCoupon(@RequestBody @Valid SaveCouponVo saveCouponVo) throws Exception {
		return CommonResult.internalServerFailed();
	}
	
	/**
	 * 删除优惠券
	 * @throws Exception
	 */
	@DeleteMapping()
	@ResponseBody
	public CommonResult<String> removeCoupon(@RequestParam("couponId") Long couponId) throws Exception {
		return CommonResult.internalServerFailed();
	}
	
	/**
	 * 修改优惠券
	 * @throws Exception
	 */
	@PutMapping()
	@ResponseBody
	public CommonResult<String> updateCoupon(@RequestBody @Valid UpdateCouponVo updateCouponVo) throws Exception {
		return CommonResult.internalServerFailed();
	}
	
	/**
	 * 查找优惠券
	 * @throws Exception
	 */
	@PostMapping("/{couponId}")
	@ResponseBody
	public CommonResult<String> getCouponById(@RequestParam("couponId") Long couponId) throws Exception {
		return CommonResult.internalServerFailed();
	}
	
	/**
	 * 判断优惠券是否存在
	 */
	public CommonResult isCouponExists() {
		Coupon coupon = new Coupon();
		return CommonResult.success(coupon);
	}
}
