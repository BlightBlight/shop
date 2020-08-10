package com.shop.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shop.security.component.CustomerPasswordEncoder;
import com.shop.security.component.JwtAuthenticationTokenFilter;
import com.shop.security.component.RestAuthenticationEntryPoint;
import com.shop.security.component.RestfulAccessDeniedHandler;
import com.shop.security.utils.JwtTokenUtil;

/**
 * 对SpringSecurity的配置的扩展，支持自定义白名单资源路径和查询用户逻辑
 */
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	/**
	 * Http安全配置
	 */
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity
				.authorizeRequests();
		// 不需要保护的资源路径允许访问
		for (String url : ignoreUrlsConfig().getUrls()) {
			registry.antMatchers(url).permitAll();
		} // 允许跨域请求的OPTIONS请求
		registry.antMatchers(HttpMethod.OPTIONS).permitAll();
		registry.and()
				.authorizeRequests().anyRequest().authenticated() // 任何请求需要身份认证
				.and()
				.formLogin().disable()
				.csrf().disable().
				sessionManagement()
				// 自定义权限拒绝处理类
				//.and()
				//.exceptionHandling()
				//.accessDeniedHandler(restfulAccessDeniedHandler())
				//.authenticationEntryPoint(restAuthenticationEntryPoint())
				// 自定义权限拦截器JWT过滤器
				.and()
				.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	  
	/**
	 * 用户认证配置
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
		/*
		 * auth .inMemoryAuthentication() .withUser("admin1") // 管理员，同事具有
		 * ADMIN,USER权限，可以访问所有资源 .password("{noop}admin1") .roles("ADMIN", "USER");
		 */
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new CustomerPasswordEncoder();
	}
	
	@Bean
	public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
		return new JwtAuthenticationTokenFilter();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public RestfulAccessDeniedHandler restfulAccessDeniedHandler() {
		return new RestfulAccessDeniedHandler();
	}

	@Bean
	public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
		return new RestAuthenticationEntryPoint();
	}

	@Bean
	public IgnoreUrlsConfig ignoreUrlsConfig() {
		return new IgnoreUrlsConfig();
	}

	@Bean
	public JwtTokenUtil jwtTokenUtil() {
		return new JwtTokenUtil();
	}
		
}
