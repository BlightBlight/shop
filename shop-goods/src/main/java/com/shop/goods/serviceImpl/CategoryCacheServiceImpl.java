package com.shop.goods.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shop.common.utils.RedisUtil;
import com.shop.goods.model.Category;
import com.shop.goods.service.CategoryCacheService;

@Service
public class CategoryCacheServiceImpl implements CategoryCacheService {
    @Autowired
    private RedisUtil redisUtil;
    
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    
    @Value("${redis.expire.category}")
    private Long REDIS_EXPIRE;
    
    @Value("${redis.key.category}")
    private String REDIS_KEY_CATEGORY;
	
	@Override
	public void setCategoryByName(Category category) {
		String key = REDIS_DATABASE + ":" + REDIS_KEY_CATEGORY + ":" + category.getCategoryName();
		redisUtil.set(key, category, REDIS_EXPIRE);
	}
	
	@Override
	public void setCategoryNameById(Long categoryId, String categoryName) {
		String key = REDIS_DATABASE + ":" + REDIS_KEY_CATEGORY + ":" + categoryId;
		redisUtil.set(key, categoryName, REDIS_EXPIRE);
	}
	
	@Override
	public void delCategoryByName(String categoryName) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CATEGORY + ":" + categoryName;
        redisUtil.del(key);
	}
	
	@Override
	public void delCategoryNameById(Long categoryId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CATEGORY + ":" + categoryId;
        redisUtil.del(key);
	}
	
	@Override
	public Category getCategoryByName(String categoryName) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CATEGORY + ":" + categoryName;
        return (Category) redisUtil.get(key);
	}
	
	@Override
	public String getCategoryNameById(Long categoryId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CATEGORY + ":" + categoryId;
        return (String) redisUtil.get(key);
	}
}
