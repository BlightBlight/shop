package com.shop.goods.controller;

import java.math.BigDecimal;

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
import com.shop.goods.model.SKUGoods;
import com.shop.goods.model.SKUGoodsSpecValue;
import com.shop.goods.service.SKUGoodsService;
import com.shop.goods.vo.SaveSKUGoodsVo;
import com.shop.goods.vo.UpdateSKUGoodsVo;

import lombok.extern.slf4j.Slf4j;

/**
 * SKU商品控制层
 */
@Controller
@RequestMapping("/skuGoods")
@Slf4j
public class SKUGoodsController {
	@Autowired
	SPUGoodsController spuGoodsController;

	@Autowired
	SKUGoodsService skuGoodsService;

	@Autowired
	CuratorFramework client;

	@Value("${zookeeper.lockScope.skuGoods}")
	private String lockScope;

	/**
	 * 新增SKU商品
	 * 
	 * @throws Exception
	 */
	@PostMapping()
	@ResponseBody
	public CommonResult<String> saveSKUGoods(@RequestBody @Valid SaveSKUGoodsVo saveSKUGoodsVo) throws Exception {
		log.debug("待创建SKU商品Vo：" + saveSKUGoodsVo);

		// 如果需要确保SPU一定准确，则可以先取得SPU的分布锁在验证，但是我认为无意义
		@SuppressWarnings("rawtypes")
		CommonResult result = spuGoodsController.isSPUGoodsExistsById(saveSKUGoodsVo.getSpuId());
		if (result.getCode() != 200) {
			return result;
		}

		InterProcessMutex lock = getSKUGoodsLockByName(saveSKUGoodsVo.getSkuName());
		try {
			lock.acquire();

			result = isSKUGoodsExistsByName(saveSKUGoodsVo.getSkuName());
			if (result.getCode() == 200) {
				SKUGoods skuGoods = (SKUGoods) result.getData();
				if (!skuGoods.getVerifyStatus().equals(new Integer(3))) {
					log.info("该SKU商品审核中" + skuGoods.getSkuName());
					return CommonResult.failed("该SKU商品审核中");
				}
				log.debug("该SKU商品已存在" + skuGoods.getSkuName());
				return CommonResult.failed("该SKU商品已存在" + skuGoods.getSkuName());
			}
			skuGoodsService.saveSKUGoods(saveSKUGoodsVo);
			log.info("成功创建SKU商品");
			return CommonResult.success("成功创建SKU商品");
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
	 * 删除SKU商品
	 * 
	 * @throws Exception
	 */

	/*
	 * 其实这里有个坑，假设使用分布式读写锁无法实现写优先，也就意味着 可能商家由于设置错误，导致给薅羊毛，这时候商家想紧急下架商品
	 * 但是由于大量的读锁已经锁上了，所以导致写锁无法及时锁上 虽然采用公平锁可以止损，但是在写锁之前的读锁都已经无法挽回了
	 * 想要解决这个问题，我觉得需要额外的自己改框架代码 比如新增一个紧急删除方法，使用这个方法顺着节点往前一路删除节点 直到轮到本节点为止
	 * 所以分布式读写锁感觉按我的逻辑没多大用处，还是独占锁算了
	 */
	@DeleteMapping(value = "/{skuId}")
	@ResponseBody
	public CommonResult<String> removeSKUGoodsById(@RequestBody String jsonObject) throws Exception {
		String skuName = JacksonUtil.parseObject(jsonObject, String.class);
		log.debug("待删除SKU商品:" + skuName);

		InterProcessMutex lock = getSKUGoodsLockByName(skuName);
		try {
			lock.acquire();

			@SuppressWarnings("rawtypes")
			CommonResult result = isSKUGoodsExistsByName(skuName);
			if (result.getCode() != 200) {
				return result;
			}

			if (skuGoodsService.removeSKUGoodsByName(skuName)) {
				log.info("成功删除SKU商品");
				return CommonResult.success("成功删除SKU商品");
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
	 * 修改SKU商品
	 * 
	 * @throws Exception
	 */
	/*
	 * 修改商品A，锁A 成功，发现A被修改 若只有库存数量被修改了 则将新修改的库存数量加上被修改的库存数量，若小于等于0，则返回修改失败
	 * 如，商品A原有2件，A被购买2件，则被修改了-2件；新修改库存数量为1件，则1 + (-2) = -1，小于0，失败
	 * 如，商品A原有2件，A被购买2件，则被修改了-2件；新修改库存数量为4件，则4 + (-2) = 2，大于0，成功 否则返回修改后商品信息，重新修改。
	 * 成功，A未被修改，则直接修改。
	 */
	@PutMapping()
	@ResponseBody
	public CommonResult<String> updateSKUGoods(@RequestBody @Valid UpdateSKUGoodsVo updateSKUGoodsVo) throws Exception {
		log.debug("待修改SKU商品Vo：" + updateSKUGoodsVo.toString());

		InterProcessMutex lock = getSKUGoodsLockByName(updateSKUGoodsVo.getSkuName());
		try {
			lock.acquire();

			// 验证skuId是否存在
			@SuppressWarnings("rawtypes")
			CommonResult result = isSKUGoodsExistsById(updateSKUGoodsVo.getSkuId());
			if (result.getCode() != 200) {
				return result;
			}
			SKUGoods skuGoods = (SKUGoods) result.getData();
			// 数据库商品修改时间是否比上传修改时间后，按照上面注释做
			if (skuGoods.getUpdateTime() != null && skuGoods.getUpdateTime().isAfter(updateSKUGoodsVo.getUpdateTime())) {
				if (skuGoods.getShopId().equals(updateSKUGoodsVo.getShopId())
						&& skuGoods.getSpuId().equals(updateSKUGoodsVo.getSpuId())
						&& skuGoods.getSkuId().equals(updateSKUGoodsVo.getSkuId())
						&& skuGoods.getSkuName().equals(updateSKUGoodsVo.getSkuName())
						&& skuGoods.getPrice().compareTo(updateSKUGoodsVo.getPrice()) == 0) {
					// TODO:快照库存从页面获取不安全，但是暂时先这样，以后从数据库中根据日志找出距离修改时间最近一次的修改库存数量作为快照库存替代
					long nowStock = updateSKUGoodsVo.getNowStock().longValue();
					long stock = updateSKUGoodsVo.getStock().longValue();
					// 若快照库存大于缓存库存，说明被商品购买，使用快照库存减缓存库存即得到修改库存所要减少数量，若小于，则全部相反
					if (nowStock > skuGoods.getStock().longValue()) {
						stock = stock - (nowStock - skuGoods.getStock().longValue());
						if (stock < 0) {
							log.debug("修改后库存数量小于0，请重新再修改");
							return CommonResult.failed("修改后库存数量小于0，请重新再修改");
						}
					} else {
						stock = stock + (skuGoods.getStock().longValue() - nowStock);
					}
					updateSKUGoodsVo.setStock(stock);
				}
			}

			result = isSKUGoodsExistsByName(updateSKUGoodsVo.getSkuName());
			// skuName存在且修改的skuName与原skuName不相同
			if (result.getCode() == 200 && (!skuGoods.getSkuName().equals(updateSKUGoodsVo.getSkuName()))) {
				log.info("该SKU商品名称已存在：" + updateSKUGoodsVo);
				return CommonResult.failed("该SKU商品名称已存在");
			}

			if (skuGoodsService.updateSKUGoods(updateSKUGoodsVo)) {
				log.info("成功修改SKU商品");
				return CommonResult.success("成功修改SKU商品");
			} else {
				log.info("SKU商品表修改失败");
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
	 * 根据Id查找SKU商品
	 * 
	 * @param skuId sku商品Id
	 */
	// TODO:搜索业务交给Elasticsearch
	public SKUGoods getSKUGoodsById(Long skuId) {
		log.debug("根据skuId：" + skuId + "查找SKU商品");
		SKUGoods skuGoods = skuGoodsService.getSKUGoodsById(skuId);
		return skuGoods;
	}

	/**
	 * 根据specValueId查找所有SKU商品
	 * 
	 * @param specValueId 规格值Id
	 */
	public SKUGoods getSKUGoodsBySpecValueId(Long specValueId) {
		log.debug("根据specValueId：" + specValueId + "查找SKU商品");
		SKUGoods skuGoods = skuGoodsService.getSKUGoodsBySpecValueId(specValueId);
		return skuGoods;
	}
				 

	/**
	 * 根据Id查找规格值
	 * 
	 * @param specValueId 规格值Id
	 */
	public SKUGoodsSpecValue getSKUGoodsSpecValueById(Long specValueId) {
		log.debug("规格值Id：" + specValueId);
		SKUGoodsSpecValue skuGoodsSpecValue = skuGoodsService.getSKUGoodsSpecValueById(specValueId);
		return skuGoodsSpecValue;
	}

	/**
	 * 根据Id查找SKU商品是否存在
	 */
	@SuppressWarnings("rawtypes")
	public CommonResult isSKUGoodsExistsById(Long skuId) {
		SKUGoods skuGoods = skuGoodsService.getSKUGoodsById(skuId);

		if (skuGoods == null) {
			log.info("该SKU商品不存在" + skuId);
			return CommonResult.validateFailed("该SKU商品不存在");
		}

		if (!skuGoods.getVerifyStatus().equals(new Integer(2))) {
			log.info("该SKU商品审核中" + skuId);
			return CommonResult.failed("该SKU商品审核中");
		}

		CommonResult result = spuGoodsController.isSPUGoodsExistsById(skuGoods.getSpuId());
		if (result.getCode() != 200) {
			return result;
		}
		return CommonResult.success(skuGoods);
	}

	/**
	 * 根据名称查找SKU商品是否正常
	 * 
	 * @param skuName sku名称
	 */
	@SuppressWarnings("rawtypes")
	public CommonResult isSKUGoodsExistsByName(String skuName) {
		SKUGoods skuGoods = skuGoodsService.getSKUGoodsByName(skuName);
		if (skuGoods == null) {
			log.info("该SKU商品不存在" + skuName);
			return CommonResult.validateFailed("该SKU商品不存在" + skuName);
		}
		CommonResult result = spuGoodsController.isSPUGoodsExistsById(skuGoods.getSpuId());
		if (result.getCode() != 200) {
			return result;
		}
		return CommonResult.success(skuGoods);
	}

	/**
	 * 验证SKU商品数量用
	 */
	@SuppressWarnings("rawtypes")
	public CommonResult validationSKUGoodsByNumber(Long shopId, Long skuId, Long number) {
		return validationSKUGoods(shopId, skuId, number, null, null);
	}

	/**
	 * 验证SKU商品规格值用
	 */
	@SuppressWarnings("rawtypes")
	public CommonResult validationSKUGoodsBySpecValueId(Long shopId, Long skuId, Long specValueId) {
		return validationSKUGoods(shopId, skuId, null, null, specValueId);
	}

	/**
	 * 验证SKU商品参数是否一致
	 */
	@SuppressWarnings("rawtypes")
	public CommonResult validationSKUGoods(Long shopId, Long skuId, Long number, BigDecimal price, Long specValueId) {
		SKUGoods skuGoods = skuGoodsService.getSKUGoodsById(skuId);
		if (skuGoods == null) {
			log.debug("该SKU商品不存在" + skuId);
			return CommonResult.validateFailed("该SKU商品不存在");
		}

		if (!skuGoods.getVerifyStatus().equals(new Integer(2))) {
			log.debug("该SKU商品审核中" + skuId);
			return CommonResult.validateFailed("该SKU商品审核中");
		}

		if (!shopId.equals(skuGoods.getShopId())) {
			log.debug("传入shopId：" + shopId + "与缓存(数据库)shopId：" + skuGoods.getShopId() + "不符");
			return CommonResult.validateFailed("该SKU商品信息已更新，请刷新重试");
		}

		if (number != null && number.longValue() > skuGoods.getStock().longValue()) {
			log.debug("传入number：" + number.longValue() + "大于缓存number：" + skuGoods.getStock().longValue());
			return CommonResult.failed("购买数量超过库存");
		}

		if (price != null && price.compareTo(skuGoods.getPrice()) != 0) {
			log.debug("传入price：" + price + "与缓存(数据库)price：" + skuGoods.getPrice() + "不符");
			return CommonResult.validateFailed("该SKU商品信息已更新，请刷新重试");
		}

		if (specValueId != null && !specValueId.equals(skuGoods.getSpecValueId())) {
			log.debug("传入specValueId：" + specValueId + "与缓存(数据库)specValueId：" + skuGoods.getSpecValueId() + "不符");
			return CommonResult.validateFailed("该SKU商品信息已更新，请刷新重试");
		}

		if (specValueId != null && !specValueId.equals(skuGoods.getSpecValueId())) {
			log.debug("传入specValueId：" + specValueId + "与缓存(数据库)specValueId：" + skuGoods.getSpecValueId() + "不符");
			return CommonResult.validateFailed("该SKU商品信息已更新，请刷新重试");
		}

		CommonResult result = spuGoodsController.validationSPUGoods(skuGoods.getSpuId());
		if (result.getCode() != 200) {
			return result;
		}
		return CommonResult.success(skuGoods);
	}

	/**
	 * 生成一个新的分布锁，不管原先有无。 适用于需要对同一个锁上多个不同节点的情况。 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * 
	 * @param skuName sku商品名称
	 */
	public InterProcessMutex setSKUGoodsLockByName(String skuName) {
		InterProcessMutex lock = new InterProcessMutex(client, "/skuGoods/getSKUGoods/" + skuName);
		if (LockUtils.getLock(lockScope, "/skuGoods/getSKUGoods/" + skuName) == null) {
			LockUtils.putLock(lockScope, "/skuGoods/getSKUGoods/" + skuName, lock);
		}
		return lock;
	}

	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * 
	 * @param skuName sku商品名称
	 */
	public InterProcessMutex getSKUGoodsLockByName(String skuName) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/skuGoods/getSPUGood/" + skuName);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/skuGoods/getSKUGood/" + skuName);
			LockUtils.putLock(lockScope, "/skuGoods/getSKUGood/" + skuName, lock);
		}
		return lock;
	}
}
