package com.shop.order.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shop.order.model.OrderDetail;

@Mapper
public interface OrderDetailDao {
	/**
	 * 新增订单详情
	 */
	@Insert("insert into order_detail(order_detail_id, order_item_id, customer_id, shop_id, sku_id, shop_name, sku_name, number, "
			+ "price, spec_value_id, spec_value_name, order_detail_price, create_time, comment_status)"
			+ "values(#{orderDetailId}, #{orderItemId}, #{customerId}, #{shopId}, #{skuId}, #{shopName}, "
			+ "#{skuName}, #{number}, #{price}, #{specValueId}, #{specValueName}, #{orderDetailPrice}, #{createTime}, #{commentStatus})")
	public int saveOrderDetail(OrderDetail orderDetail);

	/**
	 * 修改订单详情评论状态
	 */
	@Update("update order_detail set "
			+ "comment_status = #{commentStatus} "
			+ "where order_detail_id = #{orderDetailId} ")
	public int updateOrderDetailCommentStatusById(@Param("orderDetailId") Long orderDetailId, 
			@Param("commentStatus") Boolean commentStatus);

	/**
	 * 根据订单详情Id查找订单详情
	 */
	@Select("select * from order_detail "
			+ "where order_detail_id = #{orderDetailId} ")
	public OrderDetail getOrderDetailById(@Param("orderDetailId") Long orderDetailId);

	/**
	 * 根据子订单Id查找所有订单详情
	 */
	@Select("select * from order_detail "
			+ "where order_item_id = #{orderItemId} ")
	public OrderDetail getOrderDetailByOrderItemId(@Param("orderItemId") Long orderItemId);
}
