package com.shop.order.serviceImpl;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.common.exception.DAOException;
import com.shop.common.utils.LockUtils;
import com.shop.common.utils.SnowflakeIdUtil;
import com.shop.order.dao.OrderItemDao;
import com.shop.order.model.OrderItem;
import com.shop.order.service.OrderItemService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderItemServiceImpl implements OrderItemService{
	@Autowired
	OrderItemDao orderItemDao;
	
    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.orders}")
    private String lockScope;
	
	SnowflakeIdUtil sf = new SnowflakeIdUtil(0,0);	
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int saveOrderItem(OrderItem orderItem) {
		int effectRow = 0;
		try {
			effectRow = orderItemDao.saveOrderItem(orderItem);
			if (effectRow != 1) {
				throw new DAOException("插入order_item表异常");
			}
		} catch (DataAccessException e) {
			log.error("插入order_item表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("插入order_item表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
    public int removeOrderItemById(Long orderItemId) {
		int effectRow = 0;
		try {
			effectRow = orderItemDao.removeOrderItemById(orderItemId, new Integer(3));
			if (effectRow != 1) {
				throw new DAOException("修改order_item表异常");
			}
		} catch (DataAccessException e) {
			log.error("修改order_item表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改order_item表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int updateOrderItemStatusById(Long orderItemId, Integer orderItemStatus) {
		int effectRow = 0;
		try {
			effectRow = orderItemDao.updateOrderItemStatusById(orderItemId, orderItemStatus);
			if (effectRow != 1) {
				throw new DAOException("修改order_item表异常");
			}
		} catch (DataAccessException e) {
			log.error("修改order_item表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改order_item表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}
    
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int updateOrderItemrefundStatusById(Long orderItemId, Integer refundStatus) {
		int effectRow = 0;
		try {
			effectRow = orderItemDao.updateOrderItemrefundStatusById(orderItemId, refundStatus);
			if (effectRow != 1) {
				throw new DAOException("修改order_item表异常");
			}
		} catch (DataAccessException e) {
			log.error("修改order_item表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改order_item表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}
	
	@Override
	public OrderItem getOrderItemById(Long orderItemId) {
		OrderItem orderItem;
		try {
			orderItem = orderItemDao.getOrderItemById(orderItemId, 0);
		} catch (DataAccessException e) {
			log.error("查找order_item表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("查找order_item表异常");
			throw new DAOException(e);
		}
		return orderItem;
	}
	
	@Override
	public OrderItem isOrderItemExists(Long orderItemId) {
		OrderItem orderItem;
		try {
			orderItem = orderItemDao.isOrderItemExists(orderItemId);
		} catch (DataAccessException e) {
			log.error("查找order_item表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("查找order_item表异常");
			throw new DAOException(e);
		}
		return orderItem;
	}
	
	@Override
	public List<OrderItem> listOrderItemByOrderId(Long orderId) {
		List<OrderItem> orderItemList;
		try {
			orderItemList = orderItemDao.listOrderItemByOrderId(orderId, 0);
		} catch (DataAccessException e) {
			log.error("查找order_item表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("查找order_item表异常");
			throw new DAOException(e);
		}
		return orderItemList;
	}
	
	@Override
    public List<OrderItem> listOrderItemByCustomerId(Long customerId) {
		List<OrderItem> orderItemList;
		try {
			orderItemList = orderItemDao.listOrderItemByCustomerId(customerId, 0);
		} catch (DataAccessException e) {
			log.error("查找order_item表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("查找order_item表异常");
			throw new DAOException(e);
		}
		return orderItemList;
	}
	
	@Override
	public int sendDelayMessageCancelOrderItem(Long orderItemId) {
		return 0;
	}

	@Override
	public int cancelOrderItem(Long orderItemId) {
		return 0;
	}

	@Override
	public int cancelTimeOutOrderItem() {
		return 0;
	}

	@Override
	public int paySuccess(Long orderId, Integer payType) {
		return 0;
	}

	@Override
	public int confirmReceiveOrder(Long orderId) {
		return 0;
	}
	
	@Override
	public InterProcessMutex setOrderItemLockById(Long orderItemId) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/orders/getOrderItem/" + orderItemId);
		if (LockUtils.getLock(lockScope, "/orders/getOrderItem/" + orderItemId) == null) {
			LockUtils.putLock(lockScope, "/orders/getOrderItem/" + orderItemId, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getOrderItemLockById(Long orderItemId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/orders/getOrderItem/" + orderItemId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/orders/getOrderItem/" + orderItemId);
			LockUtils.putLock(lockScope, "/orders/getOrderItem/" + orderItemId, lock);
		}
		return lock;
	}
}
