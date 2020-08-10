package com.shop.shopcart.service;

import com.shop.shopcart.model.CartItem;
import com.shop.shopcart.model.ShopCart;

/**
 * 购物车缓存
 */
public interface ShopCartCacheService {
	/**
	 * 根据Id设置购物车选项缓存
	 */
	void setCartItemById(CartItem cartItem);
	
	/**
	 * 根据customerId设置购物车缓存
	 * @param customerId 用户Id
	 * @param shopCart 购物车
	 */
	void setShopCartById(Long customerId, ShopCart shopCart);
	
	/**
	 * 删除购物车选项缓存
	 * @param cartItemId 购物车选项Id
	 */
    void delCartItemById(Long cartItemId);
	
	/**
	 * 根据customerId删除购物车缓存
	 * @param customerId 用户Id
	 */
    void delShopCartById(Long customerId);

	/**
	 * 根据Id获取购物车选项缓存
	 * @param cartItemId 购物车选项Id
	 */
    CartItem getCartItemById(Long cartItemId);
    
	/**
	 * 根据customerId获取购物车缓存
	 * @param customerId 用户Id
	 */
    ShopCart getShopCartById(Long customerId);
    
}
