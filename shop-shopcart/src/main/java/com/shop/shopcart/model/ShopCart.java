package com.shop.shopcart.model;

import java.io.Serializable;
import java.util.LinkedList;

import com.shop.shopcart.utils.ShopMap;

/**
 * 购物车实体类
 * 这个类是为了要存储shopId和CartItem之间的对应关系，不存在真正对应实体类
 * 对应实体类为CartItem
 */
public class ShopCart implements Serializable {
	private static final long serialVersionUID = 1L;
	/*
	 * LRU购物车列表，key为shopId，value为LinkedList<Long>，存储cartItemId
	 * 整体根据shopId使用LRU算法排序，也就是说最新放入购物车的那件商品的店铺最先显示，店铺商品按照插入顺序排序
	 * 有利于提高店铺整体销量（其实是因为也没有别的好方法了，总不能一件商品显示一个店铺吧，这样很蠢好吧）
	 * 内部LinkedList根据插入顺序排序，使用头插入
	 */
	private ShopMap<Long, LinkedList<Long>> shopCartList;
	
	public ShopMap<Long, LinkedList<Long>> getShopCartList() {
		return shopCartList;
	}
	public void setShopCartList(ShopMap<Long, LinkedList<Long>> shopCartList) {
		this.shopCartList = shopCartList;
	}
	@Override
	public String toString() {
		return "ShopCart [shopCartList=" + shopCartList + "]";
	}		
}
