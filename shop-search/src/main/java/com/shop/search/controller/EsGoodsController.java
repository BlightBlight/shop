package com.shop.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.shop.common.result.CommonPage;
import com.shop.common.result.CommonResult;
import com.shop.search.model.EsGoods;
import com.shop.search.model.EsGoodsRelatedInfo;
import com.shop.search.service.EsGoodsService;

import java.util.List;

/**
 * 搜索商品管理Controller
 */
@Controller
@RequestMapping("/EsGoods")
public class EsGoodsController {
    @Autowired
    private EsGoodsService esGoodsService;

    @PostMapping(value = "/batch")
    @ResponseBody
    public CommonResult<Integer> batchSaveEsGoods() {
        int effectRow = esGoodsService.batchSaveEsGoods();
        return CommonResult.success(effectRow);
    }

    @DeleteMapping(value = "/{EsGoodsId}")
    @ResponseBody
    public CommonResult<Object> removeEsGoodsById(@PathVariable long EsGoodsId) {
    	esGoodsService.removeEsGoodsById(EsGoodsId);
        return CommonResult.success("成功删除ES商品");
    }

    @DeleteMapping(value = "/batch")
    @ResponseBody
    public CommonResult<Object> batchRemoveEsGoodsById(@RequestParam("EsGoodsIds") List<Long> EsGoodsIds) {
    	esGoodsService.batchRemoveEsGoodsById(EsGoodsIds);
        return CommonResult.success(null);
    }

    @PostMapping(value = "/search/simple")
    @ResponseBody
    public CommonResult<CommonPage<EsGoods>> search(@RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                      @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        Page<EsGoods> esProductPage = esGoodsService.query(keyword, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(esProductPage));
    }

    @PostMapping(value = "/search")
    @ResponseBody
    public CommonResult<CommonPage<EsGoods>> search(@RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) Long brandId,
                                                      @RequestParam(required = false) Long productCategoryId,
                                                      @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                      @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                                                      @RequestParam(required = false, defaultValue = "0") Integer sort) {
        Page<EsGoods> esProductPage = esGoodsService.query(keyword, brandId, productCategoryId, pageNum, pageSize, sort);
        return CommonResult.success(CommonPage.restPage(esProductPage));
    }

    @PostMapping(value = "/recommend/{EsGoodsId}")
    @ResponseBody
    public CommonResult<CommonPage<EsGoods>> recommend(@PathVariable Long EsGoodsId,
                                                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                         @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        Page<EsGoods> esProductPage = esGoodsService.recommend(EsGoodsId, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(esProductPage));
    }

    @GetMapping(value = "/search/relate")
    @ResponseBody
    public CommonResult<EsGoodsRelatedInfo> searchRelatedInfo(@RequestParam(required = false) String keyword) {
        EsGoodsRelatedInfo goodsRelatedInfo = esGoodsService.searchRelatedInfo(keyword);
        return CommonResult.success(goodsRelatedInfo);
    }
}
