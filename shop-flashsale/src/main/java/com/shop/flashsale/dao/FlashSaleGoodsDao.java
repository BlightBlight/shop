package com.shop.flashsale.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shop.flashsale.model.FlashSaleGoods;
import com.shop.goods.model.SKUGoodsSpecValue;

@Mapper
public interface FlashSaleGoodsDao {

	/**
	 * 新增秒杀商品基本信息
	 */
	@Insert("insert into flashsale_goods(shop_id, spu_id, flashsale_goods_id, flashsale_goods_name, stock, price, "
			+ "spec_value_id, create_time, verify_status, flashsale_goods_status, delete_status) "
			+ "values(#{shopId}, #{spuId}, #{flashSaleGoodsId}, "
			+ "#{flashSaleGoodsName}, #{stock}, #{price}, #{specValueId}, #{createTime}, "
			+ "#{verifyStatus}, #{flashSaleGoodsStatus}, #{deleteStatus}) ")
	public int saveFlashSaleGoods(FlashSaleGoods flashSaleGoods);
	
	/**
	 * 新增规格值表信息
	 */
	@Insert("insert into goods_spec_values(spec_value_id, spec_id, spec_value_name, create_time, delete_status) "
			+ "values(#{specValueId}, #{specId}, #{specValueName}, #{createTime}, #{deleteStatus}) ")
	public int saveSpecValue(SKUGoodsSpecValue goodsSpecValue);
	
	/**
	 * 根据Id删除秒杀商品
	 */
	@Update("update flashsale_goods set "
			+ "delete_status = 1 "
			+ "where flashsale_goods_name = #{flashSaleGoodsName} ")
	public int removeFlashSaleGoodsByName(@Param("flashSaleGoodsName") String flashSaleGoodsName);
	
	/**
	 * 根据Id删除规格值表
	 */
	@Update("update goods_spec_values set "
			+ "delete_status = 1 "
			+ "where spec_value_id = #{specValueId} and delete_status != 1 ")
	public int removeSpecValueById(@Param("specValueId") Long specValueId);
	
	/**
	 * 修改秒杀商品信息
	 */
	@Update({"<script>"
		    + "update flashsale_goods set "
			+ "<trim suffixOverrides=\",\">"
			+ "<if test = \"spuId != null and spuId != '' \">"
			+ "spu_id = #{spuId}, "
			+ "</if>"
			+ "<if test = \"flashSaleGoodsName != null and flashSaleGoodsName != '' \">"
			+ "flashsale_goods_name = #{flashSaleGoodsName}, "
			+ "</if>"
			+ "<if test = \"stock != null and stock != '' \">"
			+ "stock = #{stock}, "
			+ "</if>"
			+ "<if test = \"price != null and price != '' \">"
			+ "price = #{price}, "
			+ "</if>"
			+ "<if test = \"specValueId != null and specValueId != '' \">"
			+ "spec_value_id = #{specValueId}, "
			+ "</if>"
			+ "<if test = \"updateTime != null \">"
			+ "update_time = #{updateTime}, "
			+ "</if>"
			+ "</trim>"
			+ "where flashsale_goods_id = #{flashSaleGoodsId} "
			+ "</script>"})
	public int updateFlashSaleGoods(FlashSaleGoods flashSaleGoods);
	
	/**
	 * 修改规格值信息
	 */
	@Update({"<script>"
		    + "update goods_spec_values set "
			+ "<trim suffixOverrides=\",\">"
			+ "<if test = \"specId != null and specId != '' \">"
			+ "spec_id = #{specId}, "
			+ "</if>"
			+ "<if test = \"specValueName != null and specValueName != '' \">"
			+ "spec_value_name = #{specValueName}, "
			+ "</if>"
			+ "<if test = \"updateTime != null \">"
			+ "update_time = #{updateTime}, "
			+ "</if>"
			+ "</trim>"
			+ "where spec_value_id = #{specValueId} "
			+ "</script>"})
	public int updateSpecValue(SKUGoodsSpecValue goodsSpecValue);
	
	/**
	 * 根据Name查找秒杀商品
	 */
	@Select({ "<script>"
			+ "select * from flashsale_goods "
			+ "where flashsale_goods_name = #{flashSaleGoodsName} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 2\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public FlashSaleGoods getFlashSaleGoodsByName(@Param("flashSaleGoodsName") String flashSaleGoodsName,
			@Param("deleteStatus") Integer deleteStatus);
	
	/**
	 * 根据Id查找秒杀商品
	 */
	@Select({ "<script>"
			+ "select * from flashsale_goods "
			+ "where flashsale_goods_id = #{flashSaleGoodsId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 2\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public FlashSaleGoods getFlashSaleGoodsById(@Param("flashSaleGoodsId") Long flashSaleGoodsId,
			@Param("deleteStatus") Integer deleteStatus);
	
}
