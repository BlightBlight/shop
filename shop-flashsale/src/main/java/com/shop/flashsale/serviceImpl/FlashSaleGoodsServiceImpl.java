package com.shop.flashsale.serviceImpl;

import java.util.List;
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
import com.shop.flashsale.dao.FlashSaleGoodsDao;
import com.shop.flashsale.model.FlashSaleGoods;
import com.shop.flashsale.service.FlashSaleGoodsCacheService;
import com.shop.flashsale.service.FlashSaleGoodsService;
import com.shop.flashsale.vo.SaveFlashSaleGoodsVo;
import com.shop.flashsale.vo.UpdateFlashSaleGoodsVo;
import com.shop.goods.model.SKUGoodsSpecValue;
import com.shop.goods.service.SKUGoodsCacheService;
import com.shop.goods.service.SKUGoodsService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FlashSaleGoodsServiceImpl implements FlashSaleGoodsService {
	@Autowired
	SKUGoodsCacheService skuGoodsCacheService;
	
	@Autowired
	SKUGoodsService skuGoodsService;
	
	@Autowired
	FlashSaleGoodsCacheService flashSaleGoodsCacheService;
	
	@Autowired
	FlashSaleGoodsDao flashSaleGoodsDao;
	
	@Autowired
	CuratorFramework client;

    @Value("${zookeeper.lockScope.flashsales}")
    private String lockScope;
	
	// 这里可以改进，对Snowflake的部署应该要考虑到分布式，不过鉴于目前只有一台机器。。。
	SnowflakeIdUtil sf = new SnowflakeIdUtil(0, 0);
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public boolean saveFlashSaleGoods(SaveFlashSaleGoodsVo saveFlashSaleGoodsVo) {
		FlashSaleGoods flashSaleGoods = new FlashSaleGoods();
		flashSaleGoods.setShopId(saveFlashSaleGoodsVo.getShopId());
		flashSaleGoods.setSpuId(saveFlashSaleGoodsVo.getSpuId());
		flashSaleGoods.setFlashSaleGoodsId(sf.nextId());
		flashSaleGoods.setFlashSaleGoodsName(saveFlashSaleGoodsVo.getFlashSaleGoodsName());
		flashSaleGoods.setStock(saveFlashSaleGoodsVo.getStock());
		flashSaleGoods.setPrice(saveFlashSaleGoodsVo.getPrice());
		flashSaleGoods.setSpecValueId(sf.nextId());
		flashSaleGoods.setCreateTime(saveFlashSaleGoodsVo.getCreateTime());
		flashSaleGoods.setVerifyStatus(new Integer(1));
		flashSaleGoods.setFlashSaleGoodsStatus(new Integer(1));
		flashSaleGoods.setDeleteStatus(new Integer(1));
		log.debug("存入flaslsale_goods表实体：" + flashSaleGoods.toString());
		
		//这里暂且用回原来的规格值表，以后看需不需要改成单独规格值表
		SKUGoodsSpecValue skuGoodsSpecValue = new SKUGoodsSpecValue();
		skuGoodsSpecValue.setSpecId(saveFlashSaleGoodsVo.getSpecId());
		skuGoodsSpecValue.setSpecValueId(flashSaleGoods.getSpecValueId());
		skuGoodsSpecValue.setSpecValueName(saveFlashSaleGoodsVo.getSpecValueName());
		skuGoodsSpecValue.setCreateTime(saveFlashSaleGoodsVo.getCreateTime());
		skuGoodsSpecValue.setDeleteStatus(new Integer(0));
		log.debug("存入goods_spec_values表实体：" + skuGoodsSpecValue.toString());
		
		int effectRow = 0;
		try {
			effectRow = flashSaleGoodsDao.saveSpecValue(skuGoodsSpecValue);
			if (effectRow != 1) {
				throw new DAOException("插入goods_spec_values表异常");
			}
		} catch (DataAccessException e) {
			log.error("插入goods_spec_values表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("插入goods_spec_values表异常");
			throw new DAOException(e);
		}
		
		try {
			effectRow = flashSaleGoodsDao.saveFlashSaleGoods(flashSaleGoods);
			if (effectRow != 1) {
				throw new DAOException("插入flashsale_goods表异常");
			}
			log.debug("根据flashSaleGoodsName:" + flashSaleGoods.getFlashSaleGoodsName() + "设置flashSaleGoods缓存");
			flashSaleGoodsCacheService.setFlashSaleGoodsByName(flashSaleGoods);
			log.debug("根据flashSaleGoodsId:" + flashSaleGoods.getFlashSaleGoodsId() + "设置flashSaleGoodsName：" + flashSaleGoods.getFlashSaleGoodsName() + "缓存" );
			flashSaleGoodsCacheService.setFlashSaleGoodsNameById(flashSaleGoods.getFlashSaleGoodsId(), flashSaleGoods.getFlashSaleGoodsName());
			log.debug("根据flashSaleGoodsId:" + flashSaleGoods.getFlashSaleGoodsId() + "设置skuGoodsSpecValue缓存");
			skuGoodsCacheService.setSKUGoodsSpecValueById(skuGoodsSpecValue);
		} catch (DataAccessException e) {
			log.error("插入flashsale_goods表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("插入flashsale_goods表异常");
			throw new DAOException(e);
		}
		return true;
	}

	@Override
	@Transactional(rollbackFor = DAOException.class)
	public boolean removeFlashSaleGoodsByName(String flashSaleGoodsName) {
		FlashSaleGoods flashSaleGoods = getFlashSaleGoodsByName(flashSaleGoodsName);
		
		int effectRow = 0;
		try {
			effectRow = flashSaleGoodsDao.removeSpecValueById(flashSaleGoods.getSpecValueId());
			if (effectRow != 1) {
				throw new DAOException("修改goods_spec_values表异常");
			}
			log.debug("根据flashSaleGoodsId:" + flashSaleGoods.getFlashSaleGoodsId() + "删除skuGoodsSpecValue缓存");
			skuGoodsCacheService.delSKUGoodsSpecValueById(flashSaleGoods.getSpecValueId());
		} catch (DataAccessException e) {
			log.error("修改goods_spec_values表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改goods_spec_values表异常");
			throw new DAOException(e);
		}
		try {
			effectRow = flashSaleGoodsDao.removeFlashSaleGoodsByName(flashSaleGoodsName);
			if (effectRow != 1) {
				throw new DAOException("修改flashsale_goods表异常");
			}
			log.debug("根据flashSaleGoodsName:" + flashSaleGoods.getFlashSaleGoodsName() + "删除flashSaleGoods缓存");
			flashSaleGoodsCacheService.delFlashSaleGoodsByName(flashSaleGoodsName);
			log.debug("根据flashSaleGoodsId:" +  + flashSaleGoods.getFlashSaleGoodsId() + "删除flashSaleGoodsName：" + flashSaleGoods.getFlashSaleGoodsName() + "缓存");
			flashSaleGoodsCacheService.delFlashSaleGoodsNameById(flashSaleGoods.getFlashSaleGoodsId());
		} catch (DataAccessException e) {
			log.error("修改flashsale_goods表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改flashsale_goods表异常");
			throw new DAOException(e);
		}
		return true;
	}

	@Override
	public boolean updateFlashSaleGoods(UpdateFlashSaleGoodsVo updateFlashSaleGoodsVo) {
		SKUGoodsSpecValue skuGoodsSpecValue = skuGoodsCacheService.getSKUGoodsSpecValueById(updateFlashSaleGoodsVo.getSpecValueId());
		
		int effectRow = 0;
		// 规格值有变化则修改规格值表
		if (!skuGoodsSpecValue.getSpecValueName().equals(updateFlashSaleGoodsVo.getSpecValueName())) {
			SKUGoodsSpecValue databaseskuGoodsSpecValue = new SKUGoodsSpecValue();
			databaseskuGoodsSpecValue.setSpecId(updateFlashSaleGoodsVo.getSpecId());
			databaseskuGoodsSpecValue.setSpecValueId(updateFlashSaleGoodsVo.getSpecValueId());
			databaseskuGoodsSpecValue.setSpecValueName(updateFlashSaleGoodsVo.getSpecValueName());
			databaseskuGoodsSpecValue.setUpdateTime(updateFlashSaleGoodsVo.getUpdateTime());
			log.debug("存入goods_spec_values表实体：" + databaseskuGoodsSpecValue.toString());
			
			InterProcessMutex lock = skuGoodsService.getSKUGoodsSpecValueLockById(databaseskuGoodsSpecValue.getSpecValueId());
			try {
				lock.acquire();
				
				effectRow = flashSaleGoodsDao.updateSpecValue(databaseskuGoodsSpecValue);
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
		
		FlashSaleGoods flashSaleGoods = getFlashSaleGoodsById(updateFlashSaleGoodsVo.getFlashSaleGoodsId());
		FlashSaleGoods databaseflashSaleGoods = new FlashSaleGoods();
		databaseflashSaleGoods.setSpuId(updateFlashSaleGoodsVo.getSpuId());
		databaseflashSaleGoods.setFlashSaleGoodsId(updateFlashSaleGoodsVo.getFlashSaleGoodsId());
		databaseflashSaleGoods.setFlashSaleGoodsName(updateFlashSaleGoodsVo.getFlashSaleGoodsName());
		databaseflashSaleGoods.setStock(updateFlashSaleGoodsVo.getStock());
		databaseflashSaleGoods.setPrice(updateFlashSaleGoodsVo.getPrice());
		databaseflashSaleGoods.setSpecValueId(updateFlashSaleGoodsVo.getSpecValueId());
		databaseflashSaleGoods.setUpdateTime(updateFlashSaleGoodsVo.getUpdateTime());
		log.debug("存入flashsale_goods表实体：" + databaseflashSaleGoods.toString());
		
		//将旧sku商品名称锁起来
		InterProcessMutex lock = null;
		if (!flashSaleGoods.getFlashSaleGoodsName().equals(updateFlashSaleGoodsVo.getFlashSaleGoodsName())) {
			lock = setFlashSaleGoodsLockByName(flashSaleGoods.getFlashSaleGoodsName());
		}
		try {
			if (lock != null) {
				lock.acquire();
			}
			
			effectRow = flashSaleGoodsDao.updateFlashSaleGoods(databaseflashSaleGoods);
			if (effectRow != 1) {
				throw new DAOException("修改flashsale_goods表异常");
			}
			log.debug("根据flashSaleGoodsId：" + databaseflashSaleGoods.getFlashSaleGoodsId() + "删除flashSaleGoods缓存");
			flashSaleGoodsCacheService.delFlashSaleGoodsByName(databaseflashSaleGoods.getFlashSaleGoodsName());
			log.debug("根据flashSaleGoodsId：" + databaseflashSaleGoods.getFlashSaleGoodsId() + "设置flashSaleGoodsName：" + databaseflashSaleGoods.getFlashSaleGoodsName() + "缓存：");
			flashSaleGoodsCacheService.setFlashSaleGoodsNameById(databaseflashSaleGoods.getFlashSaleGoodsId(), databaseflashSaleGoods.getFlashSaleGoodsName());
		} catch (DataAccessException e) {
			log.error("修改flashsale_goods表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改flashsale_goods表异常");
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
		return true;
	}

	@Override
	public boolean decrFlashSaleGoodsById(Long flashSaleGoodsId, Long delta) {
		InterProcessMutex lock = getFlashSaleGoodsLockById(flashSaleGoodsId);
		try {
			lock.acquire();
			
			Long nowStock = flashSaleGoodsCacheService.getFlashSaleGoodsStockById(flashSaleGoodsId);
			if (nowStock.longValue() < delta.longValue()) {
				log.debug("缓存nowStock：" + nowStock + "小于购买数量：" + delta + ",返回购买失败");
				return false;
			}
			
			// 设置新库存缓存
			flashSaleGoodsCacheService.setFlashSaleGoodsStockById(flashSaleGoodsId, nowStock - delta);
			return true;
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
	
	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public FlashSaleGoods getFlashSaleGoodsByName(String flashSaleGoodsName) {
		FlashSaleGoods flashSaleGoods = flashSaleGoodsCacheService.getFlashSaleGoodsByName(flashSaleGoodsName);
		if (flashSaleGoods == null) {
			InterProcessMutex lock = getFlashSaleGoodsLockByName(flashSaleGoodsName);
			try {
				if (lock.acquire(3L, TimeUnit.SECONDS)) {
					flashSaleGoods = flashSaleGoodsDao.getFlashSaleGoodsByName(flashSaleGoodsName, new Integer(0));
					if (flashSaleGoods != null) {
						log.debug("根据flashSaleGoodsName：" + flashSaleGoodsName + "查找出flashSaleGoods实体为：" + flashSaleGoods.toString());
						log.debug("根据flashSaleGoodsName：" + flashSaleGoodsName + "设置flashSaleGoods缓存");
						flashSaleGoodsCacheService.setFlashSaleGoodsByName(flashSaleGoods);
						return flashSaleGoods;
					}
					return flashSaleGoods;
				} else {
					flashSaleGoods = flashSaleGoodsCacheService.getFlashSaleGoodsByName(flashSaleGoodsName);
					if (flashSaleGoods == null) {
						log.debug("重试失败");
						throw new DAOException("flashSaleGoods查找重试失败");
					}
					return flashSaleGoods;
				}
			} catch (DataAccessException e) {
				log.error("查找flashsale_goods表异常");
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
		return flashSaleGoods;
	}
	
	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public FlashSaleGoods getFlashSaleGoodsById(Long flashSaleGoodsId) {
		String flashSaleGoodsName = flashSaleGoodsCacheService.getFlashSaleGoodsNameById(flashSaleGoodsId);
		if (flashSaleGoodsName == null) {
			InterProcessMutex lock = getFlashSaleGoodsLockById(flashSaleGoodsId);
			try {
				if (lock.acquire(3L, TimeUnit.SECONDS)) {
					FlashSaleGoods flashSaleGoods = flashSaleGoodsDao.getFlashSaleGoodsById(flashSaleGoodsId, new Integer(0));
					if (flashSaleGoods != null) {
						log.debug("根据flashSaleGoodsId：" + flashSaleGoodsId + "查找出flashSaleGoods实体为：" + flashSaleGoods.toString());
						log.debug("根据flashSaleGoodsId：" + flashSaleGoodsId + "设置flashSaleGoodsName：" + flashSaleGoods.getFlashSaleGoodsName() + "缓存");
						flashSaleGoodsCacheService.setFlashSaleGoodsNameById(flashSaleGoodsId, flashSaleGoodsName);
						return getFlashSaleGoodsByName(flashSaleGoods.getFlashSaleGoodsName());
					}
					return flashSaleGoods;
				} else {
					flashSaleGoodsName = flashSaleGoodsCacheService.getFlashSaleGoodsNameById(flashSaleGoodsId);
					if(flashSaleGoodsName == null) {
						log.debug("重试失败");
						throw new DAOException("flashSaleGoodsName查找重试失败");
					}
					return getFlashSaleGoodsByName(flashSaleGoodsName);
				}
			} catch (DataAccessException e) {
				log.error("查找flashsale_goods表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error("不明异常");
				throw new DAOException(e);
			}
		}
		return getFlashSaleGoodsByName(flashSaleGoodsName);
	}

	@Recover
	public int recover(DAOException e) {
		log.debug("flashSaleGoods查找重试3次失败！！！");
		throw new DAOException(e);
	}
	
	@Override
	public List<FlashSaleGoods> listFlashSaleGoodsByPromotionId(Long promotionId) {
		//TODO:活动模块还没做好
		return null;
	}
	
	@Override
	public InterProcessMutex setFlashSaleGoodsLockByName(String flashSaleGoodsName) {
		InterProcessMutex lock = new InterProcessMutex(client, "/flashsales/getFlashSaleGood/" + flashSaleGoodsName);
		if (LockUtils.getLock(lockScope, "/flashsales/getFlashSaleGood/" + flashSaleGoodsName) == null) {
			LockUtils.putLock(lockScope, "/flashsales/getFlashSaleGood/" + flashSaleGoodsName, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getFlashSaleGoodsLockByName(String flashSaleGoodsName) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/flashSales/getFlashSaleGood/" + flashSaleGoodsName);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/flashSales/getFlashSaleGood/" + flashSaleGoodsName);
			LockUtils.putLock(lockScope, "/flashSales/getFlashSaleGood/" + flashSaleGoodsName, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getFlashSaleGoodsLockById(Long flashSaleGoodsId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/flashSales/getFlashSaleGood/" + flashSaleGoodsId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/flashSales/getFlashSaleGood/" + flashSaleGoodsId);
			LockUtils.putLock(lockScope, "/flashSales/getFlashSaleGood/" + flashSaleGoodsId, lock);
		}
		return lock;
	}
}
