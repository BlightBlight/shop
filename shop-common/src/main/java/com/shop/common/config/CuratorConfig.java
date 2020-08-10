package com.shop.common.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CuratorConfig {
	@Value("${zookeeper.url}")
    private String zkUrl;
	
	@Bean
	public CuratorFramework initCuratorFramework() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(5000,3);	//重连策略
		CuratorFramework client = CuratorFrameworkFactory.builder()		//建造者模式
		                                      .connectString(zkUrl)		//配置zk服务器地址
		                                      .sessionTimeoutMs(5000)	//session时间
		                                      .connectionTimeoutMs(5000)//连接超时时间
		                                      .retryPolicy(retryPolicy)
		                                      .build();
		client.start();
		log.info("初始化连接成功" + zkUrl);
		return client;
	}
}
