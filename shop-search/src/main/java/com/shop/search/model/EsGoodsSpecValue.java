package com.shop.search.model;

import java.io.Serializable;

/**
 * 搜索中的商品规格值信息
 */
public class EsGoodsSpecValue implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private long ESGoodsId;				//商品Id
    private Long ESGoodsSpecId;			//商品规格值Id
    //属性参数：0->规格；1->规格值
    private int type;
    //属性名称
    private String specName;
    //属性值
    private String specValue;
    
	public long getESGoodsId() {
		return ESGoodsId;
	}
	public Long getESGoodsSpecId() {
		return ESGoodsSpecId;
	}
	public int getType() {
		return type;
	}
	public String getSpecName() {
		return specName;
	}
	public String getSpecValue() {
		return specValue;
	}
	public void setESGoodsId(long eSGoodsId) {
		ESGoodsId = eSGoodsId;
	}
	public void setESGoodsSpecId(Long eSGoodsSpecId) {
		ESGoodsSpecId = eSGoodsSpecId;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setSpecName(String specName) {
		this.specName = specName;
	}
	public void setSpecValue(String specValue) {
		this.specValue = specValue;
	}
	
	@Override
	public String toString() {
		return "ESGoodsSpecValue [ESGoodsId=" + ESGoodsId + ", ESGoodsSpecId=" + ESGoodsSpecId + ", type=" + type
				+ ", specName=" + specName + ", specValue=" + specValue + "]";
	}
}
