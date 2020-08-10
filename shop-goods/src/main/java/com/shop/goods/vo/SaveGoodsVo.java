package com.shop.goods.vo;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 创建商品的Vo
 */
public class SaveGoodsVo {
	@NotNull(message = "spu商品Id不能为空")
	private Long spuId;			//SPU商品Id
	
	@NotEmpty(message = "spu商品名称不能为空")
	private String spuName;		//商品名称
	
	@NotEmpty(message = "spu商品规格不能为空")
	private String specName;	//规格名称，JSON形式
	
	@NotNull(message = "sku商品不能为空")
	private List<SaveSKUGoodsVo> skuList;	//sku队列
	
	@NotNull(message = "创建时间不能为空")	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;	//创建时间

	public Long getSpuId() {
		return spuId;
	}

	public void setSpuId(Long spuId) {
		this.spuId = spuId;
	}

	public String getSpuName() {
		return spuName;
	}

	public void setSpuName(String spuName) {
		this.spuName = spuName;
	}

	public String getSpecName() {
		return specName;
	}

	public void setSpecName(String specName) {
		this.specName = specName;
	}

	public List<SaveSKUGoodsVo> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<SaveSKUGoodsVo> skuList) {
		this.skuList = skuList;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "GoodsVo [spuId=" + spuId + ", spuName=" + spuName + ", specName=" + specName + ", skuList=" + skuList
				+ ", createTime=" + createTime + "]";
	}
}
