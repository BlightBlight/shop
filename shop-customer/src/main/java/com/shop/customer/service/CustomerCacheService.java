package com.shop.customer.service;

import com.shop.customer.model.Customer;
import com.shop.customer.model.CustomerInfo;

/**
 * 用户信息缓存业务类
 */
public interface CustomerCacheService {
	/**
	 * 根据手机号设置用户缓存(customerId, mobileNum, nickName, customerStatus, deleteStatus)
	 */
	void setCustomer(Customer customer);

	/**
	 * 根据Id设置用户手机号缓存
	 * @param customerId 用户Id
	 * @param mobileNum 手机号
	 */
	void setCustomermobileNumById(Long customerId, String mobileNum);
	
	/**
	 * 根据手机号设置用户信息缓存(customerId, mobileNum, pwd, salt, registerTime, loginTime, updateTime, deleteTime)
	 */
	void setCustomerInfo(CustomerInfo customerInfo);
	
	/**
	 * 根据手机号设置验证码
	 * @param mobileNum 用户手机号
	 * @param authCode 验证码
	 */
    void setAuthCodeBymobileNum(String mobileNum, String authCode);
	
	/**
	 * 根据手机号设置token缓存
	 * @param mobileNum 用户手机号
	 * @param token token
	 */
    void setTokenBymobileNum(String mobileNum, String token);
	
	/**
	 * 根据手机号设置用户登录次数缓存
	 * @param mobileNum 用户手机号
	 * @param count 登录次数
	 * @param expire 过期时间
	 */
	void setCustomerLoginCountBymobileNum(String mobileNum, Integer count, Long expire);
	
	/**
	 * 根据手机号删除用户缓存
	 * @param mobileNum 用户手机号
	 */
    void delCustomerBymobileNum(String mobileNum);
	
	/**
	 * 根据手机号删除用户信息缓存
	 * @param mobileNum 用户手机号
	 */
    void delCustomerInfoBymobileNum(String mobileNum);
	
    /**
	 * 根据Id删除手机号缓存
	 * @param customerId 用户Id
	 */
	void delCustomermobileNumById(Long customerId);
    
	/**
	 * 根据手机号删除token缓存
	 * @param mobileNum 用户手机号
	 */
    void delTokenBymobileNum(String mobileNum);
	
    /**
	 * 根据手机号获取用户信息缓存
	 * @param mobileNum 手机号
	 */
    Customer getCustomerBymobileNum(String mobileNum);
    
    /**
	 * 根据手机号获取用户缓存
	 * @param mobileNum 手机号
	 */
    CustomerInfo getCustomerInfoBymobileNum(String mobileNum);
    
	/**
	 * 根据Id获取手机号缓存
	 * @param customerId 用户Id
	 */
	String getCustomermobileNumById(Long customerId);
    
	/**
	 * 根据手机号获取验证码
	 * @param mobileNum
	 */
    String getAuthCodeBymobileNum(String mobileNum);
    
	/**
	 * 根据手机号获取token缓存
	 * @param mobileNum 用户手机号
	 */
    String getTokenBymobileNum(String mobileNum);
	
	/**
	 * 根据手机号获取用户登陆次数缓存
	 * @param mobileNum 用户手机号
	 */
    int getCustomerLoginCountBymobileNum(String mobileNum);
}
