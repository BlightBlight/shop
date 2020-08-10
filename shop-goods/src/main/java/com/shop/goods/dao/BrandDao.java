package com.shop.goods.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shop.goods.model.Brand;

@Mapper
public interface BrandDao {
	/**
	 * 新增商品品牌信息
	 */
	@Insert("insert into goods_brand(category_id, brand_id, brand_name, create_time, delete_status) "
			+ "values(#{categoryId}, #{brandId}, #{brandName}, #{createTime}, #{deleteStatus})")
	public int saveBrand(Brand brand);
	
	/**
	 * 根据名称批量删除品牌
	 * @param brandName 商品品牌名称
	 */
	@Update("update goods_brand set "
		    + "delete_status = 1 "
		    + "where brand_name = #{brandName} ")
	public int removeBrandByName(@Param("brandName") String brandName);
	
	/**
	 * 根据Id批量删除品牌
	 */
	@Update({"<script>"
		    + "<foreach collection = 'list' item = 'item' index = 'index' open = '(' separator =' ,' close = ')'>"
		    + "update goods_brand set "
		    + "delete_status = 2 "
		    + "where brand_id = #{item} "
		    + "</foreach>"
			+ "</script>"})
	public int batchRemoveBrandById(@Param("list") List<Long> list);
	
	/**
	 * 根据CategoryId批量删除品牌
	 * @param categoryId 商品分类Id
	 */
	@Update("update goods_brand set "
		    + "delete_status = 2 "
		    + "where category_id = #{categoryId} ")
	public int batchRemoveBrandByCategoryId(@Param("categoryId") Long categoryId);
	
	/**
	 * 修改商品品牌信息
	 */
	@Update({"<script>"
		    + "update goods_brand set "
			+ "<trim suffixOverrides=\",\">"
			+ "<if test = \"categoryId != null and categoryId != '' \">"
			+ "category_id = #{categoryId}, "
			+ "</if>"
			+ "<if test = \"brandName != null and brandName != '' \">"
			+ "brand_name = #{brandName}, "
			+ "</if>"
			+ "<if test = \"updateTime != null \">"
			+ "update_time = #{updateTime}, "
			+ "</if>"
			+ "<if test =\"deleteStatus != null and deleteStatus != '' \">"
			+ "delete_status = #{deleteStatus}, "
			+ "</if>"
			+ "</trim>"
			+ "where brand_id = #{brandId} "
			+ "</script>"})
	public int updateBrand(Brand brand);
	
	/**
	 * 根据Id查找所有商品品牌
	 * @param brandId 商品品牌Id
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_brand "
			+ "where brand_id = #{brandId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public Brand getBrandById(@Param("brandId") Long brandId, @Param("deleteStatus") Integer deleteStatus);
	
	/**
	 * 根据名称查找所有商品品牌
	 * @param brandName 商品品牌名称
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_brand "
			+ "where brand_name = #{brandName} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public Brand getBrandByName(@Param("brandName") String brandName, @Param("deleteStatus") Integer deleteStatus);
	
	/**
	 * 根据categoryId查找所有商品品牌Id
	 * @param categoryId 商品分类Id
	 */
	@Select("select brand_id from goods_brand "
			+ "where category_id = #{categoryId} ")
	public List<Long> listBrandIdByCategoryId(@Param("categoryId") Long categoryId);
}
