package com.shop.flashsale.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shop.common.utils.RedisUtil;
import com.shop.flashsale.model.FlashSaleGoods;
import com.shop.flashsale.service.FlashSaleGoodsCacheService;

@Service
public class FlashSaleGoodsCacheServiceImpl implements FlashSaleGoodsCacheService {
	@Autowired
    private RedisUtil redisUtil;
    
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    
    @Value("${redis.expire.flashSaleGoods}")
    private long REDIS_EXPIRE;
    
    @Value("${redis.key.flashSaleGoods}")
    private String REDIS_KEY_FLASHSALEGOODS;
    
    @Value("${redis.key.flashSaleGoodsStock}")
    private String REDIS_KEY_FLASHSALEGOODSSTOCK;
	
    @Override
	public void setFlashSaleGoodsByName(FlashSaleGoods flashSaleGoods) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODS + ":" + flashSaleGoods.getFlashSaleGoodsName();
        redisUtil.set(key, flashSaleGoods, REDIS_EXPIRE);
	}

    @Override
	public void setFlashSaleGoodsNameById(Long flashSaleGoodsId, String flashSaleGoodsName) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODS + ":" + flashSaleGoodsId;
        redisUtil.set(key, flashSaleGoodsName, REDIS_EXPIRE);
	}
    
    @Override
	public void setFlashSaleGoodsStockById(Long flashSaleGoodsId, Long stock) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODSSTOCK + ":" + flashSaleGoodsId;
        redisUtil.set(key, stock, REDIS_EXPIRE);
	}
    
	@Override
	public void delFlashSaleGoodsByName(String flashSaleGoodsName) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODS + ":" + flashSaleGoodsName;
        redisUtil.del(key);
	}
    
	@Override
	public void delFlashSaleGoodsNameById(Long flashSaleGoodsId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODS + ":" + flashSaleGoodsId;
        redisUtil.del(key);
	}

	@Override
	public void delFlashSaleGoodsStockById(Long flashSaleGoodsId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODSSTOCK + ":" + flashSaleGoodsId;
        redisUtil.del(key);
	}
	
	@Override
	public FlashSaleGoods getFlashSaleGoodsByName(String flashSaleGoodsName) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODS + ":" + flashSaleGoodsName;
        return (FlashSaleGoods) redisUtil.get(key);
	}
	
	@Override
	public String getFlashSaleGoodsNameById(Long flashSaleGoodsId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODS + ":" + flashSaleGoodsId;
        return (String) redisUtil.get(key);
	}

	@Override
	public Long getFlashSaleGoodsStockById(Long flashSaleGoodsId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_FLASHSALEGOODSSTOCK + ":" + flashSaleGoodsId;
        return (Long) redisUtil.get(key);
	}
}
