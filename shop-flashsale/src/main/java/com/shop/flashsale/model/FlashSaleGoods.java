package com.shop.flashsale.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 秒杀商品实体类
 */
/*
 * 秒杀商品表和商品表分开
 * 原因：不太知道，总感觉应该要分开
 * 普通商品有一口价约束最低价，秒杀商品无约束
 * 秒杀商品约束非常小，而普通商品出于保护商家的前提，必然会加上越来越多约束
 * 那规格和规格值需要再单独分开吗？
 * 感觉如果以后数据量大了一样要分开，因为秒杀商品经常性变化活动与规格
 * 但是普通商品也可以经常变规格啊？不对，普通商品可以通过数据挖掘来分析规格关键字
 * 所以普通商品规格和规格值通常价值较大
 * 而秒杀商品通常不会做数据挖掘，只是一次性消耗品，赔钱赚吆喝，所以从这个角度看还是需要分表
 * 但是太麻烦了，所以我选择暂时先这样。。。。
 */
/*
 * 优化：改为Builder设计模式进行初始化
 */
public class FlashSaleGoods implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long shopId;					//商铺ID
	private Long spuId;						//SPU商品ID
	private Long flashSaleGoodsId;			//秒杀SKU商品ID
	private String flashSaleGoodsName;		//秒杀商品名称
	private Long stock;						//秒杀SKU商品库存量
	private BigDecimal price;				//秒杀价
	private Long specValueId;				//规格值ID
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;		//创建时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;		//修改时间
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime deleteTime;		//删除时间
	
	private Integer verifyStatus;			//1,未审核；2,审核中；3,已审核
	private Integer flashSaleGoodsStatus;	//1,未上架；2,已上架；3,已下架
	private Integer deleteStatus;			//1,未删除；2,已删除
	
	private String specName;				//规格名称
	private String specValueName;			//规格值名称

	public Long getShopId() {
		return shopId;
	}
	public Long getSpuId() {
		return spuId;
	}
	public Long getFlashSaleGoodsId() {
		return flashSaleGoodsId;
	}
	public String getFlashSaleGoodsName() {
		return flashSaleGoodsName;
	}
	public Long getStock() {
		return stock;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public String getSpecName() {
		return specName;
	}
	public Long getSpecValueId() {
		return specValueId;
	}
	public String getSpecValueName() {
		return specValueName;
	}
	public LocalDateTime getCreateTime() {
		return createTime;
	}
	public LocalDateTime getUpdateTime() {
		return updateTime;
	}
	public LocalDateTime getDeleteTime() {
		return deleteTime;
	}
	public Integer getVerifyStatus() {
		return verifyStatus;
	}
	public Integer getFlashSaleGoodsStatus() {
		return flashSaleGoodsStatus;
	}
	public Integer getDeleteStatus() {
		return deleteStatus;
	}
	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}
	public void setSpuId(Long spuId) {
		this.spuId = spuId;
	}
	public void setFlashSaleGoodsId(Long flashSaleGoodsId) {
		this.flashSaleGoodsId = flashSaleGoodsId;
	}
	public void setFlashSaleGoodsName(String flashSaleGoodsName) {
		this.flashSaleGoodsName = flashSaleGoodsName;
	}
	public void setStock(Long stock) {
		this.stock = stock;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public void setSpecName(String specName) {
		this.specName = specName;
	}
	public void setSpecValueId(Long specValueId) {
		this.specValueId = specValueId;
	}
	public void setSpecValueName(String specValueName) {
		this.specValueName = specValueName;
	}
	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}
	public void setDeleteTime(LocalDateTime deleteTime) {
		this.deleteTime = deleteTime;
	}
	public void setVerifyStatus(Integer verifyStatus) {
		this.verifyStatus = verifyStatus;
	}
	public void setFlashSaleGoodsStatus(Integer flashSaleGoodsStatus) {
		this.flashSaleGoodsStatus = flashSaleGoodsStatus;
	}
	public void setDeleteStatus(Integer deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	
	@Override
	public String toString() {
		return "FlashSaleGoods [shopId=" + shopId + ", spuId=" + spuId + ", flashSaleGoodsId=" + flashSaleGoodsId
				+ ", flashSaleGoodsName=" + flashSaleGoodsName + ", stock=" + stock + ", price=" + price + 
				", specName=" + specName + ", specValueId=" + specValueId + ", specValueName="
				+ specValueName + ", createTime=" + createTime + ", updateTime=" + updateTime + ", deleteTime="
				+ deleteTime + ", verifyStatus=" + verifyStatus + ", flashSaleGoodsStatus=" + flashSaleGoodsStatus
				+ ", deleteStatus=" + deleteStatus + "]";
	}
}
