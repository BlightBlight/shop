package com.shop.shopcart.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shop.shopcart.model.CartItem;

@Mapper
public interface ShopCartDao {
	/**
	 * 新增cartItem
	 */
	@Insert("insert into customer_cartitem(cartitem_id, customer_id, sku_id, shop_id, spec_value_id, spec_value_name, price, number, "
			+ "create_time, select_status, delete_status, buy_status) "
			+ "values(#{cartItemId}, #{customerId}, #{skuId}, #{shopId}, #{specValueId}, #{specValueName}, #{price}, "
			+ "#{number}, #{createTime}, #{selectStatus}, #{deleteStatus}, #{buyStatus}) ")
	public int saveCartItem(CartItem cartItem);
	
	/**
	 * 根据Id删除cartItem
	 * @param cartItemId 购物车选项Id
	 * @param deleteTime 删除时间
	 */
	@Update("update customer_cartitem set "
			+ "delete_time = #{deleteTime}, "
			+ "delete_status = 2 "
			+ "where cartitem_id = #{cartItemId} ")
	public int removeCartItemById(@Param("cartItemId") Long cartItemId, @Param("deleteTime") LocalDateTime deleteTime);
	
	/**
	 * 修改购物车商品数量
	 * @param cartItemId 购物车选项Id
	 * @param number 商品数量
	 * @param updateTime 修改时间
	 */
	@Update("update customer_cartitem set "
			+ "number = #{number}, "
			+ "update_time = #{updateTime} "
			+ "where cartitem_id = #{cartItemId} ")
	public int updateCartItemNumberById(@Param("cartItemId") Long cartItemId, @Param("number") Long number, 
			@Param("updateTime") LocalDateTime updateTime);
	
	/**
	 * 修改购物车商品规格值
	 * @param cartItemId 购物车选项Id
	 * @param skuId sku商品Id
	 * @param price sku商品价格
	 * @param specValueId 规格值Id
	 * @param specValueName 规格值名称
	 * @param updateTime 修改时间
	 */
	@Update("update customer_cartitem set "
			+ "sku_id = #{skuId}, "
			+ "price = #{price}, "
			+ "spec_value_id = #{specValueId}, "
			+ "spec_value_name = #{specValueName}, "
			+ "update_time = #{updateTime} "
			+ "where cartitem_id = #{cartItemId} ")
	public int updateCartItemSpecValueById(@Param("cartItemId") Long cartItemId, @Param("skuId") Long skuId, 
			@Param("price") BigDecimal price, @Param("specValueId") Long specValueId, 
			@Param("specValueName") String specValueName, @Param("updateTime") LocalDateTime updateTime);
	
	/**
	 * 根据cartItemId查找购物车选项
	 * @param cartItemId 购物车选项Id
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from customer_cartitem "
			+ "where cartitem_id = #{cartItemId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public CartItem getCartItemById(@Param("cartItemId") Long cartItemId, Integer deleteStatus);
	
	/**
	 * 根据customerId和skuId查找购物车选项
	 * @param cartItemId 购物车选项Id
	 * @param deleteStatus 0,全部；1,未删除；2,已删除
	 */
	@Select({"<script>"
			+ "select * from customer_cartitem "
			+ "where customer_id = #{customerId} "
			+ "and sku_id = #{skuId}"
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public CartItem getCartItemByCustomerIdAndSKUId(@Param("customerId") Long customerId, @Param("skuId") Long skuId, Integer deleteStatus);
	
	/**
	 * 根据用户Id查找所有购物车选项，按
	 * @param customerId 用户Id
	 * @param currentTime 系统时间，所以比这前的
	 */
	@Select("select * from customer_cartitem "
			+ "where customer_id = #{customerId} "
			+ "and delete_status != 2 "
			+ "order by "
			+ "create_time desc ")
	public List<CartItem> listCartItemById(@Param("customerId") Long customerId);
	
}
