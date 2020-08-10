package com.shop.flashsale.serviceImpl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.common.exception.DAOException;
import com.shop.common.exception.ServiceException;
import com.shop.common.utils.JacksonUtil;
import com.shop.common.utils.LockUtils;
import com.shop.common.utils.MD5Util;
import com.shop.common.utils.SnowflakeIdUtil;
import com.shop.flashsale.model.FlashSaleMessage;
import com.shop.flashsale.service.FlashSaleCacheService;
import com.shop.flashsale.service.FlashSaleService;
import com.shop.flashsale.vo.FlashSaleGoodsVo;
import com.shop.order.model.Order;
import com.shop.order.model.OrderDetail;
import com.shop.order.model.OrderItem;
import com.shop.order.mq.OrderSender;
import com.shop.order.service.OrderCacheService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FlashSaleServiceImpl implements FlashSaleService {
	@Autowired
	FlashSaleCacheService flashSaleCacheService;
	
	@Autowired
	OrderCacheService orderCacheService;
	
	@Autowired
	OrderSender orderSender;
	
    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.flashsales}")
    private String lockScope;
	
	SnowflakeIdUtil sf = new SnowflakeIdUtil(0,0);	
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public void saveFlashSaleGoodsPath(Long flashSaleGoodsId, String verifyCode) {
		/*
		 * 这里路径的生成需要结合秒杀商品Id与验证码
		 * 关键是要验证码，验证码需要由用户输入，所以URL地址的安全取决于验证码的安全
		 * 验证码作为随机盐值，插入到秒杀商品Id中
		 */
		String path = MD5Util.formPassToDBPass(String.valueOf(flashSaleGoodsId), verifyCode);
		log.debug("根据flashSaleGoodsId：" + flashSaleGoodsId + "设置path：" + path + "缓存");
		flashSaleCacheService.setFlashSaleGoodsPath(flashSaleGoodsId, path);
	}

	@Override
	public BufferedImage refreshVertifyBufferedImage(Long customerId, Long flashSaleGoodsId) {
		int width = 80;
		int height = 30;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(new Color(0xDCDCDC));
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width - 1, height - 1);
		Random rdm = new Random();
		for (int i = 0; i < 50; i++) {
			int x = rdm.nextInt(width);
			int y = rdm.nextInt(height);
			g.drawOval(x, y, 0, 0);
		}
		// 生成验证码
		String vertifyCode = createVertifyCode(rdm);
		g.setColor(new Color(0, 100, 0));
		g.setFont(new Font("Candara", Font.BOLD, 24));
		// 将验证码写在图片上
		g.drawString(vertifyCode, 8, 24);
		g.dispose();
		// 计算存值
		int result = calc(vertifyCode);
		// 将计算结果保存到redis上面去
		log.debug("根据customerId：" + customerId + ",flashSaleGoodsId："+ flashSaleGoodsId + "设置result：" + result + "缓存");
		flashSaleCacheService.setFlashSaleGoodsVerifyCode(customerId, flashSaleGoodsId, String.valueOf(result));
		return img;
	}
	
	private static char[]ops = new char[] {'+','-','*'};
	/**
	 * + - *
	 */
	private String createVertifyCode(Random rdm) {
		// 生成10以内的
		int n1 = rdm.nextInt(10);
		int n2 = rdm.nextInt(10);
		int n3 = rdm.nextInt(10);
		char op1 = ops[rdm.nextInt(3)];// 0 1 2
		char op2 = ops[rdm.nextInt(3)];// 0 1 2
		String exp = "" + n1 + op1 + n2 + op2 + n3;
		return exp;
	}
	
	private static int calc(String exp) {
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			return (Integer) engine.eval(exp);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	@Transactional(rollbackFor = {ServiceException.class, DAOException.class})
	public void saveFlashSaleOrder(Long customerId, FlashSaleGoodsVo flashSaleGoodsVo) {
		// order实体
		Order order = new Order();
		order.setOrderId(sf.nextId());
		order.setCustomerId(customerId);
		order.setCreateTime(LocalDateTime.now());
		Long number = flashSaleGoodsVo.getNumber();
		order.setOrderNumber(number);
		BigDecimal totalPrice = new BigDecimal(number).multiply(flashSaleGoodsVo.getPrice());
		order.setOrderPrice(totalPrice);
		
		// orderItem实体
		List<OrderItem> orderItemList = new LinkedList<OrderItem>();
		OrderItem orderItem = new OrderItem();
		orderItem.setOrderItemId(sf.nextId());
		orderItem.setOrderId(order.getOrderId());
		orderItem.setOrderItemNumber(order.getOrderNumber());
		orderItem.setOrderItemPrice(order.getOrderPrice());
		orderItem.setCreateTime(order.getCreateTime());
		
		// orderDetail实体
		List<OrderDetail> orderDetailList = new LinkedList<OrderDetail>();
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setOrderDetailId(sf.nextId());
		orderDetail.setOrderItemId(orderItem.getOrderItemId());
		orderDetail.setShopId(flashSaleGoodsVo.getShopId());
		orderDetail.setSkuId(flashSaleGoodsVo.getFlashSaleGoodsId());
		orderDetail.setSkuName(flashSaleGoodsVo.getFlashSaleGoodsName());
		orderDetail.setShopName(flashSaleGoodsVo.getShopName());
		orderDetail.setNumber(orderItem.getOrderItemNumber());
		orderDetail.setPrice(orderItem.getOrderItemPrice());
		orderDetail.setSpecValueId(flashSaleGoodsVo.getSpecValueId());
		orderDetail.setSpecValueName(flashSaleGoodsVo.getSpecValueName());
		orderDetail.setCreateTime(orderItem.getCreateTime());
		log.debug("存入order_detail表实体" + orderDetail.toString());
		
		orderDetailList.add(orderDetail);
		orderItem.setOrderDetailList(orderDetailList);
		log.debug("存入order_item表实体" + orderItem.toString());
		
		orderItemList.add(orderItem);
		order.setOrderItemList(orderItemList);
		log.debug("存入order表实体" + order.toString());
		
		// 订单写入redis，秒杀成功标志写入redis
		log.debug("根据customerId：" + customerId + "设置order缓存" + order.toString());
		log.debug("根据customerId：" + customerId + ",flashSaleGoodsId：" + flashSaleGoodsVo.getFlashSaleGoodsId() + "设置已秒杀缓存");
		try {
			flashSaleCacheService.setFlashSaleOrderById(order);
			flashSaleCacheService.setCustomerFlagById(customerId, flashSaleGoodsVo.getFlashSaleGoodsId());
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
		try {
			// 订单写入RabbitMQ
			orderSender.sendOrder(String.valueOf(flashSaleGoodsVo.getFlashSaleGoodsId()), JacksonUtil.toJSON(order));
		} catch (Exception e) {
			log.debug("消息队列发送失败");
			try {
				// 删除订单缓存，用户已秒杀缓存
				flashSaleCacheService.delFlashSaleOrderById(customerId);
				flashSaleCacheService.delCustomerFlagById(customerId, flashSaleGoodsVo.getFlashSaleGoodsId());
			} catch (Exception e1) {
				throw new ServiceException(e1);
			}
			throw new ServiceException(e);
		}
	}
	
	@Override
	public void removeFlashSaleGoodsPathById(Long flashSaleGoodsId) {
		flashSaleCacheService.delFlashSaleGoodsPathById(flashSaleGoodsId);
	}

	@Override
	public void removeFlashSaleGoodsVerifyCodeById(Long customerId, Long flashSaleGoodsId) {
		flashSaleCacheService.delFlashSaleGoodsVerifyCodeById(customerId, flashSaleGoodsId);
	}
	
	@Override
	public void removeFlashSaleOrderById(Long customerId) {
		flashSaleCacheService.delFlashSaleOrderById(customerId);
	}
	
	@Override
	public String getFlashSaleGoodsPathById(Long flashSaleGoodsId) {
		String path = flashSaleCacheService.getFlashSaleGoodsPathById(flashSaleGoodsId);

		return path;
	}

	@Override
	public String getFlashSaleGoodsVerifyCodeById(Long customerId, Long flashSaleGoodsId) {
		String verifyCode = flashSaleCacheService.getFlashSaleGoodsVerifyCodeById(customerId, flashSaleGoodsId);
		return verifyCode;
	}
	
	@Override
	public 	Order getFlashSaleOrderById(Long customerId) {
		Order order = flashSaleCacheService.getFlashSaleOrderById(customerId);
		return order;
	}
	
	@Override
	public  boolean getCustomerFlagById(Long customerId, Long flashSaleGoodsId) {
		return flashSaleCacheService.getCustomerFlagById(customerId, flashSaleGoodsId);
	}
	
	@Override
	public InterProcessMutex setFlashSaleGoodsLockById(Long flashSaleGoodsId) {
		InterProcessMutex lock = new InterProcessMutex(client, "/flashSales/getFlashSaleGood/" + flashSaleGoodsId);
		if (LockUtils.getLock(lockScope, "/flashSales/getFlashSaleGood/" + flashSaleGoodsId) == null) {
			LockUtils.putLock(lockScope, "/flashSales/getFlashSaleGood/" + flashSaleGoodsId, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getFlashSaleGoodsLockById(Long flashSaleGoodsId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/flashSales/getFlashSaleGood/" + flashSaleGoodsId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/flashSales/getFlashSaleGood/" + flashSaleGoodsId);
			LockUtils.putLock(lockScope, "/flashSales/getFlashSaleGood/" + flashSaleGoodsId, lock);
		}
		return lock;
	}
}
