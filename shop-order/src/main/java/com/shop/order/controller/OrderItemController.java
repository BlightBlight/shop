package com.shop.order.controller;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.result.CommonResult;
import com.shop.common.utils.JacksonUtil;
import com.shop.common.utils.LockUtils;
import com.shop.customer.model.Customer;
import com.shop.customer.service.CustomerService;
import com.shop.order.model.OrderItem;
import com.shop.order.service.OrderItemService;

import lombok.extern.slf4j.Slf4j;

/**
 * 子订单控制层
 */
@Controller
@RequestMapping("/orderItems")
@Slf4j
public class OrderItemController {
	@Autowired
	CustomerService customerService;
	
	@Autowired
	OrderItemService orderItemService;
	
    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.orders}")
    private String lockScope;
	
	/**
	 * 根据子订单Id删除子订单
	 * @param jsonData json序列化消息
	 * @throws Exception 
	 */
	@DeleteMapping()
	@ResponseBody
    public CommonResult<String> removeOrderItemById(@RequestBody String jsonData) throws Exception {
		Long orderItemId = JacksonUtil.parseObject(jsonData, Long.class);
    	Customer customer = customerService.getCurrentCustomer();
    	if (customer == null) {
    		log.debug("系统取不到用户");
    		//TODO:强制登出用户
    		return CommonResult.unauthorized("暂未登录或token已经过期");
    	}
		return removeOrderItemById(customer.getCustomerId(), orderItemId);
    }
	
	/**
	 * 根据子订单Id删除子订单
	 * @param orderItemId 子订单Id
	 * @throws Exception 
	 */
    public CommonResult<String> removeOrderItemById(Long customerId, Long orderItemId) throws Exception {
    	log.debug("待删除子订单Id：" + orderItemId);
    	
    	InterProcessMutex lock = getOrderItemLockById(orderItemId);
		try {
			lock.acquire();
			
	    	@SuppressWarnings("rawtypes")
			CommonResult result = isOrderItemExists(customerId, orderItemId);
	    	if (result.getCode() != 200) {
	    		return result;
	    	}
	    	OrderItem orderItem = (OrderItem) result.getData();
	    	if (orderItem.getDeleteStatus().equals(new Integer(3))) {
	    		log.debug("重复删除子订单");
	    		return CommonResult.failed("子订单已删除");
	    	}
			
	    	orderItemService.removeOrderItemById(orderItemId);
	    	log.info("成功删除子订单");
	    	return CommonResult.success("成功删除子订单");
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
	
	/**
	 * 根据子订单Id修改子订单订单状态
	 * @param orderItemId 子订单Id
	 * @param orderItemStatus 0,下单(create)；1,付款(pay)；2,卖家发货(deliver)；3,买家收货(receive)；4,退货(rereturn)
	 * @throws Exception 
	 */
	/*
	 * 这个方法不是给用户调用的！！！注意，也不能给用户调用，除非能确保前端输入orderItemStatus绝对准确
	 */
	@PutMapping("/updateOrderItemStatus")
	@ResponseBody
    public CommonResult<String> updateOrderItemStatusById(@RequestParam(value = "orderItemId") Long orderItemId,
    		@RequestParam(value = "orderItemStatus") Integer orderItemStatus) throws Exception {
    	log.debug("待修改子订单Id：" + orderItemId + "，修改状态为：" + orderItemStatus);
    	Customer customer = customerService.getCurrentCustomer();
    	if (customer == null) {
    		log.debug("系统取不到用户");
    		//TODO:强制登出用户
    		return CommonResult.unauthorized("暂未登录或token已经过期");
    	}
    	Long customerId = customer.getCustomerId();
    	
    	InterProcessMutex lock = getOrderItemLockById(orderItemId);
		try {
			lock.acquire();
			
	    	@SuppressWarnings("rawtypes")
			CommonResult result = isOrderItemExists(customerId, orderItemId);
	    	if (result.getCode() != 200) {
	    		return result;
	    	}
			
			orderItemService.updateOrderItemStatusById(orderItemId, orderItemStatus);
			log.info("成功修改子订单状态");
			return CommonResult.success("成功修改子订单状态");
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
	
	/**
	 * 根据子订单Id修改退款状态
	 * @param orderItemId 子订单Id
	 * @param refundStatus 0,未退款；1,退款中；2,部分退款；3,全退款
	 * @throws Exception 
	 */
	/*
	 * 这个方法不是给用户调用的！！！注意，也不能给用户调用，除非能确保前端输入refundStatus绝对准确
	 */
	@PutMapping("/updateOrderItemrefundStatus")
	@ResponseBody
    public CommonResult<String> updateOrderItemrefundStatusById(@RequestParam(value = "orderItemId") Long orderItemId,
    		@RequestParam(value = "refundStatus") Integer refundStatus) throws Exception {
    	log.debug("待退款子订单Id：" + orderItemId + "，修改退款状态为：" + refundStatus);
    	Customer customer = customerService.getCurrentCustomer();
    	if (customer == null) {
    		log.debug("系统取不到用户");
    		//TODO:强制登出用户
    		return CommonResult.unauthorized("暂未登录或token已经过期");
    	}
    	Long customerId = customer.getCustomerId();
    	
    	InterProcessMutex lock = getOrderItemLockById(orderItemId);
		try {
			lock.acquire();
			
	    	@SuppressWarnings("rawtypes")
			CommonResult result = isOrderItemExists(customerId, orderItemId);
	    	if (result.getCode() != 200) {
	    		return result;
	    	}
			
	    	orderItemService.updateOrderItemrefundStatusById(orderItemId, refundStatus);
	    	log.info("子订单修改退款状态成功");
	    	return CommonResult.success("子订单修改退款状态成功");
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
    
    /**
	 * 根据子订单Id查找子订单信息
	 */
	@GetMapping("/{orderItemId}")
	@ResponseBody
    public CommonResult getOrderItem(@RequestParam("orderItemId") Long orderItemId) {
    	log.debug("待查找子订单Id：" + orderItemId);
    	
    	OrderItem orderItem = orderItemService.getOrderItemById(orderItemId);
    	if (orderItem == null) {
    		log.info("找不到该子订单Id");
    		return CommonResult.validateFailed("找不到该子订单Id");
    	}
    	
    	return CommonResult.success(orderItem);
    }
	
	/**
	 * 根据主订单Id查找所有子订单信息
	 */
	@SuppressWarnings("rawtypes")
	@GetMapping("/{orderId}")
	@ResponseBody
    public CommonResult listOrderItem(@RequestParam("orderId") Long orderId) {
		log.debug("待查找主订单Id：" + orderId);
		
		List<OrderItem> orderItemList = orderItemService.listOrderItemByOrderId(orderId);
		return CommonResult.success(orderItemList);
    }
	
	/**
	 * 子订单是否存在
	 */
	@SuppressWarnings("rawtypes")
	public CommonResult isOrderItemExists(Long customerId, Long orderItemId) {
		OrderItem orderItem = orderItemService.getOrderItemById(orderItemId);
		if (customerId.equals(orderItem.getCustomerId())) {
			return CommonResult.success(orderItem);
		}
		return CommonResult.validateFailed("该子订单不存在");
	}
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于需要对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param orderItemId 子订单Id
	 */
	public InterProcessMutex setOrderItemLockById(Long orderItemId) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/orders/getOrderItem/" + orderItemId);
		if (LockUtils.getLock(lockScope, "/orders/getOrderItem/" + orderItemId) == null) {
			LockUtils.putLock(lockScope, "/orders/getOrderItem/" + orderItemId, lock);
		}
		return lock;
	}
	
	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * @param orderItemId 子订单Id
	 */
	public InterProcessMutex getOrderItemLockById(Long orderItemId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/orders/getOrderItem/" + orderItemId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/orders/getOrderItem/" + orderItemId);
			LockUtils.putLock(lockScope, "/orders/getOrderItem/" + orderItemId, lock);
		}
		return lock;
	}
}
