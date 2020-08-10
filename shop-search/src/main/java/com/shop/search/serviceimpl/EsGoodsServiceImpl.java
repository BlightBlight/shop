package com.shop.search.serviceimpl;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.nested.InternalNested;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.shop.search.dao.EsGoodsDao;
import com.shop.search.model.EsGoods;
import com.shop.search.model.EsGoodsRelatedInfo;
import com.shop.search.repository.EsGoodsRepository;
import com.shop.search.service.EsGoodsService;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 商品搜索管理Service实现类
 */
@Service
@Slf4j
public class EsGoodsServiceImpl implements EsGoodsService {
	@Autowired
	private EsGoodsDao goodsDao;

	@Autowired
	private EsGoodsRepository goodsRepository;

	@Autowired
	private ElasticsearchRestTemplate elasticsearchRestTemplate;

	@Override
	public EsGoods saveEsGoods(Long ESGoodsId) {
		EsGoods result = null;
		List<EsGoods> esGoodsList = goodsDao.listESGoods();
		if (esGoodsList.size() > 0) {
			EsGoods esGoods = esGoodsList.get(0);
			result = goodsRepository.save(esGoods);
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int batchSaveEsGoods() {
		List<EsGoods> esGoodsList = goodsDao.listESGoods();
		Iterable<EsGoods> esGoodsIterable = goodsRepository.saveAll(esGoodsList);
		Iterator<EsGoods> iterator = esGoodsIterable.iterator();
		int result = 0;
		while (iterator.hasNext()) {
			result++;
			iterator.next();
		}
		return result;
	}

	@Override
	public void removeEsGoodsById(Long EsGoodsId) {
		goodsRepository.deleteById(EsGoodsId);
	}

	@Override
	public void batchRemoveEsGoodsById(List<Long> EsGoodsIds) {
		if (!CollectionUtils.isEmpty(EsGoodsIds)) {
			List<EsGoods> esGoodsList = new LinkedList<>();
			for (Long EsGoodsId : EsGoodsIds) {
				EsGoods esGoods = new EsGoods();
				esGoods.setEsGoodsId(EsGoodsId);
				esGoodsList.add(esGoods);
			}
			goodsRepository.deleteAll(esGoodsList);
		}
	}

	@Override
	public Page<EsGoods> query(String keyword, Integer pageNum, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNum, pageSize);
		return goodsRepository.findByNameOrSubTitleOrKeywords(keyword, keyword, keyword, pageable);
	}

	@Override
	public Page<EsGoods> query(String keyword, Long brandId, Long categoryId, Integer pageNum, Integer pageSize,
			Integer sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<EsGoods> recommend(Long ESGoodsId, Integer pageNum, Integer pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EsGoodsRelatedInfo searchRelatedInfo(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @Override public Page<EsGoods> search(String keyword, Long brandId, Long
	 * productCategoryId, Integer pageNum, Integer pageSize, Integer sort) {
	 * Pageable pageable = PageRequest.of(pageNum, pageSize);
	 * NativeSearchQueryBuilder nativeSearchQueryBuilder = new
	 * NativeSearchQueryBuilder(); //分页
	 * nativeSearchQueryBuilder.withPageable(pageable); //过滤 if (brandId != null ||
	 * productCategoryId != null) { BoolQueryBuilder boolQueryBuilder =
	 * QueryBuilders.boolQuery(); if (brandId != null) {
	 * boolQueryBuilder.must(QueryBuilders.termQuery("brandId", brandId)); } if
	 * (productCategoryId != null) {
	 * boolQueryBuilder.must(QueryBuilders.termQuery("productCategoryId",
	 * productCategoryId)); } nativeSearchQueryBuilder.withFilter(boolQueryBuilder);
	 * } //搜索 if (StringUtils.isEmpty(keyword)) {
	 * nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery()); } else {
	 * List<FunctionScoreQueryBuilder.FilterFunctionBuilder> filterFunctionBuilders
	 * = new ArrayList<>(); filterFunctionBuilders.add(new
	 * FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery(
	 * "name", keyword), ScoreFunctionBuilders.weightFactorFunction(10)));
	 * filterFunctionBuilders.add(new
	 * FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery(
	 * "subTitle", keyword), ScoreFunctionBuilders.weightFactorFunction(5)));
	 * filterFunctionBuilders.add(new
	 * FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery(
	 * "keywords", keyword), ScoreFunctionBuilders.weightFactorFunction(2)));
	 * FunctionScoreQueryBuilder.FilterFunctionBuilder[] builders = new
	 * FunctionScoreQueryBuilder.FilterFunctionBuilder[filterFunctionBuilders.size()
	 * ]; filterFunctionBuilders.toArray(builders); FunctionScoreQueryBuilder
	 * functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(builders)
	 * .scoreMode(FunctionScoreQuery.ScoreMode.SUM) .setMinScore(2);
	 * nativeSearchQueryBuilder.withQuery(functionScoreQueryBuilder); } //排序
	 * if(sort==1){ //按新品从新到旧
	 * nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("id").order(
	 * SortOrder.DESC)); }else if(sort==2){ //按销量从高到低
	 * nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sale").order(
	 * SortOrder.DESC)); }else if(sort==3){ //按价格从低到高
	 * nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("price").order(
	 * SortOrder.ASC)); }else if(sort==4){ //按价格从高到低
	 * nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("price").order(
	 * SortOrder.DESC)); }else{ //按相关度
	 * nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.
	 * DESC)); }
	 * nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.
	 * DESC)); NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();
	 * log.debug("DSL:{}", searchQuery.getQuery().toString()); return
	 * goodsRepository.search(searchQuery); }
	 * 
	 * @Override public Page<EsGoods> recommend(Long EsGoodsId, Integer pageNum,
	 * Integer pageSize) { Pageable pageable = PageRequest.of(pageNum, pageSize);
	 * List<EsGoods> esGoodsList = goodsDao.listEsGoodsById(EsGoodsId); if
	 * (esGoodsList.size() > 0) { EsGoods esGoods = esGoodsList.get(0); String
	 * keyword = esGoods.getName(); Long brandId = esGoods.getBrandId(); Long
	 * productCategoryId = esProduct.getProductCategoryId(); //根据商品标题、品牌、分类进行搜索
	 * List<FunctionScoreQueryBuilder.FilterFunctionBuilder> filterFunctionBuilders
	 * = new ArrayList<>(); filterFunctionBuilders.add(new
	 * FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery(
	 * "name", keyword), ScoreFunctionBuilders.weightFactorFunction(8)));
	 * filterFunctionBuilders.add(new
	 * FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery(
	 * "subTitle", keyword), ScoreFunctionBuilders.weightFactorFunction(2)));
	 * filterFunctionBuilders.add(new
	 * FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery(
	 * "keywords", keyword), ScoreFunctionBuilders.weightFactorFunction(2)));
	 * filterFunctionBuilders.add(new
	 * FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery(
	 * "brandId", brandId), ScoreFunctionBuilders.weightFactorFunction(5)));
	 * filterFunctionBuilders.add(new
	 * FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery(
	 * "productCategoryId", productCategoryId),
	 * ScoreFunctionBuilders.weightFactorFunction(3)));
	 * FunctionScoreQueryBuilder.FilterFunctionBuilder[] builders = new
	 * FunctionScoreQueryBuilder.FilterFunctionBuilder[filterFunctionBuilders.size()
	 * ]; filterFunctionBuilders.toArray(builders); FunctionScoreQueryBuilder
	 * functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(builders)
	 * .scoreMode(FunctionScoreQuery.ScoreMode.SUM) .setMinScore(2); //用于过滤掉相同的商品
	 * BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
	 * boolQueryBuilder.mustNot(QueryBuilders.termQuery("EsGoodsId",EsGoodsId));
	 * //构建查询条件 NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
	 * builder.withQuery(functionScoreQueryBuilder);
	 * builder.withFilter(boolQueryBuilder); builder.withPageable(pageable);
	 * NativeSearchQuery searchQuery = builder.build(); log.debug("DSL:{}",
	 * searchQuery.getQuery().toString()); return
	 * goodsRepository.search(searchQuery); } return new PageImpl<>(null); }
	 * 
	 * @Override public EsGoodsRelatedInfo searchRelatedInfo(String keyword) {
	 * NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder(); //搜索条件
	 * if(StringUtils.isEmpty(keyword)){
	 * builder.withQuery(QueryBuilders.matchAllQuery()); }else{
	 * builder.withQuery(QueryBuilders.multiMatchQuery(keyword,"name","subTitle",
	 * "keywords")); } //聚合搜索品牌名称
	 * builder.addAggregation(AggregationBuilders.terms("brandNames").field(
	 * "brandName")); //集合搜索分类名称
	 * builder.addAggregation(AggregationBuilders.terms("productCategoryNames").
	 * field("productCategoryName")); //聚合搜索商品属性，去除type=1的属性
	 * AbstractAggregationBuilder aggregationBuilder =
	 * AggregationBuilders.nested("allAttrValues","attrValueList")
	 * .subAggregation(AggregationBuilders.filter("productAttrs",QueryBuilders.
	 * termQuery("attrValueList.type",1))
	 * .subAggregation(AggregationBuilders.terms("attrIds")
	 * .field("attrValueList.productAttributeId")
	 * .subAggregation(AggregationBuilders.terms("attrValues")
	 * .field("attrValueList.value"))
	 * .subAggregation(AggregationBuilders.terms("attrNames")
	 * .field("attrValueList.name")))); builder.addAggregation(aggregationBuilder);
	 * NativeSearchQuery searchQuery = builder.build(); return
	 * elasticsearchRestTemplate.query(searchQuery, response -> {
	 * LOGGER.info("DSL:{}",searchQuery.getQuery().toString()); return
	 * convertProductRelatedInfo(response); }); }
	 * 
	 *//**
		 * 将返回结果转换为对象
		 *//*
			 * private EsGoodsRelatedInfo convertProductRelatedInfo(SearchResponse response)
			 * { EsGoodsRelatedInfo productRelatedInfo = new EsGoodsRelatedInfo();
			 * Map<String, Aggregation> aggregationMap =
			 * response.getAggregations().getAsMap(); //设置品牌 Aggregation brandNames =
			 * aggregationMap.get("brandNames"); List<String> brandNameList = new
			 * ArrayList<>(); for(int i = 0; i<((Terms) brandNames).getBuckets().size();
			 * i++){ brandNameList.add(((Terms)
			 * brandNames).getBuckets().get(i).getKeyAsString()); }
			 * productRelatedInfo.setBrandNames(brandNameList); //设置分类 Aggregation
			 * productCategoryNames = aggregationMap.get("productCategoryNames");
			 * List<String> productCategoryNameList = new ArrayList<>(); for(int
			 * i=0;i<((Terms) productCategoryNames).getBuckets().size();i++){
			 * productCategoryNameList.add(((Terms)
			 * productCategoryNames).getBuckets().get(i).getKeyAsString()); }
			 * productRelatedInfo.setProductCategoryNames(productCategoryNameList); //设置参数
			 * Aggregation productAttrs = aggregationMap.get("allAttrValues");
			 * List<LongTerms.Bucket> attrIds = ((LongTerms) ((InternalFilter)
			 * ((InternalNested)
			 * productAttrs).getProperty("productAttrs")).getProperty("attrIds")).getBuckets
			 * (); List<EsGoodsRelatedInfo.GoodsAttr> attrList = new ArrayList<>(); for
			 * (Terms.Bucket attrId : attrIds) { EsGoodsRelatedInfo.GoodsAttr attr = new
			 * EsGoodsRelatedInfo.GoodsAttr(); attr.setAttrId((Long) attrId.getKey());
			 * List<String> attrValueList = new ArrayList<>(); List<StringTerms.Bucket>
			 * attrValues = ((StringTerms)
			 * attrId.getAggregations().get("attrValues")).getBuckets();
			 * List<StringTerms.Bucket> attrNames = ((StringTerms)
			 * attrId.getAggregations().get("attrNames")).getBuckets(); for (Terms.Bucket
			 * attrValue : attrValues) { attrValueList.add(attrValue.getKeyAsString()); }
			 * attr.setAttrValues(attrValueList); if(!CollectionUtils.isEmpty(attrNames)){
			 * String attrName = attrNames.get(0).getKeyAsString();
			 * attr.setAttrName(attrName); } attrList.add(attr); }
			 * productRelatedInfo.setProductAttrs(attrList); return productRelatedInfo; }
			 */
}
