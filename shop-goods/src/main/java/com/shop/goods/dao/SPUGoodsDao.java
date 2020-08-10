package com.shop.goods.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shop.goods.model.SPUGoodsSpec;
import com.shop.goods.model.SPUGoods;

@Mapper
public interface SPUGoodsDao {
	/**
	 * 新增SPU商品信息
	 */
	@Insert("insert into goods_spu(spu_id, spec_id, "
			+ "category_id, brand_id, pre_id, level, type, spu_name, create_time, verify_status, delete_status) "
			+ "values(#{spuId}, #{specId}, #{categoryId}, "
			+ "#{brandId}, #{preId}, #{level}, #{type}, #{spuName}, #{createTime}, #{verifyStatus}, #{deleteStatus}) ")
	public int saveSPUGoods(SPUGoods spuGoods);
	
	/**
	 * 新增规格表信息
	 */
	@Insert("insert into goods_spec(spec_id, spec_name, create_time, "
			+ "delete_status) "
			+ "values(#{specId}, #{specName}, #{createTime}, #{deleteStatus}) ")
	public int saveSPUGoodsSpec(SPUGoodsSpec goodsSpec);
	
	/**
	 * 根据名称删除SPU商品信息
	 * @param spuName spu商品名称
	 */
	@Update("update goods_spu set "
			+ "delete_status = 2 "
			+ "where spu_name = #{spuName} ")
	public int removeSPUGoodsByName(@Param("spuName") String spuName);
	
	/**
	 * 根据Id删除SPU商品信息
	 * @param spuId spu商品Id
	 */
	@Update("update goods_spu set "
			+ "delete_status = 2 "
			+ "where spu_id = #{spuId} ")
	public int removeSPUGoodsById(@Param("spuId") Long spuId);
	
	/**
	 * 根据Id删除规格信息
	 * @param specId 规格Id
	 */
	@Update("update goods_spec set "
			+ "delete_status = 2 "
			+ "where spec_id = #{specId} ")
	public int removeSPUGoodsSpecById(@Param("specId") Long specId);
	
	/**
	 * 修改SPU商品信息
	 */
	@Update({"<script>"
		    + "update goods_spu set "
			+ "<trim suffixOverrides=\",\">"
			+ "<if test = \"categoryId != null and categoryId != '' \">"
			+ "category_id = #{categoryId}, "
			+ "</if>"
			+ "<if test = \"brandId != null and brandId != '' \">"
			+ "brand_id = #{brandId}, "
			+ "</if>"
			+ "<if test = \"spuName != null and spuName != '' \">"
			+ "spu_name = #{spuName}, "
			+ "</if>"
			+ "<if test = \"updateTime != null \">"
			+ "update_time = #{updateTime}, "
			+ "</if>"
			+ "</trim>"
			+ "where spu_id = #{spuId} "
			+ "</script>"})
	public int updateSPUGoods(SPUGoods spuGoods);
	
	/**
	 * 修改规格信息
	 */
	@Update({"<script>"
		    + "update goods_spec set "
			+ "<trim suffixOverrides=\",\">"
			+ "<if test = \"specName != null and specName != '' \">"
			+ "spec_name = #{specName}, "
			+ "</if>"
			+ "<if test = \"updateTime != null \">"
			+ "update_time = #{updateTime}, "
			+ "</if>"
			+ "</trim>"
			+ "where spec_id = #{specId} "
			+ "</script>"})
	public int updateSPUGoodsSpec(SPUGoodsSpec spuGoodsSpec);
	
	/**
	 * 根据名称查找SPU商品信息
	 * @param spuName spu商品名称
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_spu "
			+ "where spu_name = #{spuName} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public SPUGoods getSPUGoodsByName(@Param("spuName") String spuName, Integer deleteStatus);
	
	/**
	 * 根据Id查找SPU商品信息
	 * @param spuId spu商品Id
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_spu "
			+ "where spu_id = #{spuId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public SPUGoods getSPUGoodsById(@Param("spuId") Long spuId, Integer deleteStatus);
	
	/**
	 * 根据specId查找SPU商品信息
	 * @param specId 规格Id
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_spu "
			+ "where spec_id = #{specId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public SPUGoods getSPUGoodsBySpecId(@Param("specId") Long specId, Integer deleteStatus);
	
	/**
	 * 根据Id查找SPU商品是否存在
	 * @param spuId spu商品Id
	 */
	@Select("select spu_id, verify_status from goods_spu "
			+ "where spu_id = #{spuId} "
			+ "where delete_status != 1 ")
	public SPUGoods isSPUGoodsExists(@Param("spuId") Long spuId);
	
	/**
	 * 根据规格查找SPU商品是否存在
	 * @param categoryId 商品分类Id
	 * @param brandId 商品品牌Id
	 * @param type 型号
	 */
	@Select("select spu_id, verify_status from goods_spu "
			+ "where category_id = #{categoryId} "
			+ "and brand_id = #{brandId} "
			+ "and type = #{type} "
			+ "and delete_status != 2")
	public SPUGoods isSPUGoodsExistsByType(@Param("categoryId") Long categoryId, 
			@Param("brandId") Long brandId, @Param("type") String type);
	
	/**
	 * 根据名称查找规格
	 * @param specName 规格名称
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_spec "
			+ "where spec_name = #{specName} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public SPUGoodsSpec getSPUGoodsSpecByName(@Param("specName") String specName, @Param("deleteStatus") Integer deleteStatus);
	
	/**
	 * 根据Id查找规格
	 * @param specId 规格Id
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_spec "
			+ "where spec_id = #{specId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public SPUGoodsSpec getSPUGoodsSpecById(@Param("specId") Long specId, @Param("deleteStatus") Integer deleteStatus);
}
