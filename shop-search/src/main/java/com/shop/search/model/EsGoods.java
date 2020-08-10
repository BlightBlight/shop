package com.shop.search.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Elasticsearch商品信息
 */
@Document(indexName = "esGoods", shards = 3,replicas = 3)
public class EsGoods implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private Long esGoodsId;				//ES商品Id
   
	@Field(type = FieldType.Keyword)
	private Long categoryId;			//分类Id
    
    @Field(type = FieldType.Keyword)
    private Long brandId;				//品牌Id
	
    @Field(analyzer = "ik_max_word",type = FieldType.Text)
	private String esGoodsName;			//ES商品名称
	
    @Field(analyzer = "ik_max_word",type = FieldType.Text)
    private String sutTitle;			//商品标题
    
    @Field(analyzer = "ik_max_word",type = FieldType.Text)
    private String keyword;				//关键词
    
    @Field(analyzer = "ik_max_word",type = FieldType.Text)
	private String categoryName;		//分类名称
    
    @Field(analyzer = "ik_max_word",type = FieldType.Text)
	private String brandName;			//品牌名称
    
    @Field(analyzer = "ik_max_word",type = FieldType.Text)
	private String type;				//型号
    
	private BigDecimal price;			//价格
	private Integer stock;				//库存
	
    private List<EsGoodsSpecValue> specValueList;	//规格

	public Long getEsGoodsId() {
		return esGoodsId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public Long getBrandId() {
		return brandId;
	}

	public String getEsGoodsName() {
		return esGoodsName;
	}

	public String getSutTitle() {
		return sutTitle;
	}

	public String getKeyword() {
		return keyword;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public String getBrandName() {
		return brandName;
	}

	public String getType() {
		return type;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Integer getStock() {
		return stock;
	}

	public List<EsGoodsSpecValue> getSpecValueList() {
		return specValueList;
	}

	public void setEsGoodsId(Long esGoodsId) {
		this.esGoodsId = esGoodsId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public void setEsGoodsName(String esGoodsName) {
		this.esGoodsName = esGoodsName;
	}

	public void setSutTitle(String sutTitle) {
		this.sutTitle = sutTitle;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public void setSpecValueList(List<EsGoodsSpecValue> specValueList) {
		this.specValueList = specValueList;
	}

	@Override
	public String toString() {
		return "EsGoods [esGoodsId=" + esGoodsId + ", categoryId=" + categoryId + ", brandId=" + brandId
				+ ", esGoodsName=" + esGoodsName + ", sutTitle=" + sutTitle + ", keyword=" + keyword + ", categoryName="
				+ categoryName + ", brandName=" + brandName + ", type=" + type + ", price=" + price + ", stock=" + stock
				+ ", specValueList=" + specValueList + "]";
	}
}
