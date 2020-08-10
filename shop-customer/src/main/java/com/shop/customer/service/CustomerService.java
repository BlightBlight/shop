package com.shop.customer.service;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.security.core.userdetails.UserDetails;

import com.shop.customer.model.Customer;
import com.shop.customer.model.CustomerInfo;
import com.shop.customer.vo.RegisterVo;
import com.shop.customer.vo.UpdateCustomerVo;

/**
 * 用户Service接口
 */
public interface CustomerService {
	/**
	 * 新增用户
	 */
	boolean saveCustomer(RegisterVo registerVo);
	
	/**
	 * 生成手机验证码
	 */
    String generateAuthCode(String mobileNum);
	
	/**
	 * 删除用户
	 * @param mobileNum 用户手机号
	 */
	int removeCustomerBymobileNum(String mobileNum);
	
	/**
	 *取消删除用户
	 * @param mobileNum 用户手机号
	 */
	int cancelRemoveCustomerBymobileNum(String mobileNum);
	
	/**
	 * 修改用户
	 */
	int updateCustomer(UpdateCustomerVo updateUserVo);
	
	/**
	 * 修改密码
	 * @param mobileNum 用户手机号
	 * @param password 密码
	 * @param authCode 验证码
	 */
    void updatePasswordBymobileNum(String mobileNum, String password, String authCode);
	
	/**
	 * 根据手机号查找用户
	 * @param mobileNum 手机号
	 * @param lock 分布锁
	 */
	Customer getCustomerBymobileNum(String mobileNum);
	
	/**
	 * 根据Id查找用户
	 * @param customerId 用户Id
	 */
	Customer getCustomerById(Long customerId);
	
	/**
	 * 根据用户名查找用户
	 * @param customerName 用户名
	 */
	Customer getCustomerByName(String customerName);
	
	/**
	 * 根据手机号查找用户信息
	 * @param mobileNum 手机号
	 */
	CustomerInfo getCustomerInfoBymobileNum(String mobileNum);
	
	/**
	 * 根据手机号查找验证码
	 * @param mobileNum
	 */
	String getVerifyCodeBymobileNum(String mobileNum);
	
	/**
	 * 根据手机号登录
	 * @param mobileNum 手机号
	 */
	String loginBymobileNum(String mobileNum);
	
	/**
	 * 根据登录名生成UserDetails
	 * @param customerName 手机号
	 */
	UserDetails loadCustomerByName(String customerName);
	
	/**
	 * 获取当前登录用户
	 */
    Customer getCurrentCustomer();
    
	/**
	 * 登出用户
	 * @param mobileNum 手机号
	 */
	boolean logoutCustomerBymobileNum(String mobileNum);
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于需要对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param mobileNum 用户手机号
	 */
	InterProcessMutex setCustomerLockBymobileNum(String mobileNum);
	
	/**
	 * 根据手机号获取用户分布锁
	 * 不同参数获取分布锁不一样
	 * @param mobileNum 手机号
	 */
	InterProcessMutex getCustomerLockBymobileNum(String mobileNum);
	
	InterProcessMutex setCustomerLockById(Long customerId);
	
	InterProcessMutex getCustomerLockById(Long customerId);
	
	InterProcessMutex setCustomerInfoLockBymobileNum(String mobileNum);
	
	InterProcessMutex getCustomerInfoLockBymobileNum(String mobileNum);
}
