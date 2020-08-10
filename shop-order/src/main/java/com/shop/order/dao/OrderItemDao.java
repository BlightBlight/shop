package com.shop.order.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shop.order.model.OrderItem;

@Mapper
public interface OrderItemDao {
	/**
	 * 新增子订单
	 */
	@Insert("insert into order_item(order_item_id, order_id, customer_id, shop_id, order_item_price, "
			+ "order_item_number, create_time, order_item_status, delete_status, cancel_status, refund_status) "
			+ "values(#{orderItemId}, #{orderId}, #{customerId}, #{shopId}, #{orderItemPrice}, #{orderItemNumber}, "
			+ "#{createTime}, #{orderItemStatus}, #{deleteStatus}, #{cancelStatus}, #{refundStatus}) ")
	public int saveOrderItem(OrderItem orderItem);

	/**
	 * 删除子订单
	 * @param orderItemId 子订单Id
	 * @param deleteStatus 1,未删除；2,删除中；3,已删除
	 */
	@Update("update order_item set "
			+ "delete_status = #{deleteStatus} "
			+ "where order_item_id = #{orderItemId} ")
	public int removeOrderItemById(@Param("orderItemId") Long orderItemId, @Param("deleteStatus") Integer deleteStatus);

	/**
	 * 修改子订单状态
	 * @param orderItemId 子订单Id
	 * @param orderItemStatus 1,下单(create)；2,付款(pay)；3,卖家发货(deliver)；4,买家收货(receive)；5,退货(rereturn)
	 */
	@Update("update order_item set "
			+ "order_item_status = #{orderItemStatus} "
			+ "where order_item_id = #{orderItemId} "
			+ "and cancel_status != 3 ")
	public int updateOrderItemStatusById(@Param("orderItemId") Long orderItemId, @Param("orderItemStatus") Integer orderItemStatus);

	/**
	 * 修改子订单取消状态
	 * @param orderItemId 子订单Id
	 * @param cancelStatus 1,未取消；2,取消中；3,已取消 
	 */
	@Update("update order_item set "
			+ "cancel_status = #{cancelStatus} "
			+ "where order_item_id = #{orderItemId} "
			+ "and delete_status != 3 ")
	public int updateOrderItemCancelStatusById(@Param("orderItemId") Long orderItemId,
			@Param("cancelStatus") Integer cancelStatus);

	/**
	 * 修改子订单退款状态
	 * @param orderItemId 子订单Id
	 * @param refundStatus 1,无退款；2,退款中；3,部分退款；4,全退款
	 */
	@Update("update order_item set "
			+ "refund_status = #{refundStatus} "
			+ "where order_item_id = #{orderItemId} "
			+ "and delete_status != 3 ")
	public int updateOrderItemrefundStatusById(@Param("orderItemId") Long orderItemId,
			@Param("refundStatus") Integer refundStatus);

	/**
	 * 根据子订单Id查找子订单
	 * @param orderItemId 子订单Id
	 * @param deleteStatus 0,全部；1,未删除；2,伪删除；3,已删除
	 */
	@Select({"<script>"
			+ "select * from order_item "
			+ "where order_item_id = #{orderItemId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = {deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public OrderItem getOrderItemById(@Param("orderItemId") Long orderItemId, @Param("deleteStatus") Integer deleteStatus);

	/**
	 * 根据子订单Id查找主订单是否存在
	 * @param orderItemId 子订单Id
	 */
	@Select("select order_item_id, delete_status from order_item "
			+ "where order_item_id = #{orderItemId} ")
	public OrderItem isOrderItemExists(@Param("orderItemId") Long orderItemId);

	/**
	 * 根据主订单Id查找所有子订单
	 * @param orderId 总订单Id
	 * @param deleteStatus 0,全部；1,未删除；2,伪删除；3,已删除
	 */
	@Select({"<script>"
			+ "select * from order_item "
			+ "where order_id = #{orderId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public List<OrderItem> listOrderItemByOrderId(@Param("orderId") Long orderId, Integer deleteStatus);

	/**
	 * 根据用户Id查找所有子订单
	 * @param customerId 用户Id
	 * @param deleteStatus 0,全部；1,未删除；2,伪删除；3,已删除
	 */
	@Select({"<script>"
			+ "select * from order_item "
			+ "where customer_id = #{customerId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public List<OrderItem> listOrderItemByCustomerId(@Param("customerId") Long customerId, Integer deleteStatus);

}
