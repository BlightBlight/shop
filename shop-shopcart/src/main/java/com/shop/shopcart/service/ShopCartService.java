package com.shop.shopcart.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.shop.shopcart.model.CartItem;
import com.shop.shopcart.model.ShopCart;
import com.shop.shopcart.vo.SaveCartItemVo;

/**
 * 购物车Service层
 */
public interface ShopCartService {
	/**
	 * 新增cartItem
	 * @param customerId 用户Id
	 */
    int saveCartItem(Long customerId, SaveCartItemVo saveCartItemVo);
    
	/**
	 * 删除cartItem
	 * @param cartItemId 购物车选项Id
	 * @param customerId 用户Id
	 * @param shopId 商铺Id
	 */
    int removeCartItemById(Long cartItemId, Long customerId);
    
	/**
	 * 批量删除cartItem
	 * @param cartItemIdList 购物车选项Id列表
	 * @param customerId 用户Id
	 * @param shopCart 购物车
	 */
    int batchRemoveCartItem(List<Long> cartItemIdList, Long customerId);
    
	/**
	 * 修改cartItem数量
	 * @param cartItemId 商品Id
	 * @param number 商品数量
	 */
    int updateCartItemNumber(Long cartItemId, Long number);
    
	/**
	 * 修改cartItem规格值
	 * @param cartItemId 购物车选项Id
	 * @param skuId 商品Id
	 * @param price sku商品价格
	 * @param specValueId 规格值Id
	 * @param specValueName 规格值名称
	 */
    int updateCartItemSpecValue(Long cartItemId, Long skuId, BigDecimal price, Long specValueId, String specValueName);
    
	/**
	 * 根据Id查找cartItem
	 * @param customerId 用户Id
	 * @param skuId sku商品Id
	 */
    CartItem getCartItemById(Long cartItemId);
    
	/**
	 * 根据customerId和skuId查找cartItem
	 * @param customerId 用户Id
	 * @param skuId sku商品Id
	 */
    CartItem getCartItemByCustomerIdAndSKUId(Long cartItemId, Long skuId);
    
	/**
	 * 根据Id查找数据库购物车
	 * @param customerId 用户Id
	 */
    ShopCart getShopCartById(Long customerId);
    
	/**
	 * 通过customerId找出shopCart缓存，访问shopId使shopId处于队尾，将cartItemId头插入对应LinkedList
	 * @param cartItemId 购物车选项Id
	 * @param customerId 用户Id
	 * @param shopId 商铺Id
	 */
    void reFreshShopCart(Long cartItemId, Long customerId, Long shopId);
    
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param customerId 用户Id
	 */
    InterProcessMutex setShopCartLockById(Long customerId);
    
	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * @param customerId 用户Id
	 */
    InterProcessMutex getShopCartLockById(Long customerId);
    
	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * @param cartItemId 购物车选项Id
	 */
    InterProcessMutex getCartItemLockById(Long cartItemId);
}
