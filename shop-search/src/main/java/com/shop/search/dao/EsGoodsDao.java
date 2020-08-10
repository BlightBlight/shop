package com.shop.search.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.shop.search.model.EsGoods;

import java.util.List;

/**
 * 搜索系统中的商品管理自定义Dao
 */
@Mapper
public interface EsGoodsDao {
	/**
	 * 查找所有EsGoods
	 */
	@Select("select * from goods_sku")
	@Results({
        @Result(property = "esGoodsId", column = "sku_id")
})
    List<EsGoods> listESGoods();
}
