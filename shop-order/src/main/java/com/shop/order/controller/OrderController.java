package com.shop.order.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.shop.order.model.Order;
import com.shop.order.model.OrderDetail;
import com.shop.order.model.OrderItem;
import com.shop.order.service.OrderCacheService;
import com.shop.order.service.OrderService;
import com.shop.order.vo.SaveOrderVo;
import com.shop.shopcart.controller.ShopCartController;
import com.shop.shopcart.model.CartItem;
import com.shop.shopcart.service.ShopCartService;

import lombok.extern.slf4j.Slf4j;

/**
 * 主订单控制层
 */
@Controller
@RequestMapping("/orders")
@Slf4j
public class OrderController {
	@Autowired
	ShopCartController shopCartController;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	ShopCartService shopCartService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	OrderCacheService orderCacheService;
	
    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.orders}")
    private String lockScope;
    
	/**
	 * 新增订单
	 * @throws Exception  
	 */
	/*
	 * 新增订单步骤：检查购物车商品、插入主订单，插入子订单，插入订单详情，修改购物车表，删除购物车缓存
	 * 检查商品调用相应检测方法，若有变则直接返回
	 * 其余都要事务支持，使用同一个事务，一个出错，全部回滚。
	 * 可以优化的点：前三个步骤为一个事务，出错回滚
	 * 后两个步骤使用单独事务或者放到队列中进行，出错重试
	 * 假设后两个步骤就真的出错了，无法挽回呢？那就是变成了购物车中还有这一次订单的商品，没有删除
	 * 对于用户体验不好，购买了的商品还在购物车中，可能造成重复购买。
	 */
	@PostMapping()
	@ResponseBody
	public CommonResult<String> saveOrder(@RequestBody @Valid SaveOrderVo saveOrderVo) throws Exception {
		log.debug("待新增订单Vo：" + saveOrderVo.toString());
		
		/*
		 * 这里有两种处理方式
		 * 1.shopCartController已经通过购物车项验证了
		 * 所以认为此线程已经取得了SKUGoods，比起后面的修改线程要更快，所以可以继续进行
		 * 可能出现这种情况，线程1下单，验证通过，中断；线程2修改，修改完成，写入数据库
		 * 则线程1下单的SKU与线程2已修改的SKU数据不同
		 * 方案：当SKU验证完毕后，若SKU改变，支付失败；若SKU无改变，给SKU拍一个快照，保存当时信息
		 * 2.在这里再给SKU、SPU上一个分布锁，确保SKU商品、SPU商品数据绝对一致性
		 * 每个SKU、SPU商品单独上锁还是所有SKU、SPU一起上锁呢？
		 * 每个SKU单独上锁，则依然会存在上面问题，A验证通过，B再修改，则A与B数据不一致，同样解决方案，无意义
		 * 所有SKU一起上锁，则要不先购买完毕，要不先修改完毕
		 * 问题是随着用户购买商品数量越多，所有上锁商品购买速度越慢
		 * 同时，商品购买时间取决于购买商品数量最多的用户
		 * 舍弃了redis的优势，系统性能瓶颈在ZooKeeper上锁速度
		 * 归根到底，也就是绝对线程安全与相对线程安全的问题，这里还是选择相对安全就好
		 */
		Customer customer = customerService.getCurrentCustomer();
		Long customerId = customer.getCustomerId();
		
		InterProcessMutex lock = getOrderLockById(customerId);
		try {
			lock.acquire();
			
			@SuppressWarnings("rawtypes")
			CommonResult result = validationOrder(customerId, saveOrderVo.getOrderItemList(),
					saveOrderVo.getOrderPrice(), saveOrderVo.getOrderNumber());
			if (result.getCode() != 200) {
				return result;
			}
	    	if (orderService.saveOrder(customerId, saveOrderVo)) {
	    		log.info("成功创建订单");
	    		//TODO:修改购物车表和购物车缓存放到RabbitMQ中异步改
	    		return CommonResult.success("成功创建订单");
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
	 * 删除主订单
	 */
	@DeleteMapping("/{orderId}")
	@ResponseBody
	public CommonResult<String> removeOrder(@PathVariable Long orderId) {
		log.debug("待删除主订单Id：" + orderId);
		
		return CommonResult.success("成功删除订单");
	}
	
	/**
	 * 根据主订单Id查找主订单
	 */
	@GetMapping("/{orderId}")
	@ResponseBody
	public CommonResult getOrder(@RequestParam("orderId") Long orderId) {
		log.debug("待查找主订单Id：" + orderId);
		
		Order order = orderService.getOrderById(orderId);
		if (order == null) {
			log.info("查找主订单查询异常：");
			return CommonResult.validateFailed("找不到对应主订单");
		}
		return CommonResult.success(order);
	}
	
	/**
	 * 根据用户Id查找所有主订单
	 */
	@GetMapping("/{customerId}")
	@ResponseBody
	public CommonResult listOrder(@RequestParam("customerId") Long customerId) {
		log.debug("待查找用户Id：" + customerId);
		
		List<Order> orderList = orderService.listOrderById(customerId);
		if (orderList == null) {
			log.info("查找用户主订单查询异常：");
			return CommonResult.validateFailed("找不到对应用户订单");
		}
		
		return CommonResult.success(orderList);
	}
	
	@SuppressWarnings("rawtypes")
	public CommonResult validationOrder(Long customerId, List<OrderItem> orderItemList, BigDecimal orderPrice, Long orderNumber) {
		//用户应付总价
		BigDecimal tempOrderPrice = new BigDecimal(0);
		//用户总购买商品数量
		Long tempOrderNumber = new Long(0);
		
		for (OrderItem orderItem : orderItemList) {
			//每一子订单总价
			BigDecimal orderItemPrice = new BigDecimal(0);
			Long orderItemNumber = Long.valueOf(orderItemList.size());
			/*
			 * 未来还要算上运费、优惠等，使用公式计算
			 * tempTotalPrice = a * (b * x) - c + y，a指折扣，b指商品个数，c指直接减的价格，x指原价，y指运费
			 * a、b、c、x、y全部逐一读取
			 */
			CommonResult result = null;
			for (OrderDetail orderDetail : orderItem.getOrderDetailList()) {
				BigDecimal orderDetailPrice = new BigDecimal(0);
				// 根据skuId查找购物车是否存在相应商品
				result = shopCartController.isCartItemExistsFromShopCartBySKUId(customerId, orderDetail.getShopId(), orderDetail.getSkuId());
				CartItem cartItem = null;
				if (result.getCode() != 200) {
					// 若为空，则可能购物车缓存已刷新，从数据库中找
					cartItem = shopCartService.getCartItemByCustomerIdAndSKUId(customerId, orderDetail.getSkuId());
					if (cartItem == null) {
						return result;
					}
				} else {
					cartItem = (CartItem) result.getData();
				}
				//验证orderItem参数是否合法
				result = shopCartController.validationCartItem(cartItem, orderDetail.getShopId(), orderDetail.getSkuId(),
						orderDetail.getNumber(), orderDetail.getPrice(), orderDetail.getSpecValueId());
				if (result.getCode() != 200) {
					//任何一个SKU商品修改了，都直接返回购买失败，这样用户体验好吗？不知道，我也没试过
					return result;
				}
				//TODO:这里要拍一个快照，将CartItem对应的SKU、SPU信息保留下来，保存信息；或者先拍，通过了就不删除
				
				BigDecimal sale = new BigDecimal(1);	//折扣设为1
				BigDecimal number = new BigDecimal(orderDetail.getNumber());	//sku商品数量
				BigDecimal price = orderDetail.getPrice();	//sku价格
				BigDecimal discount = new BigDecimal(0);	//直接折扣
				BigDecimal freight = new BigDecimal(0);	//运费
				orderDetailPrice = orderDetailPrice.add(sale.multiply(number).multiply(price).subtract(discount).add(freight));
				/*
				 * 验证订单详情总价
				 * 这里如果是不一致的，说明页面传入参数要不就是给恶意篡改，要不就是不明BUG
				 */
				if (orderDetail.getOrderDetailPrice().compareTo(orderDetailPrice) != 0) {
					log.debug("订单详情总价出BUG");
					return CommonResult.internalServerFailed();
				}
				orderItemPrice = orderItemPrice.add(orderDetailPrice);
			}
			
			/*
			 * 验证子订单总价
			 * 这里如果是不一致的，说明页面传入参数要不就是给恶意篡改，要不就是不明BUG
			 */
			if (orderItem.getOrderItemPrice().compareTo(orderItemPrice) != 0) {
				log.debug("子订单总价出BUG");
				return CommonResult.internalServerFailed();
			}
			
			if (!orderItem.getOrderItemNumber().equals(orderItemNumber)) {
				log.debug("子订单总商品数出BUG");
				return CommonResult.internalServerFailed();
			}
			tempOrderPrice = tempOrderPrice.add(orderItemPrice);
			tempOrderNumber += orderItemNumber;
		}
		/*
		 * 验证主订单总价
		 * 这里如果是不一致的，说明页面传入参数要不就是给恶意篡改，要不就是不明BUG
		 */
		if (orderPrice.compareTo(tempOrderPrice) != 0) {
			log.debug("主订单总价出BUG");
			return CommonResult.internalServerFailed();
		}

		if (!orderNumber.equals(tempOrderNumber)) {
			log.debug("主订单总商品数出BUG");
			return CommonResult.internalServerFailed();
		}
		return CommonResult.success("冇问题");
	}
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于需要对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param customerId 用户Id
	 */
	public InterProcessMutex setOrderLockById(Long customerId) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/orders/getOrder/" + customerId);
		if (LockUtils.getLock(lockScope, "/orders/getOrder/" + customerId) == null) {
			LockUtils.putLock(lockScope, "/orders/getOrder/" + customerId, lock);
		}
		return lock;
	}
	
	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * @param customerId 用户Id
	 */
	public InterProcessMutex getOrderLockById(Long customerId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/orders/getOrder/" + customerId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/orders/getOrder/" + customerId);
			LockUtils.putLock(lockScope, "/orders/getOrder/" + customerId, lock);
		}
		return lock;
	}
}
