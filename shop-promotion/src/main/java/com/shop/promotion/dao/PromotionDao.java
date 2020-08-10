package com.shop.promotion.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shop.promotion.model.Promotion;

@Mapper
public interface PromotionDao {
	/**
	 * 新增活动信息
	 */
	@Insert("insert into promotion(promotion_id, admin_id, promotion_name, create_time, start_time, end_time, promotion_status)"
			+ "values(#{promotionId}, #{adminId}, #{promotionName}, #{createTime}, #{startTime}, #{endTime}, 0)")
	public int savePromotion(Promotion promotion);
	
	/**
	 * 删除活动信息
	 * @param promotionId 活动Id
	 */
	@Update("update promotion set delete_status = 1 where promotion_id = #{promotionId}")
	public int removeCategoryById(@Param("promotionId") long promotionId);
	
	/**
	 * 修改活动信息
	 */
	@Update({"<script>"
		    + "update promotion set"
			+ "<trim suffixOverrides=\",\">"
			+ "<if test = \"promotionId != null and promotionId != '' \">"
			+ "promotion_id = #{promotionId},"
			+ "</if>"
			+ "<if test = \"adminId != null and adminId != '' \">"
			+ "admin_id = #{adminId},"
			+ "</if>"
			+ "<if test = \"promotionName != null and promotionName != '' \">"
			+ "promotion_name = #{promotionName},"
			+ "</if>"
			+ "<if test = \"updateTime != null \">"
			+ "update_time = #{updateTime},"
			+ "</if>"
			+ "<if test = \"startTime != null \">"
			+ "start_time = #{startTime},"
			+ "</if>"
			+ "<if test = \"endTime != null \">"
			+ "end_time = #{endTime},"
			+ "</if>"
			+ "<if test =\"deleteStatus != null and deleteStatus != '' \">"
			+ "delete_status = #{deleteStatus},"
			+ "</if>"
			+ "<if test =\"promotionStatus != null and promotionStatus != '' \">"
			+ "promotion_status = #{promotionStatus},"
			+ "</if>"
			+ "</trim>"
			+ "where promotion_id = #{promotionId}"
			+ "</script>"})
	public int updatePromotion(Promotion promotion);
	
	/**
	 * 修改活动上下线状态
	 * @param promotionId 活动Id
	 * @param promotionStatus 0,未上线；1,已上线
	 */
	@Update("update promotion set"
			+ "promotion_status = #{promotionStatus}"
			+ "where promotion_id = #{promotionId} and delete_status != 1")
	public int updatePromotionStatusById(long customerId, int promotionStatus);
	
	/**
	 * 根据Id查找活动
	 * @param promotionId 活动Id
	 * @param deleteStatus 0,未删除；1,已删除；2,全部
	 */
	@Select({"<script>"
			+ "select * from promotion"
			+ "where promotion_id = #{promotionId}"
			+ "<when test = \"deleteStatus != null and deleteStatus != 2\">"
			+ "and delete_status = {deleteStatus}"
			+ "</when>"
			+ "</script>"})
	public Promotion getPromotionById(@Param("promotionId") long promotionId, @Param("deleteStatus") int deleteStatus);
}
