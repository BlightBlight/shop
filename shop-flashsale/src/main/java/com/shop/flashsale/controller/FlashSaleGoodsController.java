package com.shop.flashsale.controller;

import javax.validation.Valid;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.result.CommonResult;
import com.shop.common.utils.JacksonUtil;
import com.shop.common.utils.LockUtils;
import com.shop.flashsale.model.FlashSaleGoods;
import com.shop.flashsale.service.FlashSaleGoodsService;
import com.shop.flashsale.vo.SaveFlashSaleGoodsVo;
import com.shop.flashsale.vo.UpdateFlashSaleGoodsVo;
import com.shop.goods.controller.SPUGoodsController;
import com.shop.goods.model.SPUGoods;

import lombok.extern.slf4j.Slf4j;

/**
 * 秒杀商品控制层
 */
@RequestMapping("/flashSaleGoods")
@Controller
@Slf4j
public class FlashSaleGoodsController {
	@Autowired
	FlashSaleGoodsService flashSaleGoodsService;
	
	@Autowired
	SPUGoodsController spuGoodsController;
	
	@Autowired
	CuratorFramework client;

    @Value("${zookeeper.lockScope.flashsales}")
    private String lockScope;
	
	/**
	 * 新增秒杀商品
	 * @throws Exception
	 */
	@PostMapping()
	@ResponseBody
	public CommonResult<String> saveFlashSaleGoods(@RequestBody @Valid SaveFlashSaleGoodsVo saveFlashSaleGoodsVo)
			throws Exception {
		log.debug("待创建秒杀商品Vo：" + saveFlashSaleGoodsVo.toString());

		@SuppressWarnings("rawtypes")
		CommonResult result = spuGoodsController.isSPUGoodsExistsById(saveFlashSaleGoodsVo.getSpuId());
		if (result.getCode() != 200) {
			return result;
		}
		SPUGoods spuGoods = (SPUGoods) result.getData();
		if (!spuGoods.getVerifyStatus().equals(new Integer(2))) {
			log.info("该SPU商品正在审核");
			return CommonResult.failed("该SPU商品正在审核");
		}
		
		InterProcessMutex lock = getFlashSaleGoodsLockByName(saveFlashSaleGoodsVo.getFlashSaleGoodsName());
		try {
			lock.acquire();
			
			result = isFlashSaleGoodsExistsByName(saveFlashSaleGoodsVo.getFlashSaleGoodsName());
			if (result.getCode() == 200) {
				FlashSaleGoods flashSaleGoods = (FlashSaleGoods) result.getData();
				if (flashSaleGoods.getVerifyStatus().equals(new Integer(2))) {
					log.info("该秒杀商品审核中" + saveFlashSaleGoodsVo.getFlashSaleGoodsName());
					return CommonResult.failed("该秒杀商品审核中");
				}
				log.info("该秒杀商品已存在" + saveFlashSaleGoodsVo.getFlashSaleGoodsName());
				return CommonResult.failed("该秒杀商品已存在");
			}

			if (flashSaleGoodsService.saveFlashSaleGoods(saveFlashSaleGoodsVo)) {
				log.info("成功创建秒杀商品");
				return CommonResult.success("成功创建秒杀商品");
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
	 * 删除秒杀商品
	 * @throws Exception
	 */
	@DeleteMapping()
	@ResponseBody
	public CommonResult<String> removeFlashSaleGoodsByName(@RequestBody String jsonObject) throws Exception {
		String flashSaleGoodsName = JacksonUtil.parseObject(jsonObject, String.class);
		log.debug("删除秒杀商品:" + flashSaleGoodsName);

		InterProcessMutex lock = getFlashSaleGoodsLockByName(flashSaleGoodsName);
		try {
			lock.acquire();

			@SuppressWarnings("rawtypes")
			CommonResult result = isFlashSaleGoodsExistsByName(flashSaleGoodsName);
			if (result.getCode() != 200) {
				return result;
			}

			if (flashSaleGoodsService.removeFlashSaleGoodsByName(flashSaleGoodsName)) {
				log.info("成功删除秒杀商品");
				return CommonResult.success("成功删除秒杀商品");
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
	 * 修改秒杀商品
	 * @throws Exception 
	 */
	@PutMapping(value = "/{flashSaleGoodsId}")
	@ResponseBody
	public CommonResult<String> updateFlashSaleGoodsById(@RequestBody @Valid UpdateFlashSaleGoodsVo updatelashSaleGoodsVo) throws Exception {
		log.debug("待修改秒杀商品Vo：" + updatelashSaleGoodsVo.toString());
		
		InterProcessMutex lock = getFlashSaleGoodsLockByName(updatelashSaleGoodsVo.getFlashSaleGoodsName());
		try {
			lock.acquire();
			
			// 验证flashSaleGoodsId是否存在
			@SuppressWarnings("rawtypes")
			CommonResult result = isFlashSaleGoodsExistsById(updatelashSaleGoodsVo.getFlashSaleGoodsId());
			if (result.getCode() != 200) {
				return result;
			}
			FlashSaleGoods flashSaleGoods = (FlashSaleGoods) result.getData();
			// 验证flashSaleGoodsName是否存在
			result = isFlashSaleGoodsExistsByName(updatelashSaleGoodsVo.getFlashSaleGoodsName());
			
			//flashSaleGoodsName存在且修改的flashSaleGoodsName与原flashSaleGoodsName不相同
			if (result.getCode() == 200 && (!flashSaleGoods.getFlashSaleGoodsName().equals(updatelashSaleGoodsVo.getFlashSaleGoodsName()))) {
				log.info("该秒杀商品名称已存在：" + updatelashSaleGoodsVo);
				return CommonResult.failed("该秒杀商品名称已存在");
			}
			
			// 验证修改库存是否有问题
			if (flashSaleGoods.getUpdateTime() != null && flashSaleGoods.getUpdateTime().isAfter(updatelashSaleGoodsVo.getUpdateTime())) {
				if (flashSaleGoods.getShopId().equals(updatelashSaleGoodsVo.getShopId())
						&& flashSaleGoods.getSpuId().equals(updatelashSaleGoodsVo.getSpuId())
						&& flashSaleGoods.getFlashSaleGoodsId().equals(updatelashSaleGoodsVo.getFlashSaleGoodsId())
						&& flashSaleGoods.getFlashSaleGoodsName().equals(updatelashSaleGoodsVo.getFlashSaleGoodsName())
						&& flashSaleGoods.getPrice().compareTo(updatelashSaleGoodsVo.getPrice()) == 0) {
					// TODO:快照库存从页面获取不安全，但是暂时先这样，以后从数据库中根据日志找出距离修改时间最近一次的修改库存数量作为快照库存替代
					long nowStock = updatelashSaleGoodsVo.getNowStock().longValue();
					long stock = updatelashSaleGoodsVo.getStock().longValue();
					// 若快照库存大于缓存库存，说明被商品购买，使用快照库存减缓存库存即得到修改库存所要减少数量，若小于，则全部相反
					if (nowStock > flashSaleGoods.getStock().longValue()) {
						stock = stock - (nowStock - flashSaleGoods.getStock().longValue());
						if (stock < 0) {
							log.debug("修改后库存数量小于0，请重新再修改");
							return CommonResult.failed("修改后库存数量小于0，请重新再修改");
						}
					} else {
						stock = stock + (flashSaleGoods.getStock().longValue() - nowStock);
					}
					updatelashSaleGoodsVo.setStock(stock);
				}
			}
			
			if (flashSaleGoodsService.updateFlashSaleGoods(updatelashSaleGoodsVo)) {
				log.info("成功修改秒杀商品");
				return CommonResult.success("成功修改秒杀商品");
			} else {
				log.info("秒杀商品表修改失败");
				return CommonResult.internalServerFailed();
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
	}

	/**
	 * 根据Id查找秒杀商品
	 */
	@GetMapping(value = "/{flashSaleGoodsId}")
	@ResponseBody
	public CommonResult<String> getFlashSaleGoodsById(@PathVariable Long flashSaleGoodsId) {
		return CommonResult.internalServerFailed();
	}

	/**
	 * 根据Id查找秒杀商品是否正常
	 */
	@SuppressWarnings("rawtypes")
	public CommonResult isFlashSaleGoodsExistsById(Long flashSaleGoodsId) {
		FlashSaleGoods flashSaleGoods = flashSaleGoodsService.getFlashSaleGoodsById(flashSaleGoodsId);

		if (flashSaleGoods == null) {
			log.info("该秒杀商品不存在" + flashSaleGoodsId);
			return CommonResult.validateFailed("该秒杀商品不存在");
		}

		if (flashSaleGoods.getVerifyStatus() != 2) {
			log.info("该秒杀商品审核中" + flashSaleGoodsId);
			return CommonResult.failed("该秒杀商品审核中");
		}
		return CommonResult.success(flashSaleGoods);
	}

	/**
	 * 根据名称查找秒杀商品是否正常
	 */
    @SuppressWarnings("rawtypes")
	public CommonResult isFlashSaleGoodsExistsByName(String flashSaleGoodsName){
    	FlashSaleGoods flashSaleGoods = flashSaleGoodsService.getFlashSaleGoodsByName(flashSaleGoodsName);
    	
    	if (flashSaleGoods == null) {
    		log.info("该秒杀商品不存在" + flashSaleGoodsName);
    		return CommonResult.validateFailed("该秒杀商品不存在");
    	}
    	
		return CommonResult.success(flashSaleGoods);
    }
    
    
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于需要对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param flashSaleGoodsName 秒杀商品名称
	 */
	public InterProcessMutex setFlashSaleGoodsLockByName(String flashSaleGoodsName) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/flashSales/getFlashSaleGood/" + flashSaleGoodsName);
		if (LockUtils.getLock(lockScope, "/flashSales/getFlashSaleGood/" + flashSaleGoodsName) == null) {
			LockUtils.putLock(lockScope, "/flashSales/getFlashSaleGood/" + flashSaleGoodsName, lock);
		}
		return lock;
	}
	
	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * @param flashSaleGoodsName 秒杀商品名称
	 */
	public InterProcessMutex getFlashSaleGoodsLockByName(String flashSaleGoodsName) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/flashsales/getFlashSaleGood/" + flashSaleGoodsName);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/flashSales/getFlashSaleGood/" + flashSaleGoodsName);
			LockUtils.putLock(lockScope, "/flashSales/getFlashSaleGood/" + flashSaleGoodsName, lock);
		}
		return lock;
	}
	
	
}
