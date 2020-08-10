package com.shop.portal.model;

import java.io.Serializable;

public class A implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long skuId;					//SKU商品ID

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	@Override
	public String toString() {
		return "A [skuId=" + skuId + "]";
	}
	
	
}
