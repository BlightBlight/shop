package com.shop.goods.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shop.goods.model.Category;

@Mapper
public interface CategoryDao {
	/**
	 * 新增商品分类信息
	 */
	@Insert("insert into goods_category(category_id, category_name, create_time, delete_status) "
			+ "values(#{categoryId}, #{categoryName}, #{createTime}, #{deleteStatus})")
	public int saveCategory(Category category);
	
	/**
	 * 删除商品分类信息
	 * @param categoryId 商品分类Id
	 */
	@Update("update goods_category set "
			+ "delete_status = 2 "
			+ "where category_id = #{categoryId} ")
	public int removeCategoryById(@Param("categoryId") Long categoryId);
	
	/**
	 * 修改商品品牌信息
	 */
	@Update({"<script>"
		    + "update goods_category set "
			+ "<trim suffixOverrides=\",\">"
			+ "<if test = \"categoryName != null and categoryName != '' \">"
			+ "category_name = #{categoryName}, "
			+ "</if>"
			+ "<if test = \"updateTime != null \">"
			+ "update_time = #{updateTime}, "
			+ "</if>"
			+ "</trim>"
			+ "where category_id = #{categoryId} "
			+ "</script>"})
	public int updateCategory(Category category);
	
	/**
	 * 通过名称查找商品分类
	 * @param categoryName 商品分类名称
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_category "
			+ "where category_name = #{categoryName} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public Category getCategoryByName(@Param("categoryName")String categoryName, @Param("deleteStatus") Integer deleteStatus);
	
	/**
	 * 通过Id查找商品分类
	 * @param categoryId 商品分类Id
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from goods_category "
			+ "where category_id = #{categoryId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus}"
			+ "</when>"
			+ "</script>"})
	public Category getCategoryById(@Param("categoryId") Long categoryId, @Param("deleteStatus") Integer deleteStatus);
	
}
