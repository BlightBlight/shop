package com.shop.customer.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shop.common.utils.RedisUtil;
import com.shop.customer.model.Customer;
import com.shop.customer.model.CustomerInfo;
import com.shop.customer.service.CustomerCacheService;

/**
 * 用户缓存Service实现类
 */
@Service
/*
 * 这里有个问题：所有的缓存我都用的String，这是不对的，后期优化要对应不同的数据用不同的类型
 * 如，用户、SPU、SKU的缓存都应该用hmset存储，key为Id，value为一个数组（量少）或一个hashMap（量大），key为实体类的各个属性，value为属性具体值
 * 如果用String存储，意味着我的整个实体类，如Customer会序列化，会有并发问题，会浪费资源，还要反序列化。
 * 再比如，想要做一个网站在线人数统计，那么所有用户的Id就要放到list中，再用HLL算法进行统计等等
 */
public class CustomerCacheServiceImpl implements CustomerCacheService {
    @Autowired
    private RedisUtil redisUtil;
    
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    
    @Value("${redis.expire.common}")
    private Long REDIS_EXPIRE;
    
    @Value("${redis.expire.authCode}")
    private Long REDIS_EXPIRE_AUTH_CODE;
    
    @Value("${redis.expire.token}")
    private Long REDIS_EXPIRE_TOKEN;
    
    @Value("${redis.key.customer}")
    private String REDIS_KEY_CUSTOMER;
    
    @Value("${redis.key.customerInfo}")
    private String REDIS_KEY_CUSTOMERINFO;
    
    @Value("${redis.key.authCode}")
    private String REDIS_KEY_AUTH_CODE;
    
    @Value("${redis.key.token}")
    private String REDIS_KEY_TOKEN;

    @Override
    public void setCustomer(Customer customer) {
		/*
		 * TODO:现在是单机版redis，无需担心redis写入、删除失败的风险
		 * 但是考虑到日后分布式布置，那么这里也需要捕捉redis写入失败的异常
		 */
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CUSTOMER + ":" + customer.getCustomermobileNum();
        redisUtil.set(key, customer, REDIS_EXPIRE);
    }
    
    @Override
    public void setCustomermobileNumById(Long customerId, String mobileNum) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CUSTOMER + "mobileNum" + ":" + customerId;
        redisUtil.set(key, mobileNum, REDIS_EXPIRE);
    }
    
    @Override
    public void setCustomerInfo(CustomerInfo customerInfo) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CUSTOMERINFO + ":" + customerInfo.getCustomermobileNum();
        redisUtil.set(key, customerInfo, REDIS_EXPIRE);
    }
    
    @Override
    public void setAuthCodeBymobileNum(String customermobileNum, String authCode) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_AUTH_CODE + ":" + customermobileNum;
        redisUtil.set(key, authCode, REDIS_EXPIRE_AUTH_CODE);
    }
    
    @Override
    public void setTokenBymobileNum(String mobileNum, String token) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_TOKEN + ":" + mobileNum;
        redisUtil.set(key, token, REDIS_EXPIRE_TOKEN);
    }
    
    @Override
    public void setCustomerLoginCountBymobileNum(String mobileNum, Integer count, Long expire) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CUSTOMER + "LoginCount" + ":" + mobileNum;
        redisUtil.set(key, count, expire);
    }
    
    @Override
    public void delCustomerBymobileNum(String mobileNum) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CUSTOMER + ":" + mobileNum;
        redisUtil.del(key);
    }
    
    @Override
    public void delCustomermobileNumById(Long customerId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CUSTOMER + ":" + customerId;
        redisUtil.del(key);
    }
    
    @Override
    public void delCustomerInfoBymobileNum(String mobileNum) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CUSTOMERINFO + ":" + mobileNum;
        redisUtil.del(key);
    }
    
    @Override
    public void delTokenBymobileNum(String mobileNum) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_TOKEN + ":" + mobileNum;
        redisUtil.del(key);
    }
    
    @Override
    public Customer getCustomerBymobileNum(String customermobileNum) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CUSTOMER + ":" + customermobileNum;
        return (Customer) redisUtil.get(key);
    }
    
    @Override
    public String getCustomermobileNumById(Long customerId) {
    	String key = REDIS_DATABASE + ":" + REDIS_KEY_CUSTOMER + "mobileNum" + ":" + customerId;
    	return (String) redisUtil.get(key);
    }
    
    @Override
    public CustomerInfo getCustomerInfoBymobileNum(String mobileNum) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CUSTOMERINFO + ":" + mobileNum;
        return (CustomerInfo) redisUtil.get(key);
    }
    
    @Override
    public String getAuthCodeBymobileNum(String mobileNum) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_AUTH_CODE + ":" + mobileNum;
        return (String) redisUtil.get(key);
    }
    	
    @Override
    public String getTokenBymobileNum(String mobileNum) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_TOKEN + ":" + mobileNum;
        return (String) redisUtil.get(key);
    }
    
    @Override
    public int getCustomerLoginCountBymobileNum(String mobileNum) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CUSTOMER + "LoginCount" + ":" + mobileNum;
        return (int) redisUtil.get(key);
    }
}
