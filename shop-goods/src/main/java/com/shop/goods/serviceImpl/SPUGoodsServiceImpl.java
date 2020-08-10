package com.shop.goods.serviceImpl;

import java.util.ArrayList;
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
import com.shop.goods.dao.SKUGoodsDao;
import com.shop.goods.dao.SPUGoodsDao;
import com.shop.goods.model.SPUGoodsSpec;
import com.shop.goods.model.SKUGoods;
import com.shop.goods.model.SPUGoods;
import com.shop.goods.service.SKUGoodsCacheService;
import com.shop.goods.service.SPUGoodsCacheService;
import com.shop.goods.service.SPUGoodsService;
import com.shop.goods.vo.SaveSPUGoodsVo;
import com.shop.goods.vo.UpdateSPUGoodsVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SPUGoodsServiceImpl implements SPUGoodsService{
	@Autowired
	SPUGoodsCacheService spuGoodsCacheService;
	
	@Autowired
	SKUGoodsCacheService skuGoodsCacheService;
	
	@Autowired
	SPUGoodsDao spuGoodsDao;
	
	@Autowired
	SKUGoodsDao skuGoodsDao;
	
    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.spuGoods}")
    private String lockScope;
    
	SnowflakeIdUtil sf = new SnowflakeIdUtil(0, 0);
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public boolean saveSPUGoods(SaveSPUGoodsVo saveSKUGoodsVo) {
		SPUGoods spuGoods = new SPUGoods();
		spuGoods.setSpuId(sf.nextId());
		spuGoods.setSpecId(sf.nextId());
		spuGoods.setCategoryId(saveSKUGoodsVo.getCategoryId());
		spuGoods.setBrandId(saveSKUGoodsVo.getBrandId());
		spuGoods.setPreId(saveSKUGoodsVo.getPreId());
		spuGoods.setLevel(saveSKUGoodsVo.getLevel());
		spuGoods.setType(saveSKUGoodsVo.getType());
		spuGoods.setSpuName(saveSKUGoodsVo.getSpuName());
		spuGoods.setCreateTime(saveSKUGoodsVo.getCreateTime());
		spuGoods.setDeleteStatus(0);
		spuGoods.setVerifyStatus(new Integer(0));
		log.debug("存入goods_spu表实体：" + spuGoods.toString());
		
		SPUGoodsSpec spuGoodsSpec = new SPUGoodsSpec();
		spuGoodsSpec.setCreateTime(saveSKUGoodsVo.getCreateTime());
		spuGoodsSpec.setSpecId(spuGoods.getSpecId());
		spuGoodsSpec.setSpecName(saveSKUGoodsVo.getSpecName());
		spuGoodsSpec.setDeleteStatus(0);
		log.debug("存入goods_spec表实体：" + spuGoodsSpec.toString());
		
		int effectRow = 0;
		// 插入goods_spec表
		try {
			effectRow = spuGoodsDao.saveSPUGoodsSpec(spuGoodsSpec);
			if (effectRow != 1) {
				throw new DAOException("插入goods_spec表异常");
			}
			log.debug("根据specId：" + spuGoodsSpec.getSpecId() + "设置SPUGoodsSpec缓存" + spuGoodsSpec.toString());
			spuGoodsCacheService.setSPUGoodsSpec(spuGoodsSpec);
		} catch (DataAccessException e) {
			log.error("插入goods_spec表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("插入goods_spec表异常");
			throw new DAOException(e);
		}
		// 插入goods_spu表
		try {
			effectRow = spuGoodsDao.saveSPUGoods(spuGoods);
			if (effectRow != 1) {
				throw new DAOException("插入goods_spu表异常");
			}
			log.debug("根据spuName：" + spuGoods.getSpuName() + "设置SPUGoods缓存" + spuGoods.toString());
			spuGoodsCacheService.setSPUGoodsByName(spuGoods);
			log.debug("根据spuId：" + spuGoods.getSpuId() + "设置spuName：" + spuGoods.getSpuName() + "缓存" );
			spuGoodsCacheService.setSPUGoodsNameById(spuGoods.getSpuId(), spuGoods.getSpuName());
		} catch (DataAccessException e) {
			log.error("插入goods_spu表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("插入goods_spu表异常");
			throw new DAOException(e);
		}
		return true;
	}

	@Override
	@Transactional(rollbackFor = DAOException.class)
	public boolean removeSPUGoodsByName(String spuName) {
		SPUGoods spuGoods = getSPUGoodsByName(spuName);
		List<SKUGoods> skuGoodsList;
		int effectRow = 0;
		//批量查找SKU商品
		try {
			skuGoodsList = skuGoodsDao.listSKUGoodsBySPUId(spuGoods.getSpuId(), new Integer(0));
		} catch (DataAccessException e) {
			log.error("批量查找goods_spu表异常");
			throw new DAOException(e);
		}
		//当该SPU商品下有SKU商品时
		if (skuGoodsList.size() != 0) {
			List<Long> tempList = new ArrayList<Long>(skuGoodsList.size());
			for (SKUGoods skuGoods : skuGoodsList) {
				tempList.add(skuGoods.getSkuId());
			}
			//批量删除SKU商品
			try {
				effectRow = skuGoodsDao.batchRemoveSKUGoodsById(tempList);
				if (effectRow != tempList.size()) {
					throw new DAOException("批量删除goods_spu表异常");
				}
				// 逐个删除SKU商品缓存
				for (SKUGoods skuGoods : skuGoodsList) {
					log.debug("根据skuName：" + skuGoods.getSkuName() + "删除SKUGoods缓存");
					skuGoodsCacheService.delSKUGoodsByName(skuGoods.getSkuName());
					log.debug("根据skuId：" + skuGoods.getSkuId() + "删除skuName：" + skuGoods.getSkuName() + "缓存");
					skuGoodsCacheService.delSKUGoodsNameById(skuGoods.getSkuId());
				}
			} catch (DataAccessException e) {
				log.error("批量删除goods_spu表异常");
				throw new DAOException(e);
			} catch (DAOException e) {
				log.error("批量删除goods_spu表异常");
				throw new DAOException(e);
			}
			
			// 查找规格值Id
			tempList.clear();
			for (SKUGoods skuGoods : skuGoodsList) {
				tempList.add(skuGoods.getSpecValueId());
			}
			//批量删除规格值表
			try {
				effectRow = skuGoodsDao.batchRemoveSKUGoodsSpecValueById(tempList);
				if (effectRow != tempList.size()) {
					throw new DAOException("批量删除goods_spu表异常");
				}
				
				for (SKUGoods skuGoods : skuGoodsList) {
					log.debug("根据specValueId：" + skuGoods.getSpecValueId() + "删除SKUGoodsSpecValue缓存");
					skuGoodsCacheService.delSKUGoodsSpecValueById(skuGoods.getSpecValueId());
				}
			} catch (DataAccessException e) {
				log.error("批量删除goods_spec_values表异常");
				throw new DAOException(e);
			} catch (DAOException e) {
				log.error("批量删除goods_spec_values表异常");
				throw new DAOException(e);
			}
		}
		// 删除规格表
		try {
<<<<<<< HEAD
=======
			// 分布锁一锁上后，马上将缓存删除
			log.debug("根据specId：" + spuGoods.getSpecId() + "删除SPUGoodsSpec缓存");
			spuGoodsCacheService.delSPUGoodsSpecById(spuGoods.getSpecId());
			
>>>>>>> 2089505... 第二次提交
			effectRow = spuGoodsDao.removeSPUGoodsSpecById(spuGoods.getSpecId());
			if (effectRow != 1) {
				throw new DAOException("删除goods_spec_values表异常");
			}
<<<<<<< HEAD
			log.debug("根据specId：" + spuGoods.getSpecId() + "删除SPUGoodsSpec缓存");
			spuGoodsCacheService.delSPUGoodsSpecById(spuGoods.getSpecId());
=======
>>>>>>> 2089505... 第二次提交
		} catch (DataAccessException e) {
			log.error("删除goods_spec_values表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("删除goods_spec_values表异常");
			throw new DAOException(e);
		}
		// 删除SPU商品
		try {
<<<<<<< HEAD
			effectRow = spuGoodsDao.removeSPUGoodsById(spuGoods.getSpuId());
			if (effectRow != 1) {
				throw new DAOException("删除goods_spu表异常");
			}
=======
			// 分布锁一锁上后，马上将缓存删除
>>>>>>> 2089505... 第二次提交
			log.debug("根据spuName：" + spuGoods.getSpuName() +"删除SPUGoods缓存");
			spuGoodsCacheService.delSPUGoodsByName(spuGoods.getSpuName());
			log.debug("根据spuId：" + spuGoods.getSpuId() + "删除spuName：" + spuGoods.getSpuName() + "缓存");
			spuGoodsCacheService.delSPUGoodsNameById(spuGoods.getSpuId());
<<<<<<< HEAD
=======
			
			effectRow = spuGoodsDao.removeSPUGoodsById(spuGoods.getSpuId());
			if (effectRow != 1) {
				throw new DAOException("删除goods_spu表异常");
			}
>>>>>>> 2089505... 第二次提交
		} catch (DataAccessException e) {
			log.error("删除goods_spu表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("删除goods_spu表异常");
			throw new DAOException(e);
		}
		return true;
	}
	
	@Override
	public boolean updateSPUGoods(UpdateSPUGoodsVo updateSPUGoodsVo) {
<<<<<<< HEAD
		SPUGoodsSpec spuGoodsSpec = getSPUGoodsSpecById(updateSPUGoodsVo.getSpecId());
		
		int effectRow = 0;
		// 旧规格名称与新规格名称不同则修改
		if (!spuGoodsSpec.getSpecName().equals(updateSPUGoodsVo.getSpecName())) {
			SPUGoodsSpec databasespuGoodsSpec = new SPUGoodsSpec();
			databasespuGoodsSpec.setSpecId(updateSPUGoodsVo.getSpecId());
			databasespuGoodsSpec.setSpecName(updateSPUGoodsVo.getSpecName());
			databasespuGoodsSpec.setUpdateTime(updateSPUGoodsVo.getUpdateTime());
			log.debug("存入goods_spec表实体为：" + databasespuGoodsSpec.toString());
			
			InterProcessMutex lock = getSPUGoodsSpecLockById(databasespuGoodsSpec.getSpecId());
			try {
				lock.acquire();
				effectRow = spuGoodsDao.updateSPUGoodsSpec(databasespuGoodsSpec);
				if (effectRow != 1) {
					throw new DAOException("修改goods_spec表异常");
				}
				log.debug("根据specId：" + databasespuGoodsSpec.getSpecId() + "删除SPUGoodsSpec缓存");
				spuGoodsCacheService.delSPUGoodsSpecById(databasespuGoodsSpec.getSpecId());
			} catch (DataAccessException e) {
				log.error("修改goods_spec表异常");
				throw new DAOException(e);
			} catch (DAOException e) {
				log.error("修改goods_spec表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error("不明异常");
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
		
		SPUGoods spuGoods = getSPUGoodsById(updateSPUGoodsVo.getSpuId());
		// 修改SPU商品
=======
		SPUGoods spuGoods = getSPUGoodsById(updateSPUGoodsVo.getSpuId());
>>>>>>> 2089505... 第二次提交
		SPUGoods databasespuGoods = new SPUGoods();
		databasespuGoods.setCategoryId(updateSPUGoodsVo.getCategoryId());
		databasespuGoods.setBrandId(updateSPUGoodsVo.getBrandId());
		databasespuGoods.setPreId(updateSPUGoodsVo.getPreId());
		databasespuGoods.setLevel(updateSPUGoodsVo.getLevel());
		databasespuGoods.setType(updateSPUGoodsVo.getType());
		databasespuGoods.setSpuId(updateSPUGoodsVo.getSpuId());
		databasespuGoods.setSpuName(updateSPUGoodsVo.getSpuName());
		databasespuGoods.setSpecId(updateSPUGoodsVo.getSpecId());
		databasespuGoods.setUpdateTime(updateSPUGoodsVo.getUpdateTime());
		log.debug("存入goods_spu表实体为：" + databasespuGoods.toString());
		
		// 旧名称与新名称不同则将旧名称上锁
		InterProcessMutex lock = null;
		if (!spuGoods.getSpuName().equals(updateSPUGoodsVo.getSpuName())) {
			lock = getSPUGoodsLockByName(spuGoods.getSpuName());
		}
<<<<<<< HEAD
=======
		
		int effectRow = 0;
>>>>>>> 2089505... 第二次提交
		try {
			if (lock != null) {
				lock.acquire();
			}
<<<<<<< HEAD
			effectRow = spuGoodsDao.updateSPUGoods(databasespuGoods);
			if (effectRow != 1) {
				throw new DAOException("修改goods_spu表异常");
			}
=======
			// 分布锁一锁上后，马上将缓存删除
>>>>>>> 2089505... 第二次提交
			log.debug("根据spuName：" + spuGoods.getSpuName() + "删除SPUGoods缓存");
			spuGoodsCacheService.delSPUGoodsByName(spuGoods.getSpuName());
			log.debug("根据spuId：" + spuGoods.getSpuId() + "删除spuName：" + spuGoods.getSpuName() + "缓存");
			spuGoodsCacheService.delSPUGoodsNameById(spuGoods.getSpuId());
<<<<<<< HEAD
=======
			
			effectRow = spuGoodsDao.updateSPUGoods(databasespuGoods);
			if (effectRow != 1) {
				throw new DAOException("修改goods_spu表异常");
			}
>>>>>>> 2089505... 第二次提交
		} catch (DataAccessException e) {
			log.error("修改goods_spu表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改goods_spu表异常");
			throw new DAOException(e);
		} catch (Exception e) {
			log.error("不明异常");
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
		
		SPUGoodsSpec spuGoodsSpec = getSPUGoodsSpecById(updateSPUGoodsVo.getSpecId());
		// 旧规格名称与新规格名称不同则修改
		if (!spuGoodsSpec.getSpecName().equals(updateSPUGoodsVo.getSpecName())) {
			SPUGoodsSpec databasespuGoodsSpec = new SPUGoodsSpec();
			databasespuGoodsSpec.setSpecId(updateSPUGoodsVo.getSpecId());
			databasespuGoodsSpec.setSpecName(updateSPUGoodsVo.getSpecName());
			databasespuGoodsSpec.setUpdateTime(updateSPUGoodsVo.getUpdateTime());
			log.debug("存入goods_spec表实体为：" + databasespuGoodsSpec.toString());
			
			lock = getSPUGoodsSpecLockById(databasespuGoodsSpec.getSpecId());
			try {
				lock.acquire();
				// 分布锁一锁上后，马上将缓存删除
				log.debug("根据specId：" + databasespuGoodsSpec.getSpecId() + "删除SPUGoodsSpec缓存");
				spuGoodsCacheService.delSPUGoodsSpecById(databasespuGoodsSpec.getSpecId());
				
				effectRow = spuGoodsDao.updateSPUGoodsSpec(databasespuGoodsSpec);
				if (effectRow != 1) {
					throw new DAOException("修改goods_spec表异常");
				}
			} catch (DataAccessException e) {
				log.error("修改goods_spec表异常");
				throw new DAOException(e);
			} catch (DAOException e) {
				log.error("修改goods_spec表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error("不明异常");
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
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public SPUGoods getSPUGoodsByName(String spuName) {
		SPUGoods spuGoods = spuGoodsCacheService.getSPUGoodsByName(spuName);
		if (spuGoods == null) {
			InterProcessMutex lock = getSPUGoodsLockByName(spuName);
			try {
				if (lock.acquire(5L, TimeUnit.SECONDS)) {
					spuGoods = spuGoodsDao.getSPUGoodsByName(spuName, new Integer(0));
					if (spuGoods != null) {
						if (spuGoods.getDeleteStatus().intValue() == 2) {
							log.debug("根据spuName：" + spuName + "查找出SPUGoods实体已删除" + spuGoods.toString());
							return null;
						} else {
							log.debug("根据spuName：" + spuName + "查找出SPUGoods实体为：" + spuGoods.toString());
							log.debug("根据spuName：" + spuName + "设置SPUGoods缓存" + spuGoods.toString());
							spuGoodsCacheService.setSPUGoodsByName(spuGoods);
							return spuGoods;
						}
					}
					return null;
				} else {
					spuGoods = spuGoodsCacheService.getSPUGoodsByName(spuName);
					if (spuGoods == null) {
						log.debug("重试");
						throw new DAOException("spuGoods查找重试失败");
					}
					return spuGoods;
				}
			} catch (DataAccessException e) {
				log.error("查找goods_spu表异常");
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
		return spuGoods;
	}
	
	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public SPUGoods getSPUGoodsById(Long spuId) {
		String spuName = spuGoodsCacheService.getSPUGoodsNameById(spuId);
		if (spuName == null) {
			InterProcessMutex lock = getSPUGoodsLockById(spuId);
			try {
				if (lock.acquire(5L, TimeUnit.SECONDS)) {
					SPUGoods spuGoods = spuGoodsDao.getSPUGoodsById(spuId, new Integer(0));
					if (spuGoods != null) {
						if (spuGoods.getDeleteStatus().intValue() == 2) {
							log.debug("根据spuId：" + spuId + "查找出SPUGoods实体已删除" + spuGoods.toString());
							return null;
						} else {
							log.debug("根据spuId：" + spuId + "查找出SPUGoods实体为：" + spuGoods.toString());
							log.debug("根据spuId：" + spuId + "设置spuName：" + spuGoods.getSpuName() + "缓存");
							spuGoodsCacheService.setSPUGoodsNameById(spuGoods.getSpuId(), spuGoods.getSpuName());
							return getSPUGoodsByName(spuGoods.getSpuName());
						}
					}
					return null;
				} else {
					spuName = spuGoodsCacheService.getSPUGoodsNameById(spuId);
					if (spuName == null) {
						log.debug("重试");
						throw new DAOException("spuName查找重试失败");
					}
					return getSPUGoodsByName(spuName);
				}
			} catch (DataAccessException e) {
				log.error("查找goods_spu表异常");
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
		return getSPUGoodsByName(spuName);
	}
	
	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public SPUGoods getSPUGoodsBySpecId(Long specId) {
		String spuName = spuGoodsCacheService.getSPUGoodsNameBySpecId(specId);
		if (spuName == null) {
			InterProcessMutex lock = getSPUGoodsLockBySpecId(specId);
			try {
				if (lock.acquire(5L, TimeUnit.SECONDS)) {
					SPUGoods spuGoods = spuGoodsDao.getSPUGoodsBySpecId(specId, new Integer(0));
					if (spuGoods != null) {
						if (spuGoods.getDeleteStatus().intValue() == 2) {
							log.debug("根据specId：" + specId + "查找出SPUGoods实体已删除" + spuGoods.toString());
							return null;
						}
						log.debug("根据specId：" + specId + "查找出SPUGoods实体为：" + spuGoods.toString());
						log.debug("根据specId：" + specId + "设置spuName：" + spuGoods.getSpuName() + "缓存");
						spuGoodsCacheService.setSPUGoodsNameBySpecId(spuGoods.getSpecId(), spuGoods.getSpuName());
						return getSPUGoodsByName(spuGoods.getSpuName());
					}
					return null;
				} else {
					spuName = spuGoodsCacheService.getSPUGoodsNameBySpecId(specId);
					if (spuName == null) {
						log.debug("重试");
						throw new DAOException("spuName查找重试失败");
					}
					return getSPUGoodsByName(spuName);
				}
			} catch (DataAccessException e) {
				log.error("查找goods_spu表异常");
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
		return getSPUGoodsByName(spuName);
	}
	
	@Recover
	public int recover(DAOException e) {
		log.error("SPU查找重试3次失败！！！");
		throw new DAOException(e);
	}
	
	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public SPUGoodsSpec getSPUGoodsSpecById(Long specId) {
		SPUGoodsSpec spuGoodsSpec = spuGoodsCacheService.getSPUGoodsSpecById(specId);
		if (spuGoodsSpec == null) {
			InterProcessMutex lock = getSPUGoodsSpecLockById(specId);
			try {
				if (lock.acquire(5L, TimeUnit.SECONDS)) {
					spuGoodsSpec = spuGoodsDao.getSPUGoodsSpecById(specId, new Integer(0));
					if (spuGoodsSpec != null) {
						if (spuGoodsSpec.getDeleteStatus().intValue() == 2) {
							log.debug("根据specId：" + specId + "查找出SPUGoodsSpec实体已删除" + spuGoodsSpec.toString());
							return null;
						}
						log.debug("根据specId：" + specId + "查找出SPUGoodsSpec实体为：" + spuGoodsSpec.toString());
						log.debug("根据specId：" + specId + "设置SPUGoodsSpec缓存" + spuGoodsSpec.toString());
						spuGoodsCacheService.setSPUGoodsSpec(spuGoodsSpec);
						return spuGoodsSpec;
					}
					return null;
				} else {
					spuGoodsSpec = spuGoodsCacheService.getSPUGoodsSpecById(specId);
					if (spuGoodsSpec == null) {
						log.debug("重试");
						throw new DAOException("spuGoodsSpec查找重试失败");
					}
					return spuGoodsSpec;
				}
			} catch (DataAccessException e) {
				log.error("查找goods_spec表异常");
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
		return spuGoodsSpec;
	}
	
	@Override
	public InterProcessMutex setSPUGoodsLockByName(String spuName) {
		InterProcessMutex lock = new InterProcessMutex(client, "/spuGoods/getSPUGood/" + spuName);
		if (LockUtils.getLock(lockScope, "/spuGoods/getSPUGood/" + spuName) == null) {
			LockUtils.putLock(lockScope, "/spuGoods/getSPUGood/" + spuName, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getSPUGoodsLockByName(String spuName) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/spuGoods/getSPUGood/" + spuName);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/spuGoods/getSPUGood/" + spuName);
			LockUtils.putLock(lockScope, "/spuGoods/getSPUGood/" + spuName, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getSPUGoodsLockById(Long spuId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/spuGoods/getSPUGood/" + spuId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/spuGoods/getSPUGood/" + spuId);
			LockUtils.putLock(lockScope, "/spuGoods/getSPUGood/" + spuId, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getSPUGoodsLockBySpecId(Long specId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/spuGoods/getSPUGood/" + specId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/spuGoods/getSPUGood/" + specId);
			LockUtils.putLock(lockScope, "/spuGoods/getSPUGood/" + specId, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getSPUGoodsSpecLockById(Long specId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/spuGoods/getSPUGoodSpec/" + specId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/spuGoods/getSPUGoodSpec/" + specId);
			LockUtils.putLock(lockScope, "/spuGoods/getSPUGoodSpec/" + specId, lock);
		}
		return lock;
	}
}
