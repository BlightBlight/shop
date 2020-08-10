package com.shop.goods.serviceImpl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.common.exception.DAOException;
import com.shop.common.exception.ServiceException;
import com.shop.common.result.ResultCode;
import com.shop.common.utils.SnowflakeIdUtil;
import com.shop.goods.dao.SKUGoodsDao;
import com.shop.goods.dao.SPUGoodsDao;
import com.shop.goods.model.SKUGoodsSpecValue;
import com.shop.goods.model.SKUGoods;
import com.shop.goods.service.GoodsService;
import com.shop.goods.service.SKUGoodsService;
import com.shop.goods.service.SPUGoodsService;
import com.shop.goods.vo.SaveGoodsVo;
import com.shop.goods.vo.SaveSKUGoodsVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GoodsServiceImpl implements GoodsService {
	@Autowired
	SPUGoodsService spuGoodsService;

	@Autowired
	SKUGoodsService skuGoodsService;
	
	@Autowired
	SPUGoodsDao spuGoodsDao;

	@Autowired
	SKUGoodsDao skuGoodsDao;
	
	// 这里可以改进，对Snowflake的部署应该要考虑到分布式，不过鉴于目前只有一台机器。。。
	SnowflakeIdUtil sf = new SnowflakeIdUtil(0, 0);

	/**
	 * 新增商品
	 */

	/*
	 * 创建商品，前端输入商品信息后，将商品信息拆分为4个部分 SPU商品、SKU商品、规格、规格值 SPU指正常概念里的商品，如：IPhone4、维达纸巾等
	 * SKU指具体规格的商品，如：IPhone4内存8G、红色；维达纸巾200抽、16袋装等 规格指商品可选分类，如：手机可选内存、颜色、型号等
	 * 规格值指规格具体值：如：内存4G、8G，颜色红色、绿色等 规格和规格值实际可并入SPU、SKU概念内，由于规格和规格值都是大字段属性
	 * 且并不属于热点数据，用户点进具体产品才加载，所以将它与SPU、SKU进行拆分，单独存放
	 * 这里可以优化的点：如果商家为热门商家，则新增商品为热点key，需要进行缓存预热，防止缓存击穿问题
	 * 如果商品属性进一步增多，要进行操作封装，将操作分多几个函数进行，免得难看。
	 */
	@Override
	@Transactional(rollbackFor = DAOException.class)
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public boolean saveGoods(SaveGoodsVo saveGoodsVo) {
		List<SKUGoods> skuGoodsList = new LinkedList<SKUGoods>();
		List<SKUGoodsSpecValue> goodsSpecValueList = new LinkedList<SKUGoodsSpecValue>();
		
		for (SaveSKUGoodsVo skuGoodsVo : saveGoodsVo.getSkuList()) {
			SKUGoods skuGoods = new SKUGoods();
			skuGoods.setSkuId(sf.nextId());
			skuGoods.setSpuId(skuGoodsVo.getSpuId());
			skuGoods.setSpecValueId(sf.nextId());
			skuGoods.setShopId(skuGoodsVo.getShopId());
			skuGoods.setSkuName(skuGoodsVo.getSkuName());
			skuGoods.setPrice(skuGoodsVo.getPrice());
			skuGoods.setStock(skuGoodsVo.getStock());
			skuGoods.setCreateTime(skuGoodsVo.getCreateTime());
			log.debug("存入goods_sku表实体：" + skuGoods.toString());
			skuGoodsList.add(skuGoods);
				
			SKUGoodsSpecValue skuGoodsSpecValue = new SKUGoodsSpecValue();
			skuGoodsSpecValue.setSpecValueId(skuGoods.getSpecValueId());
			skuGoodsSpecValue.setSpecId(skuGoodsVo.getSpecId());
			skuGoodsSpecValue.setSpecValueName(skuGoodsVo.getSpecValueName());
			skuGoodsSpecValue.setCreateTime(skuGoodsVo.getCreateTime());
			log.debug("存入goods_spec_values表实体：" + skuGoodsSpecValue.toString());
			goodsSpecValueList.add(skuGoodsSpecValue);
		}
		int effectRow = 0;
		// 批量插入goods_spec_values表
		try {
			effectRow = skuGoodsDao.batchSaveSKUGoodsSpecValue(goodsSpecValueList);
			if (effectRow != skuGoodsList.size()) {
				throw new DAOException("批量插入goods_spec_values表异常");
			}
		} catch (DataAccessException e) {
			log.error("批量插入goods_spec_values表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("批量插入goods_spec_values表异常");
			throw new DAOException(e);
		}
		
		try {
			effectRow = skuGoodsDao.batchSaveSKUGoods(skuGoodsList);
			if (effectRow != skuGoodsList.size()) {
				throw new DAOException("批量插入goods_sku表异常");
			}
		} catch (DataAccessException e) {
			log.error("批量插入goods_sku表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("批量插入goods_sku表异常");
			throw new DAOException(e);
		}
		return true;
	}

	@Recover
	public int recover(DAOException e) {
		log.error("新增SKU商品失败！！！" + e);
		throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR);
	}
}
