package com.shop.goods.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shop.goods.model.SKUGoodsSpecValue;
import com.shop.goods.model.SKUGoods;

@Mapper
public interface SKUGoodsDao {
	/**
	 * 新增SKU商品基本信息
	 */
	@Insert("insert into goods_sku(sku_id, spu_id, "
			+ "spec_value_id, shop_id, sku_name, price, stock, create_time, verify_status, sku_status, delete_status) "
			+ "values(#{skuId}, #{spuId}, #{specValueId}, "
			+ "#{shopId}, #{skuName}, #{price}, #{stock}, #{createTime}, #{verifyStatus}, #{skuStatus}, #{deleteStatus}) ")
	public int saveSKUGoods(SKUGoods skuGoods);
	
	/**
	 * 批量新增SKU商品基本信息
	 */
	@Insert({"<script>"
			+ "<foreach collection = 'list' item = 'item' index = 'index' open = '(' separator =' ,' close = ')'>"
			+ "insert into goods_sku(sku_id, spu_id, "
			+ "spec_value_id, shop_id, sku_name, price, stock, create_time, delete_status) "
			+ "values(#{item.skuId}, #{item.spuId}, #{item.specValueId}, "
			+ "#{item.shopId}, #{item.skuName}, #{item.price}, #{item.stock}, #{item.createTime}, #{deleteStatus}) "
		    + "</foreach>"
			+ "</script>"})
	public int batchSaveSKUGoods(@Param("list") List<SKUGoods> skuGoodsList);
	
	/**
	 * 新增规格值表信息
	 */
	@Insert("insert into goods_spec_values(spec_value_id, spec_id, spec_value_name, create_time, "
			+ "delete_status) "
			+ "values(#{specValueId}, #{specId}, #{specValueName}, #{createTime}, #{deleteStatus})")
	public int saveSKUGoodsSpecValue(SKUGoodsSpecValue goodsSpecValue);
	
	/**
	 * 批量新增规格值表基本信息
	 */
	@Insert({"<script>"
			+ "<foreach collection = 'list' item = 'item' index = 'index' open = '(' separator =' ,' close = ')'>"
			+ "insert into goods_spec_values(spec_value_id, spec_id, "
			+ "spec_value_name, delete_status) "
			+ "values(#{item.specValueId}, #{item.specId}, #{item.specValueName}, "
			+ "#{item.createTime}, #{deleteStatus}) "
		    + "</foreach>"
			+ "</script>"})
	public int batchSaveSKUGoodsSpecValue(@Param("list") List<SKUGoodsSpecValue> goodsSpecValueList);
	
	/**
	 * 根据名称删除SKU商品
	 * @param skuName sku商品名称
	 */
	@Update("update goods_sku set "
			+ "delete_status = 1 "
			+ "where sku_name = #{skuName} ")
	public int removeSKUGoodsByName(@Param("skuName") String skuName);
	
	/**
	 * 根据Id删除SKU商品
	 * @param skuId sku商品Id
	 */
	@Update("update goods_sku set "
			+ "delete_status = 1 "
			+ "where sku_id = #{skuId} ")
	public int removeSKUGoodsById(@Param("skuId") Long skuId);
	
	/**
	 * 根据Id删除规格值表
	 * @param specValueId 规格值Id
	 */
	@Update("update goods_spec_values set "
			+ "delete_status = 1 "
			+ "where spec_value_id = #{specValueId} "
			+ "and delete_status != 1 ")
	public int removeSKUGoodsSpecValueById(@Param("specValueId") Long specValueId);
	
	/**
	 * 根据skuId批量删除SKU商品
	 */
	@Update({"<script>"
		    + "update goods_sku set "
		    + "delete_status = 1 "
		    + "where sku_id in"
		    + "<foreach collection = 'list' item = 'item' open = '(' separator =',' close = ')'>"
		    + "#{item}"
		    + "</foreach>"
			+ "</script>"})
	public int batchRemoveSKUGoodsById(@Param("list") List<Long> list);
	
	/**
	 * 根据specValueId批量删除规格
	 */
	@Update({"<script>"
			+ "update goods_spec_values set "
			+ "delete_status = 1 "
			+ "where spec_value_id in"
		    + "<foreach collection = 'list' item = 'item' open = '(' separator =',' close = ')'>"
		    + "#{item}"
		    + "</foreach>"
			+ "</script>"})
	public int batchRemoveSKUGoodsSpecValueById(@Param("list") List<Long> list);
	
	/**
	 * 修改SKU商品信息
	 */
	@Update({"<script>"
		    + "update goods_sku set "
			+ "<trim suffixOverrides=\",\">"
			+ "<if test = \"spuId != null and spuId != '' \">"
			+ "spu_id = #{spuId}, "
			+ "</if>"
			+ "<if test = \"skuName != null and skuName != '' \">"
			+ "sku_name = #{skuName}, "
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
			+ "where sku_id = #{skuId} "
			+ "</script>"})
	public int updateSKUGoods(SKUGoods skuGoods);
	
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
	public int updateSKUGoodsSpecValue(SKUGoodsSpecValue goodsSpecValue);
	
	/**
	 * 根据名称递减SKU商品
	 * @param skuName sku商品名称
	 * @param delta 递减数量（正整数）
	 */
	@Update("update goods_sku set "
			+ "stock = stock - #{delta} "
			+ "where sku_name = #{skuName} "
			+ "and delete_status != 1 " )
	public int decrSKUGoodsByName(@Param("skuName") String skuName, @Param("delta") Long delta);
	
	/**
	 * 根据名称查找SKU商品
	 * @param spuId spu商品Id
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_sku "
			+ "where sku_name = #{skuName} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public SKUGoods getSKUGoodsByName(@Param("skuName") String skuName, @Param("deleteStatus") Integer deleteStatus);
	
	/**
	 * 根据Id查找SKU商品
	 * @param skuId sku商品Id
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_sku "
			+ "where sku_id = #{skuId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public SKUGoods getSKUGoodsById(@Param("skuId") Long skuId, @Param("deleteStatus") Integer deleteStatus);
	
	/**
	 * 根据specValueId查找SKU商品
	 * @param specValueId 规格值Id
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_sku "
			+ "where spec_value_id = #{specValueId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public SKUGoods getSKUGoodsBySpecValueId(@Param("specValueId") Long specValueId, @Param("deleteStatus") Integer deleteStatus);
	
	/**
	 * 根据Id查找SKU商品是否存在
	 * @param skuId sku商品Id
	 */
	@Select("select sku_id, verify_status from goods_sku "
			+ "where sku_id = #{skuId} "
			+ "and delete_status != 2 ")
	public SKUGoods isSKUGoodsExistsById(@Param("skuId") Long skuId);
	
	/**
	 * 根据名称查找SKU商品是否存在
	 * @param skuName sku商品名称
	 */
	@Select("select sku_id, verify_status from goods_sku "
			+ "where sku_name = #{skuName} "
			+ "where delete_status != 2 ")
	public SKUGoods isSKUGoodsExistsByName(@Param("skuName") String skuName);
	
	/**
	 * 根据spuId查找所有SKU商品
	 * @param spuId spu商品Id
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_sku "
			+ "where spu_id = #{spuId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public List<SKUGoods> listSKUGoodsBySPUId(@Param("spuId") Long spuId, @Param("deleteStatus") Integer deleteStatus);
	
	/**
	 * 根据名称查找规格值
	 * @param specValue 规格值
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_spec_values "
			+ "where spec_value_name = #{specValueName} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public SKUGoodsSpecValue getSKUGoodsSpecValueByName(@Param("specValueName") String specValueName, @Param("deleteStatus") Integer deleteStatus);
	
	/**
	 * 根据Id查找规格值
	 * @param specValueId 规格值Id
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_spec_values "
			+ "where spec_value_id = #{specValueId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public SKUGoodsSpecValue getSKUGoodsSpecValueById(@Param("specValueId") Long specValueId, @Param("deleteStatus") Integer deleteStatus);
}
