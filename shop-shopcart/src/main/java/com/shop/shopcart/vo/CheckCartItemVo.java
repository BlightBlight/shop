package com.shop.shopcart.vo;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * 勾选购物车商品Vo
 */
public class CheckCartItemVo {
	@NotNull(message = "勾选商品不能为空")
	List<UpdateCartItemVo> checkCartItemList;	//勾选购物车列表
	
	public List<UpdateCartItemVo> getCheckCartItemList() {
		return checkCartItemList;
	}

	public void setCheckCartItemList(List<UpdateCartItemVo> checkCartItemList) {
		this.checkCartItemList = checkCartItemList;
	}

	@Override
	public String toString() {
		return "CheckCartItemVo [checkCartItemList=" + checkCartItemList + "]";
	}
}
