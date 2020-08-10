package com.shop.shopcart.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.common.exception.DAOException;
import com.shop.common.utils.LockUtils;
import com.shop.common.utils.SnowflakeIdUtil;
import com.shop.goods.service.SKUGoodsService;
import com.shop.shopcart.dao.ShopCartDao;
import com.shop.shopcart.model.CartItem;
import com.shop.shopcart.model.ShopCart;
import com.shop.shopcart.service.ShopCartCacheService;
import com.shop.shopcart.service.ShopCartService;
import com.shop.shopcart.utils.ShopMap;
import com.shop.shopcart.vo.SaveCartItemVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ShopCartServiceImpl implements ShopCartService{
	@Autowired
	SKUGoodsService skuGoodsService;
	
	@Autowired
	ShopCartCacheService shopCartCacheService;
	
	@Autowired
	ShopCartDao shopCartDao;
	
    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.shopcarts}")
    private String lockScope;
    
	SnowflakeIdUtil sf = new SnowflakeIdUtil(0,0);
    
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int saveCartItem(Long customerId, SaveCartItemVo saveCartItemVo) {
		CartItem cartItem = new CartItem();
		cartItem.setCartItemId(sf.nextId());
		cartItem.setCustomerId(customerId);
		cartItem.setShopId(saveCartItemVo.getShopId());
		cartItem.setSkuId(saveCartItemVo.getSkuId());
		cartItem.setNumber(saveCartItemVo.getNumber());
		cartItem.setPrice(saveCartItemVo.getPrice());
		cartItem.setSpecValueId(saveCartItemVo.getSpecValueId());
		cartItem.setSpecValueName(saveCartItemVo.getSpecValueName());
		cartItem.setCreateTime(saveCartItemVo.getCreateTime());
		cartItem.setSelectStatus(new Integer(1));
		cartItem.setDeleteStatus(new Integer(1));
		cartItem.setBuyStatus(new Integer(1));
		log.debug("存入customer_cartitem表实体为：" + cartItem.toString());
		
		int effectRow = 0;
		try {
			effectRow = shopCartDao.saveCartItem(cartItem);
			if (effectRow != 1) {
				throw new DAOException("插入customer_cartitem表异常");
			}
			log.debug("根据cartItemId：" + cartItem.getCartItemId() + "设置cartItem缓存" + cartItem.toString());
			shopCartCacheService.setCartItemById(cartItem);
			// 刷新购物车缓存
			reFreshShopCart(cartItem.getCartItemId(), customerId, cartItem.getShopId());
		} catch (DataAccessException e) {
			log.error("插入customer_cartitem表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("插入customer_cartitem表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int removeCartItemById(Long cartItemId, Long customerId) {
		int effectRow = 0;
		try {
			effectRow = shopCartDao.removeCartItemById(cartItemId, LocalDateTime.now());
			if (effectRow != 1) {
				throw new DAOException("修改customer_cartitem表异常");
			}
			
			log.debug("根据cartItemId：" + cartItemId + "删除cartItem缓存");
			shopCartCacheService.delCartItemById(cartItemId);
		} catch (DataAccessException e) {
			log.error("修改customer_cartitem表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("插入customer_cartitem表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}
	
	/**
	 * 批量删除cartItem
	 */
	@Override
	public int batchRemoveCartItem(List<Long> cartItemIdList, Long customerId) {
		return 0;
	}
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
    public int updateCartItemNumber(Long cartItemId, Long number) {
    	int effectRow = 0;
    	try {
    		effectRow = shopCartDao.updateCartItemNumberById(cartItemId, number, LocalDateTime.now());
			if (effectRow != 1) {
				throw new DAOException("修改customer_cartitem表异常");
			}
			log.debug("根据cartItemId：" + cartItemId + "删除cartItem缓存");
			shopCartCacheService.delCartItemById(cartItemId);
    	} catch (DataAccessException e) {
			log.error("修改customer_cartitem表异常");
			throw new DAOException(e);
    	} catch (DAOException e) {
			log.error("修改customer_cartitem表异常");
			throw new DAOException(e);
		}
    	return effectRow;
    }
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
    public int updateCartItemSpecValue(Long cartItemId, Long skuId, BigDecimal price, Long specValueId, String specValueName) {
    	int effectRow = 0;
    	try {
    		effectRow = shopCartDao.updateCartItemSpecValueById(cartItemId, skuId, price, specValueId, specValueName, LocalDateTime.now());
			if (effectRow != 1) {
				throw new DAOException("修改customer_cartitem表异常");
			}
			log.debug("根据cartItemId：" + cartItemId + "删除cartItem缓存");
			shopCartCacheService.delCartItemById(cartItemId);
    	} catch (DataAccessException e) {
			log.error("修改customer_cartitem表异常");
			throw new DAOException(e);
    	} catch (DAOException e) {
			log.error("修改customer_cartitem表异常");
			throw new DAOException(e);
		}
    	return effectRow;
    }
	
	@Override
	public CartItem getCartItemById(Long cartItemId) {
		CartItem cartItem = shopCartCacheService.getCartItemById(cartItemId);
		if (cartItem == null) {
			InterProcessMutex lock = getCartItemLockById(cartItemId);
			try {
				if (lock.acquire(5L, TimeUnit.SECONDS)) {
					cartItem = shopCartDao.getCartItemById(cartItemId, new Integer(0));
					if (cartItem != null) {
						log.debug("根据cartItemId：" + cartItemId + "查找到cartItem实体为：" + cartItem.toString());
						log.debug("根据cartItemId：" + cartItemId + "设置cartItem缓存" + cartItem.toString());
						shopCartCacheService.setCartItemById(cartItem);
						return cartItem;
					}
					return null;
				} else {
					cartItem = shopCartCacheService.getCartItemById(cartItemId);
					if (cartItem == null) {
						log.debug("重试");
						throw new DAOException("cartItem查找重试失败");
					}
					return cartItem;
				}
			} catch (DataAccessException e) {
				log.error("查找customer_cartitem表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error(Thread.currentThread() + "不明异常");
				throw new DAOException(e);
			} finally {
				try {
					if (lock.isOwnedByCurrentThread()) {
						lock.release();
					}
				} catch (Exception e) {
					log.error(Thread.currentThread() + "释放锁出现BUG");
				}
			}
		}
		return cartItem;
	}
	
	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public CartItem getCartItemByCustomerIdAndSKUId(Long cartItemId, Long skuId) {
		return null;
	}
	
	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public ShopCart getShopCartById(Long customerId) {
    	ShopCart shopCart = shopCartCacheService.getShopCartById(customerId);
    	if (shopCart == null) {
    		InterProcessMutex lock = getShopCartLockById(customerId);
			try {
				if (lock.acquire(5L, TimeUnit.SECONDS)) {
					List<CartItem> list = shopCartDao.listCartItemById(customerId);
			    	shopCart = new ShopCart();
			    	if (list != null) {
			    		ShopMap<Long, LinkedList<Long>> shopCartList = new ShopMap<Long, LinkedList<Long>>();
			    		LinkedList<Long> cartItemIdList = null;
			    		/*
			    		 * 遍历返回List中的数据
			    		 * 整体根据shopId进行排序，使用LRU队列
			    		 * 内部List使用LinkedList，由于一个店铺的商品数量不多
			    		 * 使用LinkedList效率高，同时支持头插入，顺序正确。
			    		 */
			    		CartItem cartItem = null;
			    		Iterator<CartItem> iterator = list.iterator();
			    		while (iterator.hasNext()) {
			    			cartItem = iterator.next();
			    			log.debug("根据cartItemId：" + cartItem.getCartItemId() + "查找到cartItem实体为：" + cartItem.toString());
			    			log.debug("根据cartItemId：" + cartItem.getCartItemId() + "设置cartItem缓存" + cartItem.toString());
			    			shopCartCacheService.setCartItemById(cartItem);
			    			
			    			// cartItemId放入LinkedList
			    			Long shopId = cartItem.getShopId();
			    			cartItemIdList = shopCartList.get(shopId);
			    			
			    			if (cartItemIdList == null) {
			    				cartItemIdList = new LinkedList<Long>();
			    				shopCartList.put(shopId, cartItemIdList);
			    			}
			    			cartItemIdList.addFirst(cartItem.getCartItemId());
			    		}
			    		shopCart.setShopCartList(shopCartList);
			    	}
			    	log.debug("根据customerId：" + customerId + "设置shopCart缓存" + shopCart.toString());
			    	shopCartCacheService.setShopCartById(customerId, shopCart);
			    	return shopCart;
				} else {
					shopCart = shopCartCacheService.getShopCartById(customerId);
					if (shopCart == null) {
						log.debug("重试");
						throw new DAOException("shopCart查找重试失败");
					}
					return shopCart;
				}
			} catch (DataAccessException e) {
				log.error("查找customer_cartitem表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error(Thread.currentThread() + "不明异常");
				throw new DAOException(e);
			} finally {
				try {
					if (lock.isOwnedByCurrentThread()) {
						lock.release();
					}
				} catch (Exception e) {
					log.error(Thread.currentThread() + "释放锁出现BUG");
					throw new DAOException(e);
				}
			}
    	}
    	return shopCart;
	}
	
	@Recover
	public int recover(DAOException e) {
		log.error("shopCart查找重试3次失败！！！");
		throw new DAOException(e);
	}
	
	@Override
	public void reFreshShopCart(Long cartItemId, Long customerId, Long shopId) {
		ShopCart shopCart = getShopCartById(customerId);
		ShopMap<Long, LinkedList<Long>> shopCartList = shopCart.getShopCartList();
		LinkedList<Long> cartItemIdList = shopCartList.get(shopId);
		if (cartItemIdList == null) {
			cartItemIdList = new LinkedList<Long>();
			shopCartList.put(shopId, cartItemIdList);
		}
		cartItemIdList.addFirst(cartItemId);
		log.debug("根据customerId：" + customerId + "设置shopCart缓存" + shopCart.toString());
		shopCartCacheService.setShopCartById(customerId, shopCart);
	}
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param customerId 用户Id
	 */
	@Override
	public InterProcessMutex setShopCartLockById(Long customerId) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/shopCarts/getShopCart/" + customerId);
		if (LockUtils.getLock(lockScope, "/shopCarts/getShopCart/" + customerId) == null) {
			LockUtils.putLock(lockScope, "/shopCarts/getShopCart/" + customerId, lock);
		}
		return lock;
	}
	
	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * @param customerId 用户Id
	 */
	@Override
	public InterProcessMutex getShopCartLockById(Long customerId) {
		InterProcessMutex lock =  LockUtils.getLock(lockScope, "/shopCarts/getShopCart/" + customerId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/shopCarts/getShopCart/" + customerId);
			LockUtils.putLock(lockScope, "/shopCarts/getShopCart/" + customerId, lock);
		}
		return lock;
	}
	
	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * @param cartItemId 购物车选项Id
	 */
	@Override
	public InterProcessMutex getCartItemLockById(Long cartItemId) {
		InterProcessMutex lock =  LockUtils.getLock(lockScope, "/shopCarts/getCartItem/" + cartItemId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/shopCarts/getCartItem/" + cartItemId);
			LockUtils.putLock(lockScope, "/shopCarts/getCartItem/" + cartItemId, lock);
		}
		return lock;
	}

}
