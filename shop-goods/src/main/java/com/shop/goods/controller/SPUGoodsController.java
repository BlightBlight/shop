package com.shop.goods.controller;

import javax.validation.Valid;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.result.CommonResult;
import com.shop.common.utils.JacksonUtil;
import com.shop.common.utils.LockUtils;
import com.shop.goods.model.SPUGoods;
import com.shop.goods.model.SPUGoodsSpec;
import com.shop.goods.service.SPUGoodsService;
import com.shop.goods.vo.SaveSPUGoodsVo;
import com.shop.goods.vo.UpdateSPUGoodsVo;

import lombok.extern.slf4j.Slf4j;

/**
 * SPU商品控制层
 */
@Controller
@RequestMapping("/spuGoods")
@Slf4j
public class SPUGoodsController {
	@Autowired
	SPUGoodsService spuGoodsService;
	
    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.spuGoods}")
    private String lockScope;
    
	/**
	 * 新增SPU商品
	 * @throws Exception 
	 */
    @PostMapping()
    @ResponseBody
    public CommonResult<String> saveSPUGoods(@RequestBody @Valid SaveSPUGoodsVo saveSPUGoodsVo) throws Exception {
    	log.debug("待创建SPU商品Vo：" + saveSPUGoodsVo);
    	
    	InterProcessMutex lock = getSPUGoodsLockByName(saveSPUGoodsVo.getSpuName());
		try {
			lock.acquire();
			
	    	@SuppressWarnings("rawtypes")
			CommonResult result = isSPUGoodsExistsByName(saveSPUGoodsVo.getSpuName());
	    	if (result.getCode() == 200) {
	    		SPUGoods spuGoods = (SPUGoods) result.getData();
	    		log.info("该SPU商品已存在");
	    		return CommonResult.failed("该SPU商品已存在");
	    	}
			
			if (spuGoodsService.saveSPUGoods(saveSPUGoodsVo)) {
				log.info("成功创建SPU商品");
				return CommonResult.success("成功创建SPU商品");
			}
		} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new Exception(e);
		} finally {
			try {
				if (lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
				throw new Exception(e);
			}
		}
    	return CommonResult.internalServerFailed();
    }
	
	/**
	 * 删除SPU商品
	 * @throws Exception
	 */
    @DeleteMapping(value = "/{spuId}")
    @ResponseBody
    public CommonResult<String> removeSPUGoods(@RequestBody String jsonObject) throws Exception {
    	String spuName = JacksonUtil.parseObject(jsonObject, String.class);
    	log.debug("待删除SPU商品:" + spuName);
    	
    	InterProcessMutex lock = getSPUGoodsLockByName(spuName);
		try {
			lock.acquire();
			
	    	@SuppressWarnings("rawtypes")
			CommonResult result = isSPUGoodsExistsByName(spuName);
	    	if (result.getCode() != 200) {
	    		return result;
	    	}
    	
	    	if (spuGoodsService.removeSPUGoodsByName(spuName)) {
	    		log.info("成功删除SPU商品");
	    		return CommonResult.success("成功删除SPU商品");
	    	}
    	} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new Exception(e);
		} finally {
			try {
				if (lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
				throw new Exception(e);
			}
		}
		return CommonResult.internalServerFailed();
    }
	
	/**
	 * 修改SPU商品
	 * @throws Exception 
	 */
	@PutMapping()
	@ResponseBody
	public CommonResult<String> updateSPUGoods(@RequestBody @Valid UpdateSPUGoodsVo updateSPUGoodsVo) throws Exception {
		log.debug("待修改SPU商品Vo：" + updateSPUGoodsVo.toString());
		
		InterProcessMutex lock = getSPUGoodsLockByName(updateSPUGoodsVo.getSpuName());
		try {
			lock.acquire();
			
			//验证spuId是否存在
			@SuppressWarnings("rawtypes")
			CommonResult result = isSPUGoodsExistsById(updateSPUGoodsVo.getSpuId());
			if (result.getCode() != 200) {
				return result;
			}
			
			SPUGoods spuGoods = (SPUGoods) result.getData();
			result = isSPUGoodsExistsByName(updateSPUGoodsVo.getSpuName());
			//spuName存在且修改的spuName与原spuName不相同
			if (result.getCode() == 200 && (!spuGoods.getSpuName().equals(updateSPUGoodsVo.getSpuName()))) {
				log.info("该SPU商品名称已存在：" + updateSPUGoodsVo);
				return CommonResult.failed("该SPU商品名称已存在");
			}
			
			if (spuGoodsService.updateSPUGoods(updateSPUGoodsVo)) {
				log.info("成功修改SPU商品");
				return CommonResult.success("成功修改SPU商品");
			}
		} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new Exception(e);
		} finally {
			try {
				if (lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
				throw new Exception(e);
			}
		}
		return CommonResult.internalServerFailed();
	}
    
	/**
	 * 根据Id查找SPU商品
	 * @param spuId spu商品Id
	 */
    public SPUGoods getSPUGoodsById(Long spuId) {
    	log.debug("根据spuId：" + spuId + "查找SPU商品");
    	SPUGoods spuGoods = spuGoodsService.getSPUGoodsById(spuId);
    	return spuGoods;
    }
    
	/**
	 * 根据specId查找SPU商品
	 * @param specId 规格Id
	 */
    public SPUGoods getSPUGoodsBySpecId(Long specId) {
    	log.debug("根据specId：" + specId + "查找SPU商品");
    	SPUGoods spuGoods = spuGoodsService.getSPUGoodsBySpecId(specId);
    	return spuGoods;
    }
    
	/**
	 * 根据Id查找规格
	 * @param specId 规格Id
	 */
    public CommonResult getSPUGoodsSpecById(Long specId) {
    	log.debug("根据specId：" + specId + "查找规格");
    	SPUGoodsSpec spuGoodsSpec = spuGoodsService.getSPUGoodsSpecById(specId);
    	return CommonResult.success(spuGoodsSpec);
    }
    
	/**
	 * 根据Id查找SPU商品是否正常
	 * @param spuId spu商品Id
	 */
    @SuppressWarnings("rawtypes")
	public CommonResult  isSPUGoodsExistsById(Long spuId) {
    	SPUGoods spuGoods = spuGoodsService.getSPUGoodsById(spuId);
    	
    	if (spuGoods == null) {
    		log.info("该SPU商品不存在" + spuId);
    		return CommonResult.validateFailed("该SPU商品不存在");
    	}
    	
		if (spuGoods.getVerifyStatus() != 2) {
			log.info("该SPU商品审核中" + spuId);
			return CommonResult.failed("该SPU商品审核中");
		}
		return CommonResult.success(spuGoods);
    }
    
	/**
	 * 根据名称查找SPU商品是否正常
	 * @param spuName spu商品名称
	 */
    @SuppressWarnings("rawtypes")
	public CommonResult isSPUGoodsExistsByName(String spuName) {
    	SPUGoods spuGoods = spuGoodsService.getSPUGoodsByName(spuName);
    	
    	if (spuGoods == null) {
    		log.info("该SPU商品不存在" + spuName);
    		return CommonResult.validateFailed("该SPU商品不存在");
    	}
		return CommonResult.success(spuGoods);
    }
    
	/**
	 * 验证SPU商品参数是否一致
	 * @param spuId spu商品Id
	 */
    @SuppressWarnings("rawtypes")
	public CommonResult validationSPUGoods(Long spuId) {
    	SPUGoods spuGoods = spuGoodsService.getSPUGoodsById(spuId);
    	if (spuGoods == null) {
    		log.info("该SPU商品不存在");
    		return CommonResult.validateFailed("该SPU商品不存在");
    	}
    	
		if (!spuGoods.getVerifyStatus().equals(new Integer(2))) {
			log.info("该SPU商品正在审核");
			return CommonResult.failed("该SPU商品正在审核");
		}
		return CommonResult.success(spuGoods);
    }
    
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param spuName spu商品名称
	 */
	public InterProcessMutex setSPUGoodsLockByName(String spuName) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/spuGoods/getSPUGood/" + spuName);
		if (LockUtils.getLock(lockScope, "/spuGoods/getSPUGood/" + spuName) == null) {
			LockUtils.putLock(lockScope, "/spuGoods/getSPUGood/" + spuName, lock);
		}
		return lock;
	}
	
	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * @param spuName spu商品名称
	 */
	public InterProcessMutex getSPUGoodsLockByName(String spuName) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/spuGoods/getSPUGood/" + spuName);
		if (lock == null) {
			lock =  new InterProcessMutex(client, "/spuGoods/getSPUGood/" + spuName);
			LockUtils.putLock(lockScope, "/spuGoods/getSPUGood/" + spuName, lock);
		}
		return lock;
	}
}
