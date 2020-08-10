package com.shop.goods.service;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.shop.goods.model.SPUGoods;
import com.shop.goods.model.SPUGoodsSpec;
import com.shop.goods.vo.SaveSPUGoodsVo;
import com.shop.goods.vo.UpdateSPUGoodsVo;

/**
 * SPU商品接口层
 */
public interface SPUGoodsService {
	/**
	 * 创建SPU商品
	 */
	boolean saveSPUGoods(SaveSPUGoodsVo saveSKUGoodsVo);
	
	/**
	 * 删除SPU商品
	 * @param spuName spu商品名称
	 */
	boolean removeSPUGoodsByName(String spuName);
	
	/**
	 * 修改SPU商品
	 */
	boolean updateSPUGoods(UpdateSPUGoodsVo updateSPUGoodsVo);
	
	/**
	 * 根据名称获取SPU商品
	 * @param spuName spu商品名称
	 */
	SPUGoods getSPUGoodsByName(String spuName);
	
	/**
	 * 根据Id获取SPU商品
	 * @param spuId spu商品Id
	 */
	SPUGoods getSPUGoodsById(Long spuId);
	
	/**
	 * 根据specId获取SPU商品
	 * @param specId 规格Id
	 */
	SPUGoods getSPUGoodsBySpecId(Long specId);
	
	/**
	 * 根据Id获取规格
	 * @param specId spu商品Id
	 */
	SPUGoodsSpec getSPUGoodsSpecById(Long specId);
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于需要对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param spuName spu商品名称
	 */
	InterProcessMutex setSPUGoodsLockByName(String spuName);
	
	/**
	 * 根据名称获取SPU商品分布锁
	 * @param spuName spu商品名称
	 */
	InterProcessMutex getSPUGoodsLockByName(String spuName);
	
	/**
	 * 根据Id获取SPU商品分布锁
	 * @param spuId spu商品Id
	 */
	InterProcessMutex getSPUGoodsLockById(Long spuId);
	
	/**
	 * 根据specId获取SPU商品分布锁
	 * @param specId 规格Id
	 */
	InterProcessMutex getSPUGoodsLockBySpecId(Long specId);
	
	/**
	 * 根据Id获取规格分布锁
	 * @param specId 规格Id
	 */
	InterProcessMutex getSPUGoodsSpecLockById(Long specId);
	
	/*
	 * 这里暂时先用不上这个方法 这个方法我设计之初是为了减少数据库读写压力，尽量少返回字段
	 * 但是如今用了redis，数据库读写问题基本解决，除非日后数据库字段膨胀太大了，到时候可以再考虑使用这两个方法
	 */
	
	/**
	 * 根据Id查询SPU商品是否存在
	 * 
	 * @param spuId spu商品Id
	 */
	/*
	 * SPUGoods isSPUGoodsExists(long spuId);
	 * 
	 *//**
		 * 根据spuName查询SPU商品是否存在
		 * 
		 * @param spuName spu商品名称
		 *//*
			 * SPUGoods isSPUGoodsExists(String spuName);
			 */
	
}
