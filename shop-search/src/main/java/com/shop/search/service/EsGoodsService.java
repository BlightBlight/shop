package com.shop.search.service;

import org.springframework.data.domain.Page;

import com.shop.search.model.EsGoods;
import com.shop.search.model.EsGoodsRelatedInfo;

import java.util.List;

/**
 * 商品搜索管理Service
 */
public interface EsGoodsService {
	/**
	 * 根据Id创建商品
	 */
    EsGoods saveEsGoods(Long EsGoodsId);
	
	/**
	 * 从数据库中导入所有商品到ES
	 */
    int batchSaveEsGoods();

	/**
	 * 根据Id删除商品
	 */
    void removeEsGoodsById(Long EsGoodsId);

	/**
	 * 根据Id批量删除商品
	 */
    void batchRemoveEsGoodsById(List<Long> EsGoodsIds);

	/**
	 * 根据关键字搜索名称或者副标题
	 */
    Page<EsGoods> query(String keyword, Integer pageNum, Integer pageSize);

	/**
	 * 根据关键字搜索名称或者副标题复合查询
	 */
    Page<EsGoods> query(String keyword, Long brandId, Long categoryId, Integer pageNum, Integer pageSize, Integer sort);

	/**
	 * 根据商品id推荐相关商品
	 */
    Page<EsGoods> recommend(Long ESGoodsId, Integer pageNum, Integer pageSize);

	/**
	 * 获取搜索词相关品牌、分类、属性
	 */
    EsGoodsRelatedInfo searchRelatedInfo(String keyword);
}
