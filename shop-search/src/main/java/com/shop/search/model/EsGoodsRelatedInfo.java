package com.shop.search.model;

import java.util.List;


/**
 * 搜索相关商品品牌名称，分类名称及规格
 */
public class EsGoodsRelatedInfo {
	private List<String> brandNames;		//品牌名称
    private List<String> categoryNames;		//分类名称
    private List<GoodsSpec> goodsSpec;		//商品规格
    
    public List<String> getBrandNames() {
		return brandNames;
	}

	public List<String> getCategoryNames() {
		return categoryNames;
	}

	public List<GoodsSpec> getGoodsSpec() {
		return goodsSpec;
	}

	public void setBrandNames(List<String> brandNames) {
		this.brandNames = brandNames;
	}

	public void setCategoryNames(List<String> categoryNames) {
		this.categoryNames = categoryNames;
	}

	public void setGoodsSpec(List<GoodsSpec> goodsSpec) {
		this.goodsSpec = goodsSpec;
	}

	public static class GoodsSpec{
        private long specId;				//规格Id
        private String specName;			//规格
        private List<String> specValues;	//规格值
		public long getSpecId() {
			return specId;
		}
		public String getSpecName() {
			return specName;
		}
		public List<String> getSpecValues() {
			return specValues;
		}
		public void setSpecId(long specId) {
			this.specId = specId;
		}
		public void setSpecName(String specName) {
			this.specName = specName;
		}
		public void setSpecValues(List<String> specValues) {
			this.specValues = specValues;
		}
		
		@Override
		public String toString() {
			return "GoosSpec [specId=" + specId + ", specName=" + specName + ", specValues=" + specValues + "]";
		}
    }

	@Override
	public String toString() {
		return "EsGoodsRelatedInfo [brandNames=" + brandNames + ", categoryNames=" + categoryNames + ", goodsSpec="
				+ goodsSpec + "]";
	}
}
