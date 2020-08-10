package com.shop.goods.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shop.common.utils.RedisUtil;
import com.shop.goods.model.Brand;
import com.shop.goods.service.BrandCacheService;

@Service
public class BrandCacheServiceImpl implements BrandCacheService {
    @Autowired
    private RedisUtil redisUtil;
    
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    
    @Value("${redis.expire.brand}")
    private Long REDIS_EXPIRE;
    
    @Value("${redis.key.brand}")
    private String REDIS_KEY_BRAND;
    
	@Override
	public void setBrandByName(Brand brand) {
		String key = REDIS_DATABASE + ":" + REDIS_KEY_BRAND + ":" + brand.getBrandName();
		redisUtil.set(key, brand, REDIS_EXPIRE);
	}
	
	@Override
	public void setBrandNameById(Long brandId, String brandName) {
		String key = REDIS_DATABASE + ":" + REDIS_KEY_BRAND + ":" + brandId;
		redisUtil.set(key, brandName, REDIS_EXPIRE);
	}
	
	@Override
	public void delBrandByName(String brandName) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_BRAND + ":" + brandName;
        redisUtil.del(key);
	}
	
	
	@Override
	public void delBrandNameById(Long brandId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_BRAND + ":" + brandId;
        redisUtil.del(key);
	}
	@Override
	public Brand getBrandByName(String brandName) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_BRAND + ":" + brandName;
        return (Brand) redisUtil.get(key);
	}
	
	@Override
	public String getBrandNameById(Long brandId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_BRAND + ":" + brandId;
        return (String) redisUtil.get(key);
	}
}
