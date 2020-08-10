package com.shop.flashsale.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.result.CommonResult;
import com.shop.common.utils.LockUtils;
import com.shop.customer.model.Customer;
import com.shop.customer.service.CustomerService;
import com.shop.flashsale.model.FlashSaleGoods;
import com.shop.flashsale.service.FlashSaleGoodsService;
import com.shop.flashsale.service.FlashSaleService;
import com.shop.flashsale.vo.FlashSaleGoodsVo;
import com.shop.security.interceptor.AccessLimit;
import com.shop.security.interceptor.AccessRefreshBuffered;

import lombok.extern.slf4j.Slf4j;


/**
 * 秒杀功能控制层
 */
@Controller
@RequestMapping("/flashSales")
@Slf4j
public class FlashSaleController {
	@Autowired
	CustomerService customerService;
	
	@Autowired
	FlashSaleGoodsService flashSaleGoodsService;
	
	@Autowired
	FlashSaleService flashSaleService;
	
	@Autowired
	CuratorFramework client;

    @Value("${zookeeper.lockScope.flashsales}")
    private String lockScope;
	
	/**
	 * 刷新图片验证码
	 */
    @AccessRefreshBuffered(seconds = 60, maxRefresh = 5)
	@GetMapping(value ="/vertifyBufferImage/{flashSaleGoodsId}")
	@ResponseBody
	public CommonResult refreshVertifyBufferedImage(@PathVariable Long flashSaleGoodsId,
			HttpServletResponse response) {
		Customer customer = customerService.getCurrentCustomer();
		Long customerId = customer.getCustomerId();
		log.debug("用户" + customerId + "获取商品" + flashSaleGoodsId + "验证码");
		
		BufferedImage img = flashSaleService.refreshVertifyBufferedImage(customerId, flashSaleGoodsId);
		if (img == null) {
			log.debug("图片生成失败");
			return CommonResult.internalServerFailed();
		}
		try {
			OutputStream out = response.getOutputStream();
			ImageIO.write(img,"JPEG", out);
			out.flush();
			out.close();
			return CommonResult.success(img);
		} catch (IOException e) {
			e.printStackTrace();
			return CommonResult.internalServerFailed();
		}
	}
	
	/**
	 * 获取秒杀的path,并且验证验证码的值是否正确
	 */
	/*
	 * 加入注解，实现拦截功能，进而实现限流功能
	 * 为了避免用户直接访问下单页面URL，需要将URL动态化，即使秒杀系统的开发者也无法在秒杀开始前访问下单页面的URL
	 */
	@AccessLimit(seconds = 5, maxCount = 5)
	@RequestMapping(value = "/getPath")
	@ResponseBody
	public CommonResult getFlashSalePath(@RequestParam("flashSaleGoodsId") Long flashSaleGoodsId,
			@RequestParam(value = "vertifyCode", defaultValue = "0") String verifyCode,
			HttpServletRequest request) {
		Customer customer = customerService.getCurrentCustomer();
		Long customerId = customer.getCustomerId();
		// 验证验证码
		String dataBaseverifyCode = flashSaleService.getFlashSaleGoodsVerifyCodeById(customerId, flashSaleGoodsId);
		
		if (dataBaseverifyCode != verifyCode) {
			return CommonResult.validateFailed("验证码错误");
		}
		
		// 生成一个随机串
		flashSaleService.saveFlashSaleGoodsPath(flashSaleGoodsId, verifyCode);
		String path = flashSaleService.getFlashSaleGoodsPathById(flashSaleGoodsId);
		if (path == null) {
			log.debug("随机串生成出错");
			return CommonResult.internalServerFailed();
		}
		return CommonResult.success(path);
	}
	
	/**
	 * 秒杀操作
	 * @throws Exception 
	 */
	/*
	 * 先验证path是否一致
	 * 再验证flashSaleGoodsVo参数是否合法
	 * 再对flashSaleGoodsId上分布锁
	 * 检查redis中是否存在(customerId,order)，若存在，则直接返回已秒杀
	 * 再使用redis+Lua脚本原子减库存
	 * 将订单生成，写入redis、写入RabbitMQ
	 * 解锁
	 * 返回成功
	 */
	@PostMapping(value = "/{path}")
	@ResponseBody
	public CommonResult<String> toFlashSaleGoods(@RequestBody @Valid FlashSaleGoodsVo flashSaleGoodsVo,
			@PathVariable("path") String path) throws Exception {
		Customer customer = customerService.getCurrentCustomer();
		Long customerId = customer.getCustomerId();
		// 验证path,去redis里面取出来然后验证。
		String cachePath = flashSaleService.getFlashSaleGoodsPathById(flashSaleGoodsVo.getFlashSaleGoodsId());
		if (!cachePath.equals(path)) {
			log.debug("缓存秒杀路径：" + cachePath + "与传入秒杀路径：" + path + "不符");
			return CommonResult.validateFailed("秒杀地址有误");
		}
		
		// 验证页面传入数据是否正确
		@SuppressWarnings("rawtypes")
		CommonResult result = validationFlashSaleGoods(flashSaleGoodsVo.getFlashSaleGoodsId(), flashSaleGoodsVo.getShopId(), 
				flashSaleGoodsVo.getSpuId(), flashSaleGoodsVo.getSpecValueId(), flashSaleGoodsVo.getSpecValueName(), 
				flashSaleGoodsVo.getNumber(), flashSaleGoodsVo.getPrice());
		if (result.getCode() != 200) {
			return result;
		}
		
		InterProcessMutex lock = getCustomerLockById(customerId);
		try {
			lock.acquire();
			
			// 根据customerId和flashSaleGoodsId从缓存中取是否已秒杀标志
			boolean flag = flashSaleService.getCustomerFlagById(customerId, flashSaleGoodsVo.getFlashSaleGoodsId());
			if (flag) {
				log.debug("用户" + customerId + "重复秒杀");
				return CommonResult.failed("已秒杀成功，请勿重复秒杀");
			}
			
			flag = flashSaleGoodsService.decrFlashSaleGoodsById(flashSaleGoodsVo.getFlashSaleGoodsId(), flashSaleGoodsVo.getNumber());
			// 递减库存成功
			if (flag) {
				// 生成订单存入缓存
				flashSaleService.saveFlashSaleOrder(customerId, flashSaleGoodsVo);
			} else {
				return CommonResult.failed("秒杀失败，该商品已被秒杀完");
			}
			log.info("秒杀成功");
			return CommonResult.success("秒杀成功");
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
	
	@SuppressWarnings("rawtypes")
	public CommonResult validationFlashSaleGoods(Long flashSalegoodsId, Long shopId, Long spuId, Long specValueId, String flashSaleGoodsName, 
			Long number, BigDecimal price) {
		FlashSaleGoods flashSaleGoods = flashSaleGoodsService.getFlashSaleGoodsById(flashSalegoodsId);
		if (flashSaleGoods == null) {
			log.debug("根据页面传入flashSaleGoodsId：" + flashSalegoodsId + "找不到相应flashSaleGoods");
			return CommonResult.validateFailed("页面传入秒杀商品Id有误");
		}
		
		if (!flashSaleGoods.getShopId().equals(shopId)) {
			log.debug("页面传入shopId：" + shopId + "与缓存shopId：" + flashSaleGoods.getShopId() + "不符");
			return CommonResult.validateFailed("页面传入商家ID有误");
		}
		
		if (!flashSaleGoods.getSpuId().equals(spuId)) {
			log.debug("页面传入spuId：" + spuId + "与缓存spuId：" + flashSaleGoods.getSpuId() + "不符");
			return CommonResult.validateFailed("页面传入SPU商品ID有误");
		}
		
		if (!flashSaleGoods.getSpecValueId().equals(specValueId)) {
			log.debug("页面传入specValueId：" + specValueId + "与缓存specValueId：" + flashSaleGoods.getSpecValueId() + "不符");
			return CommonResult.validateFailed("页面传入商品规格值有误");
		}
		
		if (!flashSaleGoods.getFlashSaleGoodsName().equals(flashSaleGoodsName)) {
			log.debug("页面传入flashSaleGoodsName：" + flashSaleGoodsName + 
					"与缓存flashSaleGoodsName：" + flashSaleGoods.getFlashSaleGoodsName() + "不符");
			return CommonResult.validateFailed("页面传入秒杀商品名称有误");
		}
		
		if (flashSaleGoods.getStock().longValue() < number.longValue()) {
			log.debug("缓存stock：" + flashSaleGoods.getStock().longValue() + "小于购买数量：" + number.longValue());
			return CommonResult.failed("秒杀商品库存不足");
		}
		
		if (flashSaleGoods.getPrice().compareTo(price) != 0) {
			log.debug("页面传入price：" + price + "与缓存price：" + flashSaleGoods.getPrice() + "不符");
			return CommonResult.validateFailed("页面传入秒杀商品价格有误");
		}
		
		return CommonResult.success(flashSaleGoods);
	}
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于需要对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param customerId 用户Id
	 */
	public InterProcessMutex setCustomerLockById(Long customerId) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/flashSales/getCustomer/" + customerId);
		if (LockUtils.getLock(lockScope, "/flashSales/getCustomer/" + customerId) == null) {
			LockUtils.putLock(lockScope, "/flashSales/getCustomer/" + customerId, lock);
		}
		return lock;
	}
	
	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * @param customerId 用户Id
	 */
	public InterProcessMutex getCustomerLockById(Long customerId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/flashSales/getCustomer/" + customerId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/flashSales/getCustomer/" + customerId);
			LockUtils.putLock(lockScope, "/flashSales/getCustomer/" + customerId, lock);
		}
		return lock;
	}
}
