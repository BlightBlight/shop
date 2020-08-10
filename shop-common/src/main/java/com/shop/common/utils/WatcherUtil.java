package com.shop.common.utils;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WatcherUtil implements Watcher {

	@Override
	public void process(WatchedEvent event) {
		log.info("【Watcher监听事件】={}", event.getState());
		log.info("【监听路径为】={}", event.getPath());
		log.info("【监听的类型为】={}", event.getType()); // 三种监听类型： 创建，删除，更新
	}
}
