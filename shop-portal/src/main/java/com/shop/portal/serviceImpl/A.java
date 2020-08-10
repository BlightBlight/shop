package com.shop.portal.serviceImpl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.shop.common.utils.LockUtils;

@Service
public class A {
    @Autowired
    CuratorFramework client;
    
	//@Async("threadPoolTaskExecutor")
	public String test1() throws Exception {
		System.out.println(Thread.currentThread() + "test1启动");
		InterProcessMutex lock = getLock("test");
		try {
			lock.acquire();
			System.out.println(Thread.currentThread() + "test1锁上节点，沉睡5秒");
			Thread.sleep(5000);
			System.out.println(Thread.currentThread() + "test1释放节点");
			lock.release();
		} catch (Exception e) {
			System.out.println(Thread.currentThread() + "test1不持有节点");
		}
		return "啊哈";
	}
	
	//@Async("threadPoolTaskExecutor")
	public String test2() throws Exception {
		System.out.println(Thread.currentThread() + "test2启动");
		InterProcessMutex lock = getLock("test");
		try {
			lock.acquire();
			System.out.println(Thread.currentThread() + "test2锁上节点");
			Thread.sleep(5000);
			lock.release();
			System.out.println(Thread.currentThread() + "test2释放节点");
		} catch (Exception e) {
			System.out.println(Thread.currentThread() + "test2不持有节点");
		}
		return "啊哈";
	}
	
	public InterProcessMutex getLock(String mobileNum) {
		InterProcessMutex lock = LockUtils.getLock("customers", "/test");
		if (lock == null) {
			lock =  new InterProcessMutex(client, "/test");
			LockUtils.putLock("customers", "/test", lock);
		}
		return lock;
	}
}
