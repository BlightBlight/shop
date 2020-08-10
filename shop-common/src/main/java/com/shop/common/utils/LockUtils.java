package com.shop.common.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * 存储所有分布锁
 * 单独机器存放，一个机器备份
 * 所有分布锁到机器中查找
 */
public class LockUtils {
	private static LockUtils lockUtils = new LockUtils();
	
	//key:工作路径，value：分布锁Map
	private static HashMap<String, LockMap<String, InterProcessMutex>> map = new HashMap<String, LockMap<String, InterProcessMutex>>();
	
	private static HashMap<String, Integer> lockSizeMap = new HashMap<String, Integer>();
	//用户分布锁最大限制
	private static int CUSTOMER_LOCK_SIZE = 500;
	//商品分类分布锁最大限制
	private static int CATEGORY_LOCK_SIZE = 500;
	//商品品牌分布锁最大限制
	private static int BRAND_LOCK_SIZE = 500;
	//SPU商品分布锁最大限制
	private static int SPUGOODS_LOCK_SIZE = 500;
	//SKU商品分布锁最大限制
	private static int SKUGOODS_LOCK_SIZE = 500;
	//购物车分布锁最大限制
	private static int SHOPCART_LOCK_SIZE = 500;
	//订单分布锁最大限制
	private static int ORDER_LOCK_SIZE = 500;
	//秒杀商品分布锁最大限制
	private static int FLASHSALES_LOCK_SIZE = 500;
	
	// 暂时先这样存放着
	static {
		lockSizeMap.put("customers", CUSTOMER_LOCK_SIZE);
		lockSizeMap.put("categorys", CATEGORY_LOCK_SIZE);
		lockSizeMap.put("brands", BRAND_LOCK_SIZE);
		lockSizeMap.put("spuGoods", SPUGOODS_LOCK_SIZE);
		lockSizeMap.put("skuGoods", SKUGOODS_LOCK_SIZE);
		lockSizeMap.put("shopcarts", SHOPCART_LOCK_SIZE);
		lockSizeMap.put("orders", ORDER_LOCK_SIZE);
		lockSizeMap.put("flashsales", FLASHSALES_LOCK_SIZE);
	}
	
	/**
	 * LRU队列，清除长时间不用的分布锁
	 */
	class LockMap<K, V> extends LinkedHashMap<K, V> {
		private static final long serialVersionUID = 1L;
		
		private int LOCK_SIZE;
		
		private LockMap(int lockSize) {
			// true基于访问排序，false基于插入排序
			super((int) (Math.ceil(lockSize / 0.75) + 1), 0.75f, true);
			LOCK_SIZE = lockSize;
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			// 当 map中的数据量大于指定的数量的时候，就自动删除最老的数据。
			return size() > LOCK_SIZE;
		}
	}
	
	/**
	 * 根据作用域查找分布锁表
	 * @param scope 作用域
	 */
	public LockMap<String, InterProcessMutex> getLockMap(String scope) {
		LockMap<String, InterProcessMutex> lockMap = map.get(scope);
		if (lockMap == null) {
			int lockSize = lockSizeMap.get(scope);
			lockMap = new LockMap<String, InterProcessMutex>(lockSize);
			map.put(scope, lockMap);
		}
		return lockMap;
	}
	
	/**
	 * 根据作用域、路径设置分布锁
	 * @param scope    作用域
	 * @param lockPath 路径
	 * @param lock 分布锁
	 */
	public static void putLock(String scope, String lockPath, InterProcessMutex lock) {
		LockMap<String, InterProcessMutex> lockMap = lockUtils.getLockMap(scope);
		lockMap.put(lockPath, lock);
	}

	/**
	 * 获取相应路径的分布锁
	 * @param scope    作用域
	 * @param lockPath 路径
	 */
	public static InterProcessMutex getLock(String scope, String lockPath) {
		LockMap<String, InterProcessMutex> lockMap = lockUtils.getLockMap(scope);
		return lockMap.get(lockPath);
	}
}
