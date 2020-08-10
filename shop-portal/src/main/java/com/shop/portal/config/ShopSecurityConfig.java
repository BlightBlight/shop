package com.shop.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.shop.customer.service.CustomerService;
import com.shop.security.config.SecurityConfig;

/**
 * shop-security模块相关配置
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class ShopSecurityConfig extends SecurityConfig {
	
	@Autowired
	CustomerService customerService;

	@Bean
	public UserDetailsService userDetailsService() { // 获取登录用户信息
		return mobileNum -> customerService.loadCustomerByName(mobileNum);
	}
	 
	 
}
