package com.shop.goods.serviceImpl;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.common.exception.DAOException;
import com.shop.common.utils.LockUtils;
import com.shop.common.utils.SnowflakeIdUtil;
import com.shop.goods.dao.SKUGoodsDao;
import com.shop.goods.model.SKUGoodsSpecValue;
import com.shop.goods.model.SKUGoods;
import com.shop.goods.service.SKUGoodsCacheService;
import com.shop.goods.service.SKUGoodsService;
import com.shop.goods.service.SPUGoodsService;
import com.shop.goods.vo.SaveSKUGoodsVo;
import com.shop.goods.vo.UpdateSKUGoodsVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SKUGoodsServiceImpl implements SKUGoodsService{
	@Autowired
	SPUGoodsService spuGoodsService;
	
	@Autowired
	SKUGoodsCacheService skuGoodsCacheService;
	
	@Autowired
	SKUGoodsDao skuGoodsDao;
   
	@Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.skuGoods}")
    private String lockScope;
	
	SnowflakeIdUtil sf = new SnowflakeIdUtil(0, 0);
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public boolean saveSKUGoods(SaveSKUGoodsVo saveSKUGoodsVo) {
		SKUGoods skuGoods = new SKUGoods();
		skuGoods.setShopId(saveSKUGoodsVo.getShopId());
		skuGoods.setSpuId(saveSKUGoodsVo.getSpuId());
		skuGoods.setSkuId(sf.nextId());
		skuGoods.setSkuName(saveSKUGoodsVo.getSkuName());
		skuGoods.setStock(saveSKUGoodsVo.getStock());
		skuGoods.setPrice(saveSKUGoodsVo.getPrice());
		skuGoods.setSpecValueId(sf.nextId());
		skuGoods.setCreateTime(saveSKUGoodsVo.getCreateTime());
		skuGoods.setVerifyStatus(new Integer(0));
		skuGoods.setSkuStatus(new Integer(0));
		skuGoods.setDeleteStatus(new Integer(0));
		log.debug("存入goods_sku表实体：" + skuGoods.toString());
		
		SKUGoodsSpecValue skuGoodsSpecValue = new SKUGoodsSpecValue();
		skuGoodsSpecValue.setSpecId(saveSKUGoodsVo.getSpecId());
		skuGoodsSpecValue.setSpecValueId(skuGoods.getSpecValueId());
		skuGoodsSpecValue.setSpecValueName(saveSKUGoodsVo.getSpecValueName());
		skuGoodsSpecValue.setCreateTime(saveSKUGoodsVo.getCreateTime());
		skuGoodsSpecValue.setDeleteStatus(new Integer(0));
		log.debug("存入goods_spec_values表实体：" + skuGoodsSpecValue.toString());
		
		int effectRow = 0;
		// 插入goods_sku表
		try {
			effectRow = skuGoodsDao.saveSKUGoods(skuGoods);
			if (effectRow != 1) {
				throw new DAOException("插入goods_spu表异常");
			}
		} catch (DataAccessException e) {
			log.error("插入goods_spu表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("插入goods_spu表异常");
			throw new DAOException(e);
		}
		// 插入goods_spec_values表
		try {
			effectRow = skuGoodsDao.saveSKUGoodsSpecValue(skuGoodsSpecValue);
			if (effectRow != 1) {
				throw new DAOException("插入goods_spec_values表异常");
			}
			log.debug("根据skuName：" + skuGoods.getSkuName() + "设置SKUGoods缓存" + skuGoods.toString());
			skuGoodsCacheService.setSKUGoodsByName(skuGoods);
			log.debug("根据skuId：" + skuGoods.getSkuId() + "设置skuName：" + skuGoods.getSkuName() + "缓存");
			skuGoodsCacheService.setSKUGoodsNameById(skuGoods.getSkuId(), skuGoods.getSkuName());
			log.debug("根据specValueId：" + skuGoodsSpecValue.getSpecValueId() + "设置skuName：" + skuGoods.getSkuName() + "缓存");
			skuGoodsCacheService.setSKUGoodsNameBySpecValueId(skuGoodsSpecValue.getSpecValueId(), skuGoods.getSkuName());
			log.debug("根据specValueId：" + skuGoodsSpecValue.getSpecValueId() + "设置SKUGoodsSpecValue缓存" + skuGoodsSpecValue.toString());
			skuGoodsCacheService.setSKUGoodsSpecValueById(skuGoodsSpecValue);
		} catch (DataAccessException e) {
			log.error("插入goods_spec_values表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("插入goods_spec_values表异常");
			throw new DAOException(e);
		}
		return true;
	}

	@Override
	@Transactional(rollbackFor = DAOException.class)
	public boolean removeSKUGoodsByName(String skuName) {
		SKUGoods skuGoods = getSKUGoodsByName(skuName);

		InterProcessMutex lock = getSKUGoodsSpecValueLockById(skuGoods.getSpecValueId());
		int effectRow = 0;
		// 删除goods_spec_values表
		try {
			lock.acquire();
<<<<<<< HEAD
=======
			
			// 分布锁一锁上后，马上将缓存删除
			log.debug("根据skuName：" + skuGoods.getSkuName() + "删除SKUGoods缓存");
			skuGoodsCacheService.delSKUGoodsByName(skuGoods.getSkuName());
			log.debug("根据skuId：" + skuGoods.getSpecValueId() + "删除skuName：" + skuGoods.getSkuName() + "缓存");
			skuGoodsCacheService.delSKUGoodsNameById(skuGoods.getSkuId());
			log.debug("根据specValueId：" + skuGoods.getSpecValueId() + "删除skuName：" + skuGoods.getSkuName() + "缓存");
			skuGoodsCacheService.delSKUGoodsSpecValueById(skuGoods.getSpecValueId());
			log.debug("根据specValueId：" + skuGoods.getSpecValueId() + "删除SKUGoodsSpecValue缓存");
			skuGoodsCacheService.delSKUGoodsSpecValueById(skuGoods.getSpecValueId());
			
>>>>>>> 2089505... 第二次提交
			effectRow = skuGoodsDao.removeSKUGoodsSpecValueById(skuGoods.getSpecValueId());
			if (effectRow != 1) {
				throw new DAOException("修改goods_spec_values表异常");
			}
		} catch (DataAccessException e) {
			log.error("修改goods_spec_values表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改goods_spec_values表异常");
			throw new DAOException(e);
		} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new DAOException(e);
		} finally {
			try {
				if (lock != null && lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
			}
		}
		// 删除goods_sku表
		try {
			effectRow = skuGoodsDao.removeSKUGoodsByName(skuName);
			if (effectRow != 1) {
				throw new DAOException("修改goods_sku表异常");
			}
<<<<<<< HEAD
			log.debug("根据skuName：" + skuGoods.getSkuName() + "删除SKUGoods缓存");
			skuGoodsCacheService.delSKUGoodsByName(skuGoods.getSkuName());
			log.debug("根据skuId：" + skuGoods.getSpecValueId() + "删除skuName：" + skuGoods.getSkuName() + "缓存");
			skuGoodsCacheService.delSKUGoodsNameById(skuGoods.getSkuId());
			log.debug("根据specValueId：" + skuGoods.getSpecValueId() + "删除skuName：" + skuGoods.getSkuName() + "缓存");
			skuGoodsCacheService.delSKUGoodsSpecValueById(skuGoods.getSpecValueId());
			log.debug("根据specValueId：" + skuGoods.getSpecValueId() + "删除SKUGoodsSpecValue缓存");
			skuGoodsCacheService.delSKUGoodsSpecValueById(skuGoods.getSpecValueId());
=======
>>>>>>> 2089505... 第二次提交
		} catch (DataAccessException e) {
			log.error("修改goods_sku表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改goods_sku表异常");
			throw new DAOException(e);
		}
		return true;
	}
	
	@Override
	public boolean updateSKUGoods(UpdateSKUGoodsVo updateSKUGoodsVo) {
<<<<<<< HEAD
		SKUGoodsSpecValue skuGoodsSpecValue = getSKUGoodsSpecValueById(updateSKUGoodsVo.getSpecValueId());
		
		int effectRow = 0;
		// 规格值有变化则修改规格值表
		if (!skuGoodsSpecValue.getSpecValueName().equals(updateSKUGoodsVo.getSpecValueName())) {
			SKUGoodsSpecValue databaseskuGoodsSpecValue = new SKUGoodsSpecValue();
			databaseskuGoodsSpecValue.setSpecId(updateSKUGoodsVo.getSpecId());
			databaseskuGoodsSpecValue.setSpecValueId(updateSKUGoodsVo.getSpecValueId());
			databaseskuGoodsSpecValue.setSpecValueName(updateSKUGoodsVo.getSpecValueName());
			databaseskuGoodsSpecValue.setUpdateTime(updateSKUGoodsVo.getUpdateTime());
			log.debug("存入goods_spec_values表实体：" + databaseskuGoodsSpecValue.toString());
			
			InterProcessMutex lock = getSKUGoodsSpecValueLockById(databaseskuGoodsSpecValue.getSpecValueId());
			try {
				lock.acquire();
				effectRow = skuGoodsDao.updateSKUGoodsSpecValue(databaseskuGoodsSpecValue);
				if (effectRow != 1) {
					throw new DAOException("修改goods_spec_values表异常");
				}
				log.debug("根据specValueId：" + databaseskuGoodsSpecValue.getSpecValueId() + "删除SKUGoodsSpecValue缓存");
				skuGoodsCacheService.delSKUGoodsSpecValueById(databaseskuGoodsSpecValue.getSpecValueId());
			} catch (DataAccessException e) {
				log.error("修改goods_spec_values表异常");
				throw new DAOException(e);
			} catch (DAOException e) {
				log.error("修改goods_spec_values表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error(Thread.currentThread() + "不明异常");
				throw new DAOException(e);
			} finally {
				try {
					if (lock.isOwnedByCurrentThread()) {
						lock.release();
					}
				} catch (Exception e) {
					log.error(Thread.currentThread() + "释放锁出现BUG");
				}
			}
		}
		
		SKUGoods skuGoods = getSKUGoodsById(updateSKUGoodsVo.getSkuId());
		//将旧sku商品名称锁起来
		InterProcessMutex lock = null;
		if (!skuGoods.getSkuName().equals(updateSKUGoodsVo.getSkuName())) {
			lock = getSKUGoodsLockByName(skuGoods.getSkuName());
		}
		
=======
		SKUGoods skuGoods = getSKUGoodsById(updateSKUGoodsVo.getSkuId());
>>>>>>> 2089505... 第二次提交
		SKUGoods tempskuGoods = new SKUGoods();
		tempskuGoods.setSkuId(updateSKUGoodsVo.getSkuId());
		tempskuGoods.setSpuId(updateSKUGoodsVo.getSpuId());
		tempskuGoods.setSpecValueId(updateSKUGoodsVo.getSpecValueId());
		tempskuGoods.setShopId(updateSKUGoodsVo.getShopId());
		tempskuGoods.setSkuName(updateSKUGoodsVo.getSkuName());
		tempskuGoods.setPrice(updateSKUGoodsVo.getPrice());
		tempskuGoods.setStock(updateSKUGoodsVo.getStock());
		tempskuGoods.setUpdateTime(updateSKUGoodsVo.getUpdateTime());
		log.debug("存入goods_sku表实体：" + tempskuGoods.toString());
		
<<<<<<< HEAD
=======
		//将旧sku商品名称锁起来
		InterProcessMutex lock = null;
		if (!skuGoods.getSkuName().equals(updateSKUGoodsVo.getSkuName())) {
			lock = getSKUGoodsLockByName(skuGoods.getSkuName());
		}
		
		int effectRow = 0;
>>>>>>> 2089505... 第二次提交
		try {
			if (lock != null) {
				lock.acquire();
			}
<<<<<<< HEAD
			effectRow = skuGoodsDao.updateSKUGoods(tempskuGoods);
			if (effectRow != 1) {
				throw new DAOException("修改goods_spec_values表异常");
			}
=======
			
			// 分布锁一锁上后，马上将缓存删除
>>>>>>> 2089505... 第二次提交
			log.debug("根据skuName：" + skuGoods.getSkuName() + "删除SKUGoods缓存");
			skuGoodsCacheService.delSKUGoodsByName(skuGoods.getSkuName());
			log.debug("根据skuId：" + skuGoods.getSkuId() + "删除skuName：" + skuGoods.getSkuName() + "缓存");
			skuGoodsCacheService.delSKUGoodsNameById(skuGoods.getSkuId());
			log.debug("根据specValueId：" + skuGoods.getSpecValueId() + "删除skuName：" + skuGoods.getSkuName() + "缓存");
			skuGoodsCacheService.delSKUGoodsNameBySpecValueId(skuGoods.getSpecValueId());
<<<<<<< HEAD
=======
			
			effectRow = skuGoodsDao.updateSKUGoods(tempskuGoods);
			if (effectRow != 1) {
				throw new DAOException("修改goods_spec_values表异常");
			}
>>>>>>> 2089505... 第二次提交
		} catch (DataAccessException e) {
			log.error("修改goods_sku表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改goods_sku表异常");
			throw new DAOException(e);
		} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new DAOException(e);
		} finally {
			try {
				if (lock != null && lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
			}
		}
<<<<<<< HEAD
=======
		
		SKUGoodsSpecValue skuGoodsSpecValue = getSKUGoodsSpecValueById(updateSKUGoodsVo.getSpecValueId());
		
		// 规格值有变化则修改规格值表
		if (!skuGoodsSpecValue.getSpecValueName().equals(updateSKUGoodsVo.getSpecValueName())) {
			SKUGoodsSpecValue databaseskuGoodsSpecValue = new SKUGoodsSpecValue();
			databaseskuGoodsSpecValue.setSpecId(updateSKUGoodsVo.getSpecId());
			databaseskuGoodsSpecValue.setSpecValueId(updateSKUGoodsVo.getSpecValueId());
			databaseskuGoodsSpecValue.setSpecValueName(updateSKUGoodsVo.getSpecValueName());
			databaseskuGoodsSpecValue.setUpdateTime(updateSKUGoodsVo.getUpdateTime());
			log.debug("存入goods_spec_values表实体：" + databaseskuGoodsSpecValue.toString());
			
			lock = getSKUGoodsSpecValueLockById(databaseskuGoodsSpecValue.getSpecValueId());
			try {
				lock.acquire();
				
				// 分布锁一锁上后，马上将缓存删除
				log.debug("根据specValueId：" + databaseskuGoodsSpecValue.getSpecValueId() + "删除SKUGoodsSpecValue缓存");
				skuGoodsCacheService.delSKUGoodsSpecValueById(databaseskuGoodsSpecValue.getSpecValueId());
				
				effectRow = skuGoodsDao.updateSKUGoodsSpecValue(databaseskuGoodsSpecValue);
				if (effectRow != 1) {
					throw new DAOException("修改goods_spec_values表异常");
				}
			} catch (DataAccessException e) {
				log.error("修改goods_spec_values表异常");
				throw new DAOException(e);
			} catch (DAOException e) {
				log.error("修改goods_spec_values表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error(Thread.currentThread() + "不明异常");
				throw new DAOException(e);
			} finally {
				try {
					if (lock.isOwnedByCurrentThread()) {
						lock.release();
					}
				} catch (Exception e) {
					log.error(Thread.currentThread() + "释放锁出现BUG");
				}
			}
		}
>>>>>>> 2089505... 第二次提交
		return true;
	}
	
	@Override
	public boolean decrSKUGoodsByName(String skuName, Long delta) {
		InterProcessMutex lock = setSKUGoodsLockByName(skuName);
		try {
			lock.acquire();
			
			SKUGoods skuGoods = skuGoodsCacheService.getSKUGoodsByName(skuName);
			if (skuGoods == null) {
				log.debug("根据skuName：" + skuName + "找不到相应SKUGoods缓存");
				return false;
			}
			if (skuGoods.getStock().longValue() < delta.longValue()) {
				log.debug("SKU商品库存不足，请重新购买");
				return false;
			}
			skuGoods.setStock(skuGoods.getStock() - delta);
			int effectRow = skuGoodsDao.decrSKUGoodsByName(skuName, delta);
			if (effectRow != 1) {
				throw new DAOException("修改goods_sku表异常");
			}
			log.debug("根据skuName：" + skuGoods.getSkuName() + "设置SKUGoods缓存" + skuGoods.toString());
			skuGoodsCacheService.setSKUGoodsByName(skuGoods);
		} catch (DataAccessException e) {
			log.error("修改goods_sku表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改goods_sku表异常");
			throw new DAOException(e);
		} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new DAOException(e);
		} finally {
			try {
				if (lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
			}
		}
		return true;
	}
	
	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public SKUGoods getSKUGoodsByName(String skuName) {
		SKUGoods skuGoods = skuGoodsCacheService.getSKUGoodsByName(skuName);
		if (skuGoods == null) {
			InterProcessMutex lock = getSKUGoodsLockByName(skuName);
			try {
				if (lock.acquire(3L, TimeUnit.SECONDS)) {
					skuGoods = skuGoodsDao.getSKUGoodsByName(skuName, new Integer(0));
					if (skuGoods != null) {
						if (skuGoods.getDeleteStatus().intValue() == 2) {
							log.debug("根据skuName：" + skuName + "查找出SKUGoods实体已删除" + skuGoods.toString());
							return null;
						} else {
							log.debug("根据skuName：" + skuName + "查找出SKUGoods实体为：" + skuGoods.toString());
							log.debug("根据skuName：" + skuName + "设置SKUGoods缓存" + skuGoods.toString());
							skuGoodsCacheService.setSKUGoodsByName(skuGoods);
							return skuGoods;
						}
					}
					return null;
				} else {
					skuGoods = skuGoodsCacheService.getSKUGoodsByName(skuName);
					if (skuGoods == null) {
						log.debug("重试失败");
						throw new DAOException("SKUGoods查找重试失败");
					}
					return skuGoods;
				}
			} catch (DataAccessException e) {
				log.error("查找goods_sku表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error(Thread.currentThread() + "不明异常");
				throw new DAOException(e);
			} finally {
				try {
					if (lock.isOwnedByCurrentThread()) {
						lock.release();
					}
				} catch (Exception e) {
					log.error(Thread.currentThread() + "释放锁出现BUG");
				}
			}
		}
		return skuGoods;
	}
	
	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public SKUGoods getSKUGoodsById(Long skuId) {
		String skuName = skuGoodsCacheService.getSKUGoodsNameById(skuId);
		if (skuName == null) {
			InterProcessMutex lock = getSKUGoodsLockById(skuId);
			try {
				if (lock.acquire(3L, TimeUnit.SECONDS)) {
					SKUGoods skuGoods = skuGoodsDao.getSKUGoodsById(skuId, new Integer(0));
					if (skuGoods != null) {
						if (skuGoods.getDeleteStatus().intValue() == 2) {
							log.debug("根据skuId：" + skuId + "查找出SKUGoods实体已删除" + skuGoods.toString());
							return null;
						} else {
							log.debug("根据Id：" + skuId + "查找出SKUGoods实体为：" + skuGoods.toString());
							log.debug("根据Id：" + skuId + "设置skuName：" + skuGoods.getSkuName() + "缓存");
							skuGoodsCacheService.setSKUGoodsNameById(skuId, skuGoods.getSkuName());
							return getSKUGoodsByName(skuGoods.getSkuName());
						}
					}
					return null;
				} else {
					skuName = skuGoodsCacheService.getSKUGoodsNameById(skuId);
					if (skuName == null) {
						log.debug("重试失败");
						throw new DAOException("skuName查找重试失败");
					}
					return getSKUGoodsByName(skuName);
				}
			} catch (DataAccessException e) {
				log.error("查找goods_sku表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error(Thread.currentThread() + "不明异常");
				throw new DAOException(e);
			} finally {
				try {
					if (lock.isOwnedByCurrentThread()) {
						lock.release();
					}
				} catch (Exception e) {
					log.error(Thread.currentThread() + "释放锁出现BUG");
					throw new DAOException(e);
				}
			}
		}
		return getSKUGoodsByName(skuName);
	}
	
	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public SKUGoods getSKUGoodsBySpecValueId(Long specValueId) {
		String skuName = skuGoodsCacheService.getSKUGoodsNameBySpecValueId(specValueId);
		if (skuName == null) {
			InterProcessMutex lock = getSKUGoodsLockBySpecValueId(specValueId);
			try {
				if (lock.acquire(3L, TimeUnit.SECONDS)) {
					SKUGoods skuGoods = skuGoodsDao.getSKUGoodsBySpecValueId(specValueId, new Integer(0));
					if (skuGoods != null) {
						if (skuGoods.getDeleteStatus().intValue() == 2) {
							log.debug("根据specValueId：" + specValueId + "查找出SKUGoods实体已删除" + skuGoods.toString());
							return null;
						} else {
							log.debug("根据specValueId：" + specValueId + "查找出SKUGoods实体为：" + skuGoods.toString());
							log.debug("根据specValueId：" + specValueId + "设置skuName：" + skuGoods.getSkuName() + "缓存");
							skuGoodsCacheService.setSKUGoodsNameBySpecValueId(specValueId, skuGoods.getSkuName());
							return getSKUGoodsByName(skuGoods.getSkuName());
						}
					}
					return null;
				} else {
					skuName = skuGoodsCacheService.getSKUGoodsNameBySpecValueId(specValueId);
					if (skuName == null) {
						log.debug("根据specValueId查找SKU商品重试");
						throw new DAOException("skuName查找重试失败");
					}
					return getSKUGoodsByName(skuName);
				}
			} catch (DataAccessException e) {
				log.error("查找goods_sku表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error(Thread.currentThread() + "不明异常");
				throw new DAOException(e);
			} finally {
				try {
					if (lock.isOwnedByCurrentThread()) {
						lock.release();
					}
				} catch (Exception e) {
					log.error(Thread.currentThread() + "释放锁出现BUG");
					throw new DAOException(e);
				}
			}
		}
		return getSKUGoodsByName(skuName);
	}
	
	@Recover
	public int recover(DAOException e) {
		log.debug("SKU查找重试3次失败！！！");
		throw new DAOException(e);
	}
	
	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public SKUGoodsSpecValue getSKUGoodsSpecValueById(Long specValueId) {
		SKUGoodsSpecValue skuGoodsSpecValue = skuGoodsCacheService.getSKUGoodsSpecValueById(specValueId);
		if (skuGoodsSpecValue == null) {
			InterProcessMutex lock = getSKUGoodsSpecValueLockById(specValueId);
			try {
				if (lock.acquire(3L, TimeUnit.SECONDS)) {
					skuGoodsSpecValue = skuGoodsDao.getSKUGoodsSpecValueById(specValueId, new Integer(0));
					if (skuGoodsSpecValue != null) {
						if (skuGoodsSpecValue.getDeleteStatus().intValue() == 2) {
							log.debug("根据specValueId：" + specValueId + "查找出SKUGoodsSpecValue实体已删除" + skuGoodsSpecValue.toString());
							return null;
						} else {
							log.debug("根据specValueId：" + specValueId + "查找出SKUGoodsSpecValue实体为：" + skuGoodsSpecValue.toString());
							log.debug("根据specValueId：" + specValueId + "设置SKUGoodsSpecValue缓存" + skuGoodsSpecValue.toString());
							skuGoodsCacheService.setSKUGoodsSpecValueById(skuGoodsSpecValue);
							return skuGoodsSpecValue;
						}
					}
					return null;
				} else {
					skuGoodsSpecValue = skuGoodsCacheService.getSKUGoodsSpecValueById(specValueId);
					if (skuGoodsSpecValue == null) {
						log.debug("重试失败");
						throw new DAOException("skuGoodsSpecValue查找重试失败");
					}
					return skuGoodsSpecValue;
				}
			} catch (DataAccessException e) {
				log.error("查找goods_spec_values表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error(Thread.currentThread() + "不明异常");
				throw new DAOException(e);
			} finally {
				try {
					if (lock.isOwnedByCurrentThread()) {
						lock.release();
					}
				} catch (Exception e) {
					log.error(Thread.currentThread() + "释放锁出现BUG");
				}
			}
		}
		return skuGoodsSpecValue;
	}
	
	
	@Override
	public InterProcessMutex setSKUGoodsLockByName(String spuName) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/skuGoods/getSKUGood/" + spuName);
		if (LockUtils.getLock(lockScope, "/skuGoods/getSKUGood/" + spuName) == null) {
			LockUtils.putLock(lockScope, "/skuGoods/getSKUGood/" + spuName, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getSKUGoodsLockByName(String skuName) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/skuGoods/getSKUGood/" + skuName);
		if (lock == null) {
			lock =  new InterProcessMutex(client, "/skuGoods/getSKUGood/" + skuName);
			LockUtils.putLock(lockScope, "/skuGoods/getSKUGood/" + skuName, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getSKUGoodsLockById(Long skuId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/skuGoods/getSKUGood/" + skuId);
		if (lock == null) {
			lock =  new InterProcessMutex(client, "/skuGoods/getSKUGood/" + skuId);
			LockUtils.putLock(lockScope, "/skuGoods/getSKUGood/" + skuId, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getSKUGoodsLockBySpecValueId(Long specValueId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/skuGoods/getSKUGood/" + specValueId);
		if (lock == null) {
			lock =  new InterProcessMutex(client, "/skuGoods/getSKUGood/" + specValueId);
			LockUtils.putLock(lockScope, "/skuGoods/getSKUGood/" + specValueId, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getSKUGoodsSpecValueLockById(Long specValueId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/skuGoods/getSKUGoodsSpecValue/" + specValueId);
		if (lock == null) {
			lock =  new InterProcessMutex(client, "/skuGoods/getSKUGoodsSpecValue/" + specValueId);
			LockUtils.putLock(lockScope, "/skuGoods/getSKUGoodsSpecValue/" + specValueId, lock);
		}
		return lock;
	}

}
