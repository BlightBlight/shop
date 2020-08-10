package com.shop.order.serviceImpl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.common.exception.DAOException;
import com.shop.common.utils.LockUtils;
import com.shop.order.dao.OrderDetailDao;
import com.shop.order.model.OrderDetail;
import com.shop.order.service.OrderDetailService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderDetailServiceImpl implements OrderDetailService{
	@Autowired
	OrderDetailDao orderDetailDao;
	
    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.orders}")
    private String lockScope;
    
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int saveOrderDetail(OrderDetail orderDetail) {
		int effectRow = 0;
		try {
			effectRow = orderDetailDao.saveOrderDetail(orderDetail);
			if (effectRow != 1) {
				throw new DAOException("插入order_detail表异常");
			}
		} catch(DataAccessException e) {
			log.error("插入order_detail表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("插入order_detail表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}

	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int updateOrderDetailCommentStatusById(Long orderDetailId, Boolean commentStatus) {
		int effectRow = 0;
		try {
			effectRow = orderDetailDao.updateOrderDetailCommentStatusById(orderDetailId, commentStatus);
			if (effectRow != 1) {
				log.error("修改order_detail表异常");
				throw new DAOException("修改order_detail表异常");
			}
		} catch(DataAccessException e) {
			log.error("修改order_detail表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改order_detail表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}

	@Override
	public OrderDetail getOrderDetailById(Long orderDetailId) {
		OrderDetail orderDetail = null;
		try {
			orderDetail = orderDetailDao.getOrderDetailById(orderDetailId);
		} catch(DataAccessException e) {
			log.error("查找order_detail表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("查找order_detail表异常");
			throw new DAOException(e);
		}
		return orderDetail;
	}
	
	@Override
	public OrderDetail getOrderDetailByOrderItemId(Long orderItemId) {
		OrderDetail orderDetail = null;
		try {
			orderDetail = orderDetailDao.getOrderDetailByOrderItemId(orderItemId);
		} catch(DataAccessException e) {
			log.error("查找order_detail表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("查找order_detail表异常");
			throw new DAOException(e);
		}
		return orderDetail;
	}
	
	@Override
	public InterProcessMutex setOrderDetailLockById(Long orderDetailId) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/orders/getOrderDetail/" + orderDetailId);
		if (LockUtils.getLock(lockScope, "/orders/getOrderDetail/" + orderDetailId) == null) {
			LockUtils.putLock(lockScope, "/orders/getOrderDetail/" + orderDetailId, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getOrderDetailLockById(Long orderDetailId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/orders/getOrderDetail/" + orderDetailId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/orders/getOrderDetail/" + orderDetailId);
			LockUtils.putLock(lockScope, "/orders/getOrderDetail/" + orderDetailId, lock);
		}
		return lock;
	}
}
