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
import com.shop.common.exception.ServiceException;
import com.shop.common.utils.LockUtils;
import com.shop.common.utils.SnowflakeIdUtil;
import com.shop.goods.dao.BrandDao;
import com.shop.goods.dao.CategoryDao;
import com.shop.goods.model.Category;
import com.shop.goods.service.CategoryCacheService;
import com.shop.goods.service.CategoryService;
import com.shop.goods.vo.SaveCategoryVo;
import com.shop.goods.vo.UpdateCategoryVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService{
	@Autowired
	CategoryDao categoryDao;

	@Autowired
	BrandDao brandDao;
	
	@Autowired
	CategoryCacheService categoryCacheService;

    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.categorys}")
    private String lockScope;
    
	SnowflakeIdUtil sf = new SnowflakeIdUtil(0,0);
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int saveCategory(SaveCategoryVo saveCategoryVo) {
		Category category = new Category();
		category.setCategoryId(sf.nextId());
		category.setCategoryName(saveCategoryVo.getCategoryName());
		category.setCreateTime(saveCategoryVo.getCreateTime());
		category.setDeleteStatus(new Integer(1));
		log.debug("存入goods_category实体为：" + category.toString());
		
		int effectRow = 0;
		// 插入goods_category表
		try {
			effectRow = categoryDao.saveCategory(category);
			if (effectRow != 1) {
				throw new DAOException("插入goods_category表异常");
			}
			log.debug("根据categoryName：" + category.getCategoryName() + "设置category缓存" + category.toString());
			categoryCacheService.setCategoryByName(category);
		} catch (DataAccessException e) {
			log.error("插入goods_category表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("插入goods_category表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public boolean removeCategoryByName(String categoryName) {
		Category category = getCategoryByName(categoryName);
		Long categoryId = category.getCategoryId();
		
		int effectRow = 0;
		// 批量删除goods_brand表
		try {
			brandDao.batchRemoveBrandByCategoryId(categoryId);
		} catch (DataAccessException e) {
			log.error("批量删除goods_brand表异常");
			// 这里如何捕捉到底是哪一行失败了呢？貌似只能看日志了
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("批量删除goods_brand表异常");
			throw new DAOException(e);
		}
		// 删除goods_category表
		try {
<<<<<<< HEAD
			effectRow = categoryDao.removeCategoryById(categoryId);
			if (effectRow != 1) {
				throw new DAOException("删除goods_category表异常");
			}
=======
			// 分布锁一锁上后，马上将缓存删除
>>>>>>> 2089505... 第二次提交
			log.debug("根据categoryName：" + categoryName + "删除category缓存");
			categoryCacheService.delCategoryByName(categoryName);
			log.debug("根据categoryId：" + categoryId + "删除categoryName：" + category.getCategoryName() + "缓存");
			categoryCacheService.delCategoryNameById(categoryId);
<<<<<<< HEAD
=======
			
			effectRow = categoryDao.removeCategoryById(categoryId);
			if (effectRow != 1) {
				throw new DAOException("删除goods_category表异常");
			}
>>>>>>> 2089505... 第二次提交
		} catch (DataAccessException e) {
			log.error("删除goods_category表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("删除goods_category表异常");
			throw new DAOException(e);
		}
		return true;
	}
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int updateCategory(UpdateCategoryVo updateCategoryVo) {
		Category category = getCategoryById(updateCategoryVo.getCategoryId());
		Category databasecategory = new Category();
		databasecategory.setCategoryId(updateCategoryVo.getCategoryId());
		databasecategory.setCategoryName(updateCategoryVo.getCategoryName());
		databasecategory.setUpdateTime(updateCategoryVo.getUpdateTime());
		log.debug("存入goods_category实体为：" + databasecategory.toString());
		
		InterProcessMutex lock = null;
		// category修改名称与旧名称不同则将旧名称锁住
		if (!category.getCategoryName().equals(updateCategoryVo.getCategoryName())) {
			lock = getCategoryLockByName(category.getCategoryName());
		}
		int effectRow = 0;
		// 修改goods_category表
		try {
			if (lock != null) {
				lock.acquire();
			}
<<<<<<< HEAD
			effectRow = categoryDao.updateCategory(databasecategory);
			if (effectRow != 1) {
				throw new DAOException("修改goods_category表异常");
			}
=======
			// 分布锁一锁上后，马上将缓存删除
>>>>>>> 2089505... 第二次提交
			log.debug("根据categoryName：" + category.getCategoryName() + "删除category缓存");
			categoryCacheService.delCategoryByName(category.getCategoryName());
			log.debug("根据categoryId：" + category.getCategoryId() + "删除categoryName：" + category.getCategoryName() + "缓存");
			categoryCacheService.delCategoryNameById(category.getCategoryId());
<<<<<<< HEAD
=======
			
			effectRow = categoryDao.updateCategory(databasecategory);
			if (effectRow != 1) {
				throw new DAOException("修改goods_category表异常");
			}
>>>>>>> 2089505... 第二次提交
		} catch (DataAccessException e) {
			log.error("修改goods_category表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改goods_category表异常");
			throw new DAOException(e);
		} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new ServiceException(e);
		} finally {
			try {
				if (lock != null && lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
				throw new ServiceException(e);
			}
		}
		return effectRow;
	}
	
	@Override
	public Category getCategoryByName(String categoryName) {
		Category category = categoryCacheService.getCategoryByName(categoryName);
		if (category == null) {
			InterProcessMutex lock = getCategoryLockByName(categoryName);
			try {
				if (lock.acquire(5L, TimeUnit.SECONDS)) {
					category = categoryDao.getCategoryByName(categoryName, new Integer(0));
					if (category != null) {
						if (category.getDeleteStatus().intValue() == 2) {
							log.debug("根据categoryName：" + categoryName + "查找出category已删除" + category.toString());
							return null;
						} else {
							log.debug("根据categoryName：" + categoryName + "查找出category实体为：" + category.toString());
							log.debug("根据categoryName：" + categoryName + "设置category缓存" + category.toString());
							categoryCacheService.setCategoryByName(category);
							return category;
						}
					}
					return null;
				} else {
					category = categoryCacheService.getCategoryByName(categoryName);
					if (category == null) {
						log.debug("重试");
						throw new DAOException("category查找重试失败");
					}
					return category;
				}
			} catch (DataAccessException e) {
				log.error("查找goods_category表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error(Thread.currentThread() + "不明异常");
				throw new ServiceException(e);
			} finally {
				try {
					if (lock.isOwnedByCurrentThread()) {
						lock.release();
					}
				} catch (Exception e) {
					log.error(Thread.currentThread() + "释放锁出现BUG");
					throw new ServiceException(e);
				}
			}
		}
		return category;
	}
	
	@Override
	public Category getCategoryById(Long categoryId) {
		String categoryName = categoryCacheService.getCategoryNameById(categoryId);
		if (categoryName == null) {
			InterProcessMutex lock = getCategoryLockById(categoryId);
			try {
				if (lock.acquire(5L, TimeUnit.SECONDS)) {
					Category category = categoryDao.getCategoryById(categoryId, new Integer(0));
					if (category != null) {
						if (category.getDeleteStatus().intValue() == 2) {
							log.debug("根据categoryId：" + categoryId + "查找出category已删除" + category.toString());
							return null;
						} else {
							log.debug("根据categoryId：" +  categoryId + "设置category名称：" + category.getCategoryName() + "缓存");
							log.debug("根据categoryId：" + categoryId + "设置categoryName：" + category.getCategoryName() + "缓存");
							categoryCacheService.setCategoryNameById(categoryId, category.getCategoryName());
							return getCategoryByName(category.getCategoryName());
						}
					}
					return null;
				} else {
					categoryName = categoryCacheService.getCategoryNameById(categoryId);
					if (categoryName == null) {
						log.debug("重试");
						throw new DAOException("categoryName查找重试失败");
					}
					return getCategoryByName(categoryName);
				}
			} catch (DataAccessException e) {
				log.error("查找goods_category表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error(Thread.currentThread() + "不明异常");
				throw new ServiceException(e);
			} finally {
				try {
					if (lock.isOwnedByCurrentThread()) {
						lock.release();
					}
				} catch (Exception e) {
					log.error(Thread.currentThread() + "释放锁出现BUG");
					throw new ServiceException(e);
				}
			}
		}
		return getCategoryByName(categoryName);
	}
	
	@Override
	public InterProcessMutex setCategoryLockById(Long categoryId) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/categorys/getCategory/" + categoryId);
		if (LockUtils.getLock(lockScope, "/categorys/getCategory/" + categoryId) == null) {
			LockUtils.putLock(lockScope, "/categorys/getCategory/" + categoryId, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getCategoryLockById(Long categoryId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/categorys/getCategory/" + categoryId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/categorys/getCategory/" + categoryId);
			LockUtils.putLock(lockScope, "/categorys/getCategory/" + categoryId, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex setCategoryLockByName(String categoryName) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/categorys/getCategory/" + categoryName);
		if (LockUtils.getLock(lockScope, "/categorys/getCategory/" + categoryName) == null) {
			LockUtils.putLock(lockScope, "/categorys/getCategory/" + categoryName, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getCategoryLockByName(String categoryName) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/categorys/getCategory/" + categoryName);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/categorys/getCategory/" + categoryName);
			LockUtils.putLock(lockScope, "/categorys/getCategory/" + categoryName, lock);
		}
		return lock;
	}
}
