package com.shop.order.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shop.order.model.Order;

@Mapper
public interface OrderDao {
	/**
	 * 新增主订单
	 */
	@Insert("insert into order_superior(order_id, customer_id, order_price, order_number, create_time, delete_status) "
			+ "values(#{orderId}, #{customerId}, #{orderPrice}, #{orderNumber}, #{createTime}, #{deleteStatus}) ")
	public int saveOrder(Order order);

	/**
	 * 伪删除主订单
	 */
	@Update("update order set "
			+ "delete_status = 2 "
			+ "where order_id = #{orderId} ")
	public int removeOrder(@Param("orderId") Long orderId);

	/**
	 * 真的删除主订单
	 */

	@Update("update order set "
			+ "delete_status = 3 "
			+ "where order_id = #{orderId} ")
	public int removeOrderReal(@Param("orderId") Long orderId);

	/**
	 * 根据orderId查找主订单
	 */
	@Select("select * from order "
			+ "where customer_id = #{customerId} ")
	public Order getOrderById(@Param("orderId") Long orderId);

	/**
	 * 根据orderId查找主订单是否存在
	 */
	@Select("select order_id from order "
			+ "where order_id = #{orderId} ")
	public Order isOrderExists(@Param("orderId") Long orderId);

	/**
	 * 根据orderId查找主订单
	 */
	@Select("select * from order "
			+ "where customer_id = #{customerId} ")
	public List<Order> listOrderById(@Param("orderId") Long orderId);
			 
}
