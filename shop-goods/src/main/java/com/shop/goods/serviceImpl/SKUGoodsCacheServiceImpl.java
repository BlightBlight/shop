package com.shop.goods.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shop.common.utils.RedisUtil;
import com.shop.goods.model.SKUGoodsSpecValue;
import com.shop.goods.model.SKUGoods;
import com.shop.goods.service.SKUGoodsCacheService;

@Service
public class SKUGoodsCacheServiceImpl implements SKUGoodsCacheService{
	@Autowired
    private RedisUtil redisUtil;
    
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    
    @Value("${redis.expire.skuGoods}")
    private Long REDIS_EXPIRE;
    
    @Value("${redis.key.skuGoods}")
    private String REDIS_KEY_SKUGOODS;
    
    @Value("${redis.expire.skuGoodsSpecValue}")
    private Long REDIS_SPECVALUE_EXPIRE;
    
    @Value("${redis.key.skuGoodsSpecValue}")
    private String REDIS_KEY_SKUGOODSSPECVALUE;
    
	@Override
	public void setSKUGoodsByName(SKUGoods skuGoods) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SKUGOODS + ":" + skuGoods.getSkuName();
        redisUtil.set(key, skuGoods, REDIS_EXPIRE);
	}

	@Override
	public void setSKUGoodsNameById(Long skuId, String skuName) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SKUGOODS + ":" + skuId;
        redisUtil.set(key, skuName, REDIS_EXPIRE);
	}
	
	@Override
	public void setSKUGoodsNameBySpecValueId(Long specValueId, String skuName) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SKUGOODS + ":" + specValueId;
        redisUtil.set(key, skuName, REDIS_EXPIRE);
	}
	
	@Override
	public void setSKUGoodsSpecValueById(SKUGoodsSpecValue skuGoodsSpecValue) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SKUGOODSSPECVALUE + ":" + skuGoodsSpecValue.getSpecValueId();
        redisUtil.set(key, skuGoodsSpecValue, REDIS_SPECVALUE_EXPIRE);
	}

	@Override
	public void delSKUGoodsByName(String skuName) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SKUGOODS + ":" + skuName;
        redisUtil.del(key);
	}
	
	@Override
	public void delSKUGoodsNameById(Long skuId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SKUGOODS + ":" + skuId;
        redisUtil.del(key);
	}
	
	@Override
	public void delSKUGoodsNameBySpecValueId(Long specValueId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SKUGOODS + ":" + specValueId;
        redisUtil.del(key);
	}
	
	@Override
	public void delSKUGoodsSpecValueById(Long specValueId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SKUGOODSSPECVALUE + ":" + specValueId;
        redisUtil.del(key);
	}
	
	@Override
    public void decrSKUGoodsByName(String skuName, Long delta) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SKUGOODS + ":" + skuName;
        redisUtil.decr(key, delta);
    }
	
	@Override
	public SKUGoods getSKUGoodsByName(String skuName) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SKUGOODS + ":" + skuName;
        return (SKUGoods) redisUtil.get(key);
	}
	
	@Override
	public String getSKUGoodsNameById(Long skuId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SKUGOODS + ":" + skuId;
        return (String) redisUtil.get(key);
	}
	
	@Override
	public String getSKUGoodsNameBySpecValueId(Long specValueId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SKUGOODS + ":" + specValueId;
        return (String) redisUtil.get(key);
	}
	
	@Override
	public SKUGoodsSpecValue getSKUGoodsSpecValueById(Long specValueId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SKUGOODSSPECVALUE + ":" + specValueId;
        return (SKUGoodsSpecValue) redisUtil.get(key);
	}
}
