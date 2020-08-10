package com.shop.shopcart.vo;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import com.shop.shopcart.model.CartItem;

/**
 * 生成展示订单Vo
 */
public class ShowOrderVo {
	@NotEmpty(message = "订单不能为空")
	List<List<CartItem>> cartItemList;	//订单

	public List<List<CartItem>> getCartItemList() {
		return cartItemList;
	}

	public void setCartItemList(List<List<CartItem>> cartItemList) {
		this.cartItemList = cartItemList;
	}

	@Override
	public String toString() {
		return "ShowOrderVo [cartItemList=" + cartItemList + "]";
	}
}
