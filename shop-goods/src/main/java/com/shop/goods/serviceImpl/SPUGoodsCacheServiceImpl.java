package com.shop.goods.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shop.common.utils.RedisUtil;
import com.shop.goods.model.SPUGoodsSpec;
import com.shop.goods.model.SPUGoods;
import com.shop.goods.service.SPUGoodsCacheService;

@Service
public class SPUGoodsCacheServiceImpl implements SPUGoodsCacheService{
    @Autowired
    private RedisUtil redisUtil;
    
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    
    @Value("${redis.expire.spuGoods}")
    private Long REDIS_EXPIRE;
    
    @Value("${redis.key.spuGoods}")
    private String REDIS_KEY_SPUGOODS;
    
    @Value("${redis.expire.spuGoodsSpec}")
    private Long REDIS_SPEC_EXPIRE;
    
    @Value("${redis.key.spuGoodsSpec}")
    private String REDIS_KEY_SPUGOODSSPEC;
	
	@Override
	public void setSPUGoodsByName(SPUGoods spuGoods){
		String key = REDIS_DATABASE + ":" + REDIS_KEY_SPUGOODS + ":" + spuGoods.getSpuName();
		redisUtil.set(key, spuGoods, REDIS_EXPIRE);
	}
	
	@Override
	public void setSPUGoodsNameById(Long spuId, String spuName){
		String key = REDIS_DATABASE + ":" + REDIS_KEY_SPUGOODS + ":" + spuId;
		redisUtil.set(key, spuName, REDIS_EXPIRE);
	}
	
	@Override
	public void setSPUGoodsNameBySpecId(Long specId, String spuName){
		String key = REDIS_DATABASE + ":" + REDIS_KEY_SPUGOODS + ":" + specId;
		redisUtil.set(key, spuName, REDIS_EXPIRE);
	}
	
	@Override
	public void setSPUGoodsSpec(SPUGoodsSpec spuGoodsSpec){
		String key = REDIS_DATABASE + ":" + REDIS_KEY_SPUGOODSSPEC + ":" + spuGoodsSpec.getSpecId();
		redisUtil.set(key, spuGoodsSpec, REDIS_EXPIRE);
	}
	
	@Override
	public void delSPUGoodsByName(String spuName) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SPUGOODS + ":" + spuName;
        redisUtil.del(key);
	}
	
	@Override
	public void delSPUGoodsNameById(Long spuId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SPUGOODS + ":" + spuId;
        redisUtil.del(key);
	}
	
	@Override
	public void delSPUGoodsNameBySpecId(Long specId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SPUGOODS + ":" + specId;
        redisUtil.del(key);
	}
	
	@Override
	public void delSPUGoodsSpecById(Long specId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SPUGOODSSPEC + ":" + specId;
        redisUtil.del(key);
	}
	
	@Override
	public SPUGoods getSPUGoodsByName(String spuName) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SPUGOODS + ":" + spuName;
        return (SPUGoods) redisUtil.get(key);
	}
	
	@Override
	public String getSPUGoodsNameById(Long spuId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SPUGOODS + ":" + spuId;
        return (String) redisUtil.get(key);
	}
	
	@Override
	public String getSPUGoodsNameBySpecId(Long specId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SPUGOODS + ":" + specId;
        return (String) redisUtil.get(key);
	}
	
	@Override
	public SPUGoodsSpec getSPUGoodsSpecById(Long specId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_SPUGOODSSPEC + ":" + specId;
        return (SPUGoodsSpec) redisUtil.get(key);
	}
}
