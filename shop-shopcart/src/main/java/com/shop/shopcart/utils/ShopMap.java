package com.shop.shopcart.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 使用LinkedHashMap实现LRU淘汰
 * 注意，这里比较恶心的一点是队列头是最久未访问的，队列尾才是最近访问的！
 */
public class ShopMap<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = 1L;
	
	//这里是购物车的最大商品数量限制，暂且先定个50吧
	private final static int CART_SIZE = 99;
	 
	 public ShopMap() {
	 // true基于访问排序，false基于插入排序
	  super((int) (Math.ceil(CART_SIZE / 0.75) + 1), 0.75f, true);
	 }
	 
	 @Override
	 protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
	 // 当 map中的数据量大于指定的数量的时候，就自动删除最老的数据。
	  return size() > CART_SIZE;
	 }
}
