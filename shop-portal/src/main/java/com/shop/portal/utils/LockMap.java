package com.shop.portal.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * 使用LinkedHashMap实现LRU淘汰
 * 注意，这里比较恶心的一点是队列头是最久未访问的，队列尾才是最近访问的！
 */
public class LockMap<K, V> extends LinkedHashMap<K, V>{
	private static final long serialVersionUID = 1L;
	
	private static LockMap<String, InterProcessMutex> lockMap = new LockMap<String, InterProcessMutex>();
	
	//这里是分布锁的最大限制，暂且先定个500吧
	private final static int LOCK_SIZE = 500;
	 
	 public LockMap(){
	 // true基于访问排序，false基于插入排序
	  super((int) (Math.ceil(LOCK_SIZE / 0.75) + 1), 0.75f, true);
	 }
	 
	 @Override
	 protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
	 // 当 map中的数据量大于指定的数量的时候，就自动删除最老的数据。
	  return size() > LOCK_SIZE;
	 }
	 
	 public static void put(String lockPath, InterProcessMutex lock) {
		 lockMap.put(lockPath, lock);
	 }
	 
	 /**
	  * 获取相应路径的分布锁
	  * @param lockPath 路径
	  */
	 public static InterProcessMutex get(String lockPath) {
		 return lockMap.get(lockPath);
	 }
}
