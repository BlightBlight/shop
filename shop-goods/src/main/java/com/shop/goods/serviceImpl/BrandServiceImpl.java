package com.shop.goods.serviceImpl;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.common.exception.DAOException;
import com.shop.common.utils.LockUtils;
import com.shop.common.utils.SnowflakeIdUtil;
import com.shop.goods.dao.BrandDao;
import com.shop.goods.model.Brand;
import com.shop.goods.service.BrandCacheService;
import com.shop.goods.service.BrandService;
import com.shop.goods.vo.SaveBrandVo;
import com.shop.goods.vo.UpdateBrandVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BrandServiceImpl implements BrandService{
	@Autowired
	BrandDao brandDao;
	
	@Autowired
	BrandCacheService brandCacheService;

	@Autowired
	CuratorFramework client;
	
    @Value("${zookeeper.lockScope.brands}")
    private String lockScope;
	
	SnowflakeIdUtil sf = new SnowflakeIdUtil(0,0);
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int saveBrand(SaveBrandVo saveBrandVo) {
		Brand brand = new Brand();
		brand.setCategoryId(saveBrandVo.getCategoryId());
		brand.setBrandId(sf.nextId());
		brand.setBrandName(saveBrandVo.getBrandName());
		brand.setCreateTime(saveBrandVo.getCreateTime());
		brand.setDeleteStatus(new Integer(1));
		log.debug("存入goods_brand实体：" + brand.toString());
		
		int effectRow = 0;
		// 插入goods_brand表
		try {
			effectRow = brandDao.saveBrand(brand);
			if (effectRow != 1) {
				throw new DAOException("插入goods_brand表异常");
			}
			log.debug("根据brandName：" + brand.getBrandName() + "设置brand缓存" + brand.toString());
			brandCacheService.setBrandByName(brand);
		} catch (DataAccessException e) {
			log.error("插入goods_brand表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("插入goods_brand表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}

	@Override
	@Transactional(rollbackFor = DAOException.class)
	public boolean removeBrandByName(String brandName) {
		Brand brand = getBrandByName(brandName);
<<<<<<< HEAD
		// 删除goods_brand表
		try {
			int effectRow = brandDao.removeBrandByName(brandName);
			if (effectRow != 1) {
				throw new DAOException("删除goods_brand表异常");
			}
=======
		
		// 删除goods_brand表
		try {
			// 分布锁一锁上后，马上将缓存删除
>>>>>>> 2089505... 第二次提交
			log.debug("根据brandName：" + brandName + "删除brand缓存");
			brandCacheService.delBrandByName(brandName);
			log.debug("根据brandId：" + brand.getBrandId() + "删除brandName：" + brandName + "缓存");
			brandCacheService.delBrandNameById(brand.getBrandId());
<<<<<<< HEAD
=======
			
			int effectRow = brandDao.removeBrandByName(brandName);
			if (effectRow != 1) {
				throw new DAOException("删除goods_brand表异常");
			}
>>>>>>> 2089505... 第二次提交
		} catch (DataAccessException e) {
			log.error("删除goods_brand表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("删除goods_brand表异常");
			throw new DAOException(e);
		}
		return true;
	}
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int updateBrand(UpdateBrandVo updateBrandVo) {
		Brand brand = getBrandById(updateBrandVo.getBrandId());
<<<<<<< HEAD
		
		InterProcessMutex lock = null;
		// brand修改名称与旧名称不同则将旧名称锁住
		if (!brand.getBrandName().equals(updateBrandVo.getBrandName())) {
			lock = getBrandLockByName(brand.getBrandName());
		}
		
=======
>>>>>>> 2089505... 第二次提交
		Brand databasebrand = new Brand();
		databasebrand.setBrandId(updateBrandVo.getBrandId());
		databasebrand.setCategoryId(updateBrandVo.getCategoryId());
		databasebrand.setBrandName(updateBrandVo.getBrandName());
		databasebrand.setUpdateTime(updateBrandVo.getUpdateTime());
		log.debug("存入goods_brand实体：" + databasebrand.toString());
		
<<<<<<< HEAD
=======
		InterProcessMutex lock = null;
		// brand修改名称与旧名称不同则将旧名称锁住
		if (!brand.getBrandName().equals(updateBrandVo.getBrandName())) {
			lock = getBrandLockByName(brand.getBrandName());
		}
		
>>>>>>> 2089505... 第二次提交
		// 修改goods_brand表
		int effectRow = 0;
		try {
			if (lock != null) {
				lock.acquire();
			}
<<<<<<< HEAD
			effectRow = brandDao.updateBrand(databasebrand);
			if (effectRow != 1) {
				throw new DAOException("修改goods_brand表异常");
			}
=======
			// 分布锁一锁上后，马上将缓存删除
>>>>>>> 2089505... 第二次提交
			log.debug("根据brandName：" + brand.getBrandName() + "删除brand缓存");
			brandCacheService.delBrandByName(brand.getBrandName());
			log.debug("根据brandId：" + brand.getBrandId() + "删除brandName：" + brand.getBrandName() + "缓存");
			brandCacheService.delBrandNameById(brand.getBrandId());
<<<<<<< HEAD
=======
			
			effectRow = brandDao.updateBrand(databasebrand);
			if (effectRow != 1) {
				throw new DAOException("修改goods_brand表异常");
			}
>>>>>>> 2089505... 第二次提交
		} catch (DataAccessException e) {
			log.error("修改goods_brand表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改goods_brand表异常");
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
		return effectRow;
	}
	
	@Override
	public Brand getBrandByName(String brandName) {
		Brand brand = brandCacheService.getBrandByName(brandName);
		if (brand == null) {
			InterProcessMutex lock = getBrandLockByName(brandName);
			try {
				if (lock.acquire(5L, TimeUnit.SECONDS)) {
					brand = brandDao.getBrandByName(brandName, new Integer(0));
					if (brand != null) {
						if (brand.getDeleteStatus().intValue() == 2) {
							log.debug("根据brandName：" + brandName + "查找出brand已删除" + brand.toString());
							return null;
						} else {
							log.debug("根据brandName：" + brandName + "查找出brand实体为：" + brand.toString());
							log.debug("根据brandName：" + brandName + "设置brand缓存" + brand.toString());
							brandCacheService.setBrandByName(brand);
							return brand;
						}
					}
					return null;
				} else {
					brand = brandCacheService.getBrandByName(brandName);
					if (brand == null) {
						log.debug("重试");
						throw new DAOException("brand查找重试失败");
					}
					return brand;
				}
			} catch (DataAccessException e) {
				log.error("查找goods_brand表异常");
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
		return brand;
	}
	
	@Override
	public Brand getBrandById(Long brandId) {
		String brandName = brandCacheService.getBrandNameById(brandId);
		if (brandName == null) {
			InterProcessMutex lock = getBrandLockById(brandId);
			try {
				if (lock.acquire(5L, TimeUnit.SECONDS)) {
					Brand brand = brandDao.getBrandById(brandId, new Integer(0));
					if (brand != null) {
						if (brand.getDeleteStatus().intValue() == 2) {
							log.debug("根据brandName：" + brandName + "查找出brand已删除" + brand.toString());
							return null;
						} else {
							log.debug("根据brandId：" + brandId + "查找出brand实体为：" + brand.toString());
							log.debug("根据brandId：" + brandId + "设置brandName：" + brand.getBrandName() + "缓存");
							brandCacheService.setBrandNameById(brand.getBrandId(), brand.getBrandName());
							return getBrandByName(brand.getBrandName());
						}
					}
					return null;
				} else {
					brandName = brandCacheService.getBrandNameById(brandId);
					if (brandName == null) {
						log.debug("重试");
						throw new DAOException("brandName查找重试失败");
					}
					return getBrandByName(brandName);
				}
			} catch (DataAccessException e) {
				log.error("查找goods_brand表异常");
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
		return getBrandByName(brandName);
	}
	
	@Override
	public InterProcessMutex getBrandLockByName(String brandName) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/brands/getBrand/" + brandName);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/brands/getBrand/" + brandName);
			LockUtils.putLock(lockScope, "/brands/getBrand/" + brandName, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getBrandLockById(Long brandId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/brands/getBrand/" + brandId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/brands/getBrand/" + brandId);
			LockUtils.putLock(lockScope, "/brands/getBrand/" + brandId, lock);
		}
		return lock;
	}
	
}
