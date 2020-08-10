package com.shop.shopcart.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shop.common.utils.RedisUtil;
import com.shop.shopcart.model.CartItem;
import com.shop.shopcart.model.ShopCart;
import com.shop.shopcart.service.ShopCartCacheService;

/**
 * 注意，shopCart不要用缓存！！！设计错了，对shopCart的读写太频繁，用缓存没意义。不过问题不大，到时候全部数据写到静态页面中
 * 将所有从缓存取的操作变成从静态页面取，所有写操作写到静态页面
 */
@Service
public class ShopCartCacheServiceImpl implements ShopCartCacheService  {
    @Autowired
    private RedisUtil redisUtil;
    
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    
    @Value("${redis.expire.cartItem}")
    private Long REDIS_EXPIRE;
    
    @Value("${redis.key.cartItem}")
    private String REDIS_KEY_CARTITEM;
	
    @Value("${redis.key.shopCart}")
    private String REDIS_KEY_SHOPCART;
    
	@Override
	public void setCartItemById(CartItem cartItem) {
		String key = REDIS_DATABASE + ":" + REDIS_KEY_CARTITEM + ":" + cartItem.getCartItemId();
		redisUtil.set(key, cartItem, REDIS_EXPIRE);
	}
    
	@Override
	public void setShopCartById(Long customerId, ShopCart shopCart) {
		String key = REDIS_DATABASE + ":" + REDIS_KEY_SHOPCART + ":" + customerId;
		redisUtil.set(key, shopCart, REDIS_EXPIRE);
	}
	
	@Override
	public void delCartItemById(Long cartItemId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CARTITEM + ":" + cartItemId;
        redisUtil.del(key);
	}
	
	@Override
	public void delShopCartById(Long customerId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SHOPCART + ":" + customerId;
        redisUtil.del(key);
	}
	
	@Override
	public CartItem getCartItemById(Long cartItemId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CARTITEM + ":" + cartItemId;
        return (CartItem) redisUtil.get(key);
	}
	
	@Override
	public ShopCart getShopCartById(Long customerId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SHOPCART + ":" + customerId;
        return (ShopCart) redisUtil.get(key);
	}
}
