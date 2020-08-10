package com.shop.order.serviceImpl;

import java.util.LinkedList;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.common.exception.DAOException;
import com.shop.common.utils.JacksonUtil;
import com.shop.common.utils.LockUtils;
import com.shop.common.utils.SnowflakeIdUtil;
import com.shop.goods.service.SKUGoodsService;
import com.shop.order.dao.OrderDao;
import com.shop.order.model.Order;
import com.shop.order.model.OrderDetail;
import com.shop.order.model.OrderItem;
import com.shop.order.mq.OrderSender;
import com.shop.order.service.OrderDetailService;
import com.shop.order.service.OrderItemService;
import com.shop.order.service.OrderService;
import com.shop.order.vo.SaveOrderVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService{
	@Autowired
	OrderItemService orderItemService;
	
	@Autowired
	OrderDetailService orderDetailService;
	
	@Autowired
	SKUGoodsService skuGoodsService;
	
	@Autowired
	OrderSender orderSender;
	
	@Autowired
	OrderDao orderDao;
	
    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.orders}")
    private String lockScope;
	
	SnowflakeIdUtil sf = new SnowflakeIdUtil(0,0);	
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public boolean saveOrder(Long customerId, SaveOrderVo saveOrderVo) {
		Order order = new Order();
		order.setOrderId(sf.nextId());
		order.setCustomerId(customerId);
		order.setOrderNumber(saveOrderVo.getOrderNumber());
		order.setOrderPrice(saveOrderVo.getOrderPrice());
		order.setCreateTime(saveOrderVo.getCreateTime());
		order.setDeleteStatus(new Integer(1));
		
		return saveOrder(order);
	}
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public boolean saveOrder(Order order) {
		log.debug("存入order_superior表实体为：" + order.toString());
		
		int effectRow = 0;
		for (OrderItem orderItem : order.getOrderItemList()) {
			log.debug("存入order_item表实体为：" + orderItem.toString());
			
			// 对子订单的每一个订单详情进行插入
			List<OrderDetail> orderDetailList = orderItem.getOrderDetailList();
			for (OrderDetail orderDetail : orderDetailList) {
				log.debug("存入order_detail表实体为：" + orderDetail.toString());
				try {
					effectRow = orderDetailService.saveOrderDetail(orderDetail);
					if (effectRow != 1) {
						log.error("插入order_detail表异常");
						throw new DAOException("插入order_detail表异常");
					}
					// 修改商品缓存
					skuGoodsService.decrSKUGoodsByName(orderDetail.getSkuName(), orderDetail.getNumber());
				} catch (DataAccessException e) {
					log.error("插入order_detail表异常");
					throw new DAOException(e);
				} catch (DAOException e) {
					log.error("插入order_detail表异常");
					throw new DAOException(e);
				}
			}
			try {
				effectRow = orderItemService.saveOrderItem(orderItem);
				if (effectRow != 1) {
					throw new DAOException("插入order_item表异常");
				}
			} catch (DataAccessException e) {
				//TODO:将SKU递减的库存再递增回去
				log.error("插入order_item表异常" );
				throw new DAOException(e);
			} catch (DAOException e) {
				//TODO:将SKU递减的库存再递增回去
				log.error("插入order_item表异常" );
				throw new DAOException(e);
			}
		}
		
		try {
			effectRow = orderDao.saveOrder(order);
			if (effectRow != 1) {
				throw new DAOException("插入order表异常");
			}
			
			try {
				orderSender.sendLazyOrder(String.valueOf(order.getOrderId()), JacksonUtil.toJSON(order), 30000);
			} catch (Exception e) {
				//TODO:将SKU递减的库存再递增回去
				log.error("消息发送失败RabbitMQ队列");
				throw new DAOException(e);
			}
		} catch (DataAccessException e) {
			//TODO:将SKU递减的库存再递增回去
			log.error("插入order表异常" );
			throw new DAOException(e);
		} catch (DAOException e) {
			//TODO:将SKU递减的库存再递增回去
			log.error("插入order表异常" );
			throw new DAOException(e);
		}
		return true;
	}
	
	@Override
	public int removeOrderById(Long orderId) {
		return 0;
	}

	@Override
	public int updateOrderStatus(Long orderId, Integer orderStatus) {
		return 0;
	}

	@Override
	public Order getOrderById(Long orderId) {
		Order order;
		try {
			order = orderDao.getOrderById(orderId);
		} catch (DataAccessException e) {
			log.error("查找主订单查询异常：" + e);
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("查找order表异常");
			throw new DAOException(e);
		}
		return order;
	}

	@Override
	public List<Order> listOrderById(Long customerId) {
		List<Order> orderList = new LinkedList<Order>();
		try {
			orderList = listOrderById(customerId);
		} catch(DataAccessException e) {
			log.error("查找order表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("查找order表异常");
			throw new DAOException(e);
		}
		return orderList;
	}
	
	@Override
	public InterProcessMutex setOrderLockById(Long customerId) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/orders/getOrder/" + customerId);
		if (LockUtils.getLock(lockScope, "/orders/getOrder/" + customerId) == null) {
			LockUtils.putLock(lockScope, "/orders/getOrder/" + customerId, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getOrderLockById(Long customerId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/orders/getOrder/" + customerId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/orders/getOrder/" + customerId);
			LockUtils.putLock(lockScope, "/orders/getOrder/" + customerId, lock);
		}
		return lock;
	}
}
