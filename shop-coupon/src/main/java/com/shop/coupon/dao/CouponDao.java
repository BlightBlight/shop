package com.shop.coupon.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shop.coupon.model.Coupon;

/**
 * 优惠券Dao层
 */
//@Mapper
public interface CouponDao {
	/**
	 * 新增优惠券
	 */
	/*
	 * @Insert("insert into coupon(coupon_id, coupon_description, place_id, place_name, range_id, range_name,"
	 * + "effect_ids, exclude_effect_ids, total_number, customer_limit_number," +
	 * "create_time, online_status, delete_status)" +
	 * "values(#{couponId}, #{couponDescription}, #{placeId}, #{placeName}, #{rangeId}, #{rangeName},"
	 * +
	 * "#{effectIds}, #{repulsionEffectIds}, #{totalNumber}, #{customerLimitNumber},"
	 * + "#{createTime}, 0, 0)")
	 */
	public int saveCoupon(Coupon coupon);
	
	/**
	 * 删除优惠券
	 */
	@Update("update coupon set delete_status = 1 where coupon_id = #{couponId}")
	public int removeCouponById(@Param("couponId")long couponId);
	
	/**
	 * 修改商品品牌信息
	 */
	@Update({"<script>"
		    + "update goods_category set"
			+ "<trim suffixOverrides=\",\">"
			+ "<if test = \"couponId != null and couponId != '' \">"
			+ "category_id = #{couponId},"
			+ "</if>"
			+ "<if test = \"categoryName != null and categoryName != '' \">"
			+ "category_name = #{categoryName},"
			+ "</if>"
			+ "<if test = \"updateTime != null \">"
			+ "updateTime = #{updateTime},"
			+ "</if>"
			+ "</trim>"
			+ "where category_id = #{categoryId}"
			+ "</script>"})
	public int updateCategory(Coupon coupon);
	
	/**
	 * 通过Id查找商品分类
	 */
	@Select({"<script>"
			+ "select * from coupon"
			+ "where coupon_id = #{couponId}"
			+ "<when test = \"deleteStatus != null and deleteStatus != 2\">"
			+ "and delete_status = {deleteStatus}"
			+ "</when>"
			+ "</script>"})
	public Coupon getCategoryById(@Param("couponId")long couponId, @Param("deleteStatus") int deleteStatus);
	
}
