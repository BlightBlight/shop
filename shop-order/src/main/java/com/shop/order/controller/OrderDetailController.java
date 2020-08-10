package com.shop.order.controller;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.result.CommonResult;
import com.shop.common.utils.LockUtils;
import com.shop.customer.service.CustomerService;
import com.shop.order.model.OrderDetail;
import com.shop.order.service.OrderDetailService;

import lombok.extern.slf4j.Slf4j;

/**
 * 订单详情控制层
 */
@Controller
@RequestMapping("/orderDetails")
@Slf4j
public class OrderDetailController {
	@Autowired
	CustomerService customerService;
	
	@Autowired
	OrderDetailService orderDetailService;
	
    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.orders}")
    private String lockScope;
	
	/**
	 * 根据订单详情Id修改订单详情评论状态
	 * @throws Exception 
	 */
	@PutMapping("/updateOrderDetailsCommentStatus")
	@ResponseBody
    public CommonResult<Object> updateOrderDetailCommentStatusById(@RequestParam(value = "orderItemId") Long orderItemId,
    		@RequestParam(value = "orderDetailId") Long orderDetailId,
    		@RequestParam(value = "commentStatus") Integer commentStatus) throws Exception {
    	log.debug("待修改子订单Id：" + orderDetailId + "，评论状态为：" + commentStatus);
    	
    	InterProcessMutex lock = getOrderDetailLockById(orderDetailId);
		try {
			lock.acquire();
			
	    	CommonResult<Object> result = isOrderDetailExists(orderItemId, orderDetailId);
	    	if (result.getCode() != 200) {
	    		log.debug(result.getMessage());
	    		return result;
	    	}
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
    	return CommonResult.internalServerFailed();
    	
    }
    
    /**
	 * 根据订单详情Id获取订单详情
	 */
	@GetMapping("/{orderDetailId}")
	@ResponseBody
    public CommonResult<Object> getOrderDetailById(@PathVariable Long orderDetailId) {
    	log.debug("待查找订单详情Id：" + orderDetailId);
    	
    	OrderDetail orderDetail = orderDetailService.getOrderDetailById(orderDetailId);
    	if(orderDetail == null) {
    		log.info("该订单详情Id找不到相应订单详情");
    		return CommonResult.validateFailed("该订单详情Id找不到相应订单详情");
    	}
    	
    	return CommonResult.success(orderDetail);
	}
	
    /**
	 * 根据子订单Id获取所有订单详情
	 */
	@GetMapping("/{orderItemId}")
	@ResponseBody
    public CommonResult<Object> getOrderDetailByOrderItemId(@PathVariable Long orderItemId) {
    	log.debug("待查找子订单Id：" + orderItemId);
    	
    	OrderDetail orderDetail = orderDetailService.getOrderDetailByOrderItemId(orderItemId);
    	
    	return CommonResult.success(orderDetail);
	}
	
	public CommonResult<Object> isOrderDetailExists(Long orderItemId, Long orderDetailId) {
		OrderDetail orderDetail = orderDetailService.getOrderDetailById(orderDetailId);
		if (orderItemId == orderDetail.getOrderItemId()) {
			return CommonResult.success(orderDetail);
		}
		return CommonResult.validateFailed("该订单详情不存在");
	}
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于需要对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param orderDetailId 订单详情Id
	 */
	public InterProcessMutex setOrderDetailLockById(Long orderDetailId) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/orders/getOrderDetail/" + orderDetailId);
		if (LockUtils.getLock(lockScope, "/orders/getOrderDetail/" + orderDetailId) == null) {
			LockUtils.putLock(lockScope, "/orders/getOrderDetail/" + orderDetailId, lock);
		}
		return lock;
	}
	
	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * @param orderDetailId 订单详情Id
	 */
	public InterProcessMutex getOrderDetailLockById(Long orderDetailId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/orders/getOrderDetail/" + orderDetailId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/orders/getOrderDetail/" + orderDetailId);
			LockUtils.putLock(lockScope, "/orders/getOrderDetail/" + orderDetailId, lock);
		}
		return lock;
	}
}
