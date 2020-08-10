package com.shop.portal.controller;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.KeeperException.SessionExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.utils.JacksonUtil;
import com.shop.common.utils.LockUtils;
import com.shop.common.utils.RedisUtil;
import com.shop.customer.model.Customer;
import com.shop.customer.service.CustomerService;
import com.shop.goods.model.SKUGoods;
import com.shop.goods.model.SPUGoods;
import com.shop.goods.service.SKUGoodsCacheService;
import com.shop.goods.service.SPUGoodsCacheService;
import com.shop.order.model.Order;
import com.shop.order.model.OrderDetail;
import com.shop.order.model.OrderItem;
import com.shop.order.mq.OrderSender;
import com.shop.portal.model.A;

import lombok.extern.slf4j.Slf4j;

@Controller()
@Slf4j
public class MainController {
	@Autowired
	CustomerService customerService;
	
	@Autowired
	SPUGoodsCacheService spuGoodsCacheService;
	
	@Autowired
	OrderSender orderSender;
	
	@Autowired
	RedisUtil redisUtil;
	
    @Autowired
    CuratorFramework client;
	
	@GetMapping(value = "/home")
	public String home() {
		return "home";
	}
	
	@GetMapping(value = "/mobileLogin")
	public String mobileLogin() {
		return "mobileLogin";
	}
	
	@PostMapping(value = "/test")
	@ResponseBody
	//@Async("threadPoolTaskExecutor")
	public String test() throws Exception {
		Order order = new Order();
		order.setOrderId(1234567890123456789L);
		order.setCustomerId(1234567890123456789L);
		
		List<OrderItem> orderItemList = new LinkedList<OrderItem>();
		OrderItem orderItem = new OrderItem();
		orderItem.setCustomerId(1234567890123456789L);
		orderItem.setOrderItemId(1234567890123456781L);
		
		List<OrderDetail> orderDetailList = new LinkedList<OrderDetail>();
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setOrderDetailId(1234567890123456789L);
		
		orderDetailList.add(orderDetail);
		orderItem.setOrderDetailList(orderDetailList);
		
		orderItemList.add(orderItem);
		order.setOrderItemList(orderItemList);
		
		orderSender.sendLazyOrder(String.valueOf(order.getOrderId()), JacksonUtil.toJSON(order), 3000);
		//orderSender.sendOrder(String.valueOf(order.getOrderId()), JacksonUtil.toJSON(order));
		return "A";
	}
	
	public InterProcessMutex getLock(String mobileNum) {
		InterProcessMutex lock = LockUtils.getLock("customers", "/test");
		if (lock == null) {
			lock =  new InterProcessMutex(client, "/test");
			LockUtils.putLock("customers", "/test", lock);
		}
		return lock;
	}
}
