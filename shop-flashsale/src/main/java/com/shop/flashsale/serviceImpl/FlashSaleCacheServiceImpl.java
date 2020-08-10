package com.shop.flashsale.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shop.common.utils.RedisUtil;
import com.shop.flashsale.service.FlashSaleCacheService;
import com.shop.order.model.Order;

@Service
public class FlashSaleCacheServiceImpl implements FlashSaleCacheService {
	@Autowired
	RedisUtil redisUtil;
	
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    
    @Value("${redis.expire.flashSaleGoodsPath}")
    private Long REDIS_FLASHSALEGOODSPATH_EXPIRE;
    
    @Value("${redis.expire.flashSaleGoodsVerifyCode}")
    private Long REDIS_FLASHSALEGOODSVERIFYCODE_EXPIRE;
    
    @Value("${redis.expire.flashSaleOrder}")
    private Long REDIS_FLASHSALEORDER_EXPIRE;
    
    @Value("${redis.key.flashSaleGoodsPath}")
    private String REDIS_KEY_FLASHSALEGOODSPATH;
    
    @Value("${redis.key.flashSaleGoodsVerifyCode}")
    private String REDIS_KEY_FLASHSALEGOODSVERIFYCODE;
    
    @Value("${redis.key.flashSaleOrder}")
    private String REDIS_KEY_FLASHSALEORDER;
    
    @Value("${redis.key.flashSaleCustomer}")
    private String REDIS_KEY_FLASHSALECUSTOMER;
    
	@Override
	public void setFlashSaleGoodsPath(Long flashSaleGoodsId, String url) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODSPATH + ":" + flashSaleGoodsId;
        redisUtil.set(key, url, REDIS_FLASHSALEGOODSPATH_EXPIRE);
	}

	@Override
	public void delFlashSaleGoodsPathById(Long customerId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODSPATH + ":" + customerId;
        redisUtil.del(key);
	}

	@Override
	public String getFlashSaleGoodsPathById(Long customerId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODSPATH + ":" + customerId;
        return (String) redisUtil.get(key);
	}

	@Override
	public void setFlashSaleGoodsVerifyCode(Long customerId, Long flashSaleGoodsId, String verifyCode) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODSVERIFYCODE + ":" + customerId + flashSaleGoodsId;
        redisUtil.set(key, verifyCode, REDIS_FLASHSALEGOODSVERIFYCODE_EXPIRE);
	}

	@Override
	public void delFlashSaleGoodsVerifyCodeById(Long customerId, Long flashSaleGoodsId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODSVERIFYCODE + ":" + customerId + flashSaleGoodsId;
        redisUtil.del(key);
	}

	@Override
	public String getFlashSaleGoodsVerifyCodeById(Long customerId, Long flashSaleGoodsId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODSVERIFYCODE + ":" + customerId + flashSaleGoodsId;
        return (String) redisUtil.get(key);
	}

	@Override
	public void setFlashSaleOrderById(Order order) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEORDER + ":" + order.getCustomerId();
        redisUtil.set(key, order, REDIS_FLASHSALEORDER_EXPIRE);
	}

    @Override
	public void setCustomerFlagById(Long customerId, Long flashSaleGoodsId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALECUSTOMER + ":" + customerId + flashSaleGoodsId;
        redisUtil.set(key, true, REDIS_FLASHSALEORDER_EXPIRE);
	}
	
	@Override
	public void delFlashSaleOrderById(Long customerId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEORDER + ":" + customerId;
        redisUtil.del(key);
	}

	@Override
	public void delCustomerFlagById(Long customerId, Long flashSaleGoodsId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALECUSTOMER + ":" + customerId + flashSaleGoodsId;
        redisUtil.del(key);
	}
	
	@Override
	public Order getFlashSaleOrderById(Long customerId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEORDER + ":" + customerId;
        return (Order) redisUtil.get(key);
	}
	
	@Override
	public boolean getCustomerFlagById(Long customerId, Long flashSaleGoodsId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALECUSTOMER + ":" + customerId + flashSaleGoodsId;
        return (boolean) redisUtil.get(key);
	}
}
