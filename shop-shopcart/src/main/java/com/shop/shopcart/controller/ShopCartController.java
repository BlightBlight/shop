package com.shop.shopcart.controller;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.KeeperException.SessionExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.result.CommonResult;
import com.shop.common.utils.CookieUtil;
import com.shop.common.utils.JacksonUtil;
import com.shop.common.utils.LockUtils;
import com.shop.customer.model.Customer;
import com.shop.customer.service.CustomerService;
import com.shop.goods.controller.SKUGoodsController;
import com.shop.goods.controller.SPUGoodsController;
import com.shop.goods.model.SKUGoods;
import com.shop.shopcart.model.CartItem;
import com.shop.shopcart.model.ShopCart;
import com.shop.shopcart.service.ShopCartService;
import com.shop.shopcart.utils.ShopMap;
import com.shop.shopcart.vo.CheckCartItemVo;
import com.shop.shopcart.vo.SaveCartItemVo;
import com.shop.shopcart.vo.ShowOrderVo;
import com.shop.shopcart.vo.UpdateCartItemVo;

import lombok.extern.slf4j.Slf4j;

/**
 * 购物车Controller层
 */
@Controller
@RequestMapping("/shopCarts")
@Slf4j
public class ShopCartController {
	@Autowired
	SPUGoodsController spuGoodsController;
	
	@Autowired
	SKUGoodsController skuGoodsController;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	ShopCartService shopCartService;
	
    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.shopcarts}")
    private String lockScope;
    
	/**
	 * 新增商品到购物车中
	 * @throws Exception 
	 */
	@PostMapping()
	@ResponseBody
	public CommonResult<String> saveCartItem(@RequestBody @Valid SaveCartItemVo saveCartItemVo) throws Exception {
		Customer customer = customerService.getCurrentCustomer();
		Long customerId = customer.getCustomerId();
		log.debug("待新增购物车Vo：" + saveCartItemVo + "用户：" + customerId);
		
		InterProcessMutex lock = getShopCartLockById(customerId);
		try {
			lock.acquire();
			
			//验证货物数据
			@SuppressWarnings("rawtypes")
			CommonResult result = isGoodsExistsById(saveCartItemVo.getSkuId());
			if (result.getCode() != 200) {
				return result;
			}
			SKUGoods skuGoods = (SKUGoods) result.getData();
			if (saveCartItemVo.getNumber().longValue() > skuGoods.getStock().longValue()) {
				log.info("购买商品数量大于商品库存数量");
				return CommonResult.failed("购买商品数量大于商品库存数量");
			}
			
			//购物车中是否已有该商品
			result = isCartItemExistsFromShopCartBySKUId(customerId, saveCartItemVo.getShopId(), saveCartItemVo.getSkuId());
			if (result.getCode() == 200) {
				log.info("用户：" + customerId + "重复添加商品：" + saveCartItemVo.getSkuId() + "进购物车");
				return CommonResult.failed("重复添加商品进购物车");
			}
			shopCartService.saveCartItem(customerId, saveCartItemVo);
			log.info("成功加入购物车");				
			return CommonResult.success("成功加入购物车");
		} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new Exception(e);
		} finally {
			try {
				if (lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
				throw new Exception(e);
			}
		}
	}
	
	/**
	 * 删除购物车选项
	 * @throws Exception 
	 */
	@DeleteMapping(value = "/{skuId}")
	@ResponseBody
    public CommonResult<String> removeCartItem(@RequestBody String jsonObject) throws Exception {
		Long cartItemId = JacksonUtil.parseObject(jsonObject, Long.class);
		Customer customer = customerService.getCurrentCustomer();
		Long customerId = customer.getCustomerId();
    	log.debug("用户Id：" + customerId + "待删除购物车选项Id：" + cartItemId);
    	
		InterProcessMutex lock = getShopCartLockById(customerId);
		try {
			lock.acquire();
			
			@SuppressWarnings("rawtypes")
			CommonResult result = isCartItemExistsById(cartItemId, customerId);
			if (result.getCode() != 200) {
				return result;
			} else {
				CartItem cartItem = (CartItem) result.getData();
	    		// 是否已被删除
	    		if (cartItem.getDeleteStatus().intValue() == 2) {
	    			return CommonResult.failed("该购物车选项已被删除");
	    		}
			}
    		
			shopCartService.removeCartItemById(cartItemId, customerId);
			log.info("用户：" + customerId + "成功删除购物车选项：" + cartItemId);
			return CommonResult.success("成功删除购物车商品");
		} catch (SessionExpiredException e) {
    		throw new Exception(e);
    	} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new Exception(e);
		} finally {
			try {
				if (lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
				throw new Exception(e);
			}
		}
    }
	
	/**
	 * 批量删除购物车选项
	 */
    public CommonResult<String> batchRemoveSKUGoods(Long customerId, List<CartItem> skuGoodsIds) {
    	return CommonResult.internalServerFailed();
    }
    
	/**
	 * 修改购物车商品数量
	 * @throws Exception 
	 */
    @PutMapping("/number")
    @ResponseBody
    public CommonResult updateCartItemNumber(@RequestBody @Valid UpdateCartItemVo updateCartItemVo) throws Exception {
    	log.debug("待修改cartItemVo：" + updateCartItemVo.toString());
    	
    	Customer customer = customerService.getCurrentCustomer();
		Long customerId = customer.getCustomerId();
    	log.debug("用户：" + customerId + "修改购物车商品：" 
    		+ updateCartItemVo.getSkuId() + "数量为：" + updateCartItemVo.getNumber());
    	
    	InterProcessMutex lock = getShopCartLockById(customerId);
    	try {
    		lock.acquire();
    		
    		@SuppressWarnings("rawtypes")
			CommonResult result = isCartItemExistsById(updateCartItemVo.getCartItemId(), customerId);
    		if (result.getCode() != 200) {
    			return result;
    		} else {
    			// 若购物车中找到相应商品
    			CartItem cartItem = (CartItem) result.getData();
				result = validationCartItem(cartItem, updateCartItemVo.getShopId(), updateCartItemVo.getSkuId(), 
						updateCartItemVo.getNumber(), updateCartItemVo.getPrice(), updateCartItemVo.getSpecValueId());
				if (result.getCode() != 200) {
					return result;
				}
				// 如果成功且cartItem数据刷新
				if (result.getData() != null) {
					reFreshCartItemVo(updateCartItemVo, (CartItem) result.getData());
				}
				shopCartService.updateCartItemNumber(updateCartItemVo.getCartItemId(), updateCartItemVo.getNumber());
				log.info("用户：" + customerId + "成功修改购物车选项：" + updateCartItemVo.getCartItemId() + "数量为：" + updateCartItemVo.getNumber());
				return CommonResult.success(updateCartItemVo);
    		}
    	} catch (SessionExpiredException e) {
    		throw new Exception(e);
    	} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new Exception(e);
		} finally {
			try {
				if (lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
				throw new Exception(e);
			}
		}
    }
    
    /**
	 * 修改购物车规格值
     * @throws Exception 
	 */
    @PutMapping("/specValue")
    @ResponseBody
    public CommonResult updateCartItemSpecValue(@RequestBody @Valid UpdateCartItemVo updateCartItemVo) throws Exception {
    	log.debug("待修改cartItemVo：" + updateCartItemVo.toString());
		Customer customer = customerService.getCurrentCustomer();
		Long customerId = customer.getCustomerId();
    	log.debug("用户：" + customerId + "修改购物车商品：" 
    		+ updateCartItemVo.getSkuId() + "规格值为：" + updateCartItemVo.getSpecValueId());
    	
    	InterProcessMutex lock = getShopCartLockById(customerId);
    	try {
    		lock.acquire();
    		
	    	@SuppressWarnings("rawtypes")
			CommonResult result = isCartItemExistsById(updateCartItemVo.getCartItemId(), customerId);
	    	if (result.getCode() != 200) {
	    		return result;
	    	} else {
	    		// 若购物车中找到相应商品
	    		CartItem cartItem = (CartItem) result.getData();
	    		result = validationCartItemSpecValue(cartItem, updateCartItemVo.getSpecValueId(), updateCartItemVo.getSpecValueName());
				if (result.getCode() != 200) {
					return result;
				}
				// 如果成功且cartItem数据刷新
				if (result.getData() != null) {
					reFreshCartItemVo(updateCartItemVo, (CartItem) result.getData());
				}
				shopCartService.updateCartItemSpecValue(updateCartItemVo.getCartItemId(), updateCartItemVo.getSkuId(), updateCartItemVo.getPrice(), 
						updateCartItemVo.getSpecValueId(), updateCartItemVo.getSpecValueName());
				log.info("用户：" + customerId + "成功修改购物车商品：" 
							+ updateCartItemVo.getSkuId() + "规格值为：" + updateCartItemVo.getSpecValueId());
				return CommonResult.success(updateCartItemVo);
	    	}
    	} catch (SessionExpiredException e) {
    		throw new Exception(e);
    	} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new Exception(e);
		} finally {
			try {
				if (lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
				throw new Exception(e);
			}
		}
    }
    
	/**
	 * 根据cookie查找购物车
	 */
    @GetMapping()
    @ResponseBody
    public ShopCart getShopCartByCookie(HttpServletRequest request) {
		//cookie查找购物车
		Cookie cookie = CookieUtil.getCookie(request, "shopCart");
		String json = cookie.getValue();
		ShopCart shopCart = (ShopCart) JacksonUtil.parseObject(json, ShopCart.class);
		return shopCart;
    }
    
	/**
	 * 根据customerId查找购物车
	 * @param customerId 用户Id
	 */
    @GetMapping("/{customerId}")
    @ResponseBody
    public ShopCart getShopCartById(@PathVariable Long customerId) {
    	log.debug("根据customerId：" + customerId + "返回购物车");
    	ShopCart shopCart = shopCartService.getShopCartById(customerId);
		return shopCart;
    }
    
	/**
	 * 勾选商品
	 */
	/*
	 * 勾选商品时刷新商品数量、价格，返回总额，总商品数量
	 */
    @PostMapping("/check")
    @ResponseBody
    public CommonResult checkCartItemList(@RequestBody @Valid CheckCartItemVo checkCartItemVo) {
		Customer customer = customerService.getCurrentCustomer();
		Long customerId = customer.getCustomerId();
    	log.debug("购物车勾选商品：" + checkCartItemVo.toString() + "用户：" + customerId);
    	
    	for (UpdateCartItemVo updateCartItemVo : checkCartItemVo.getCheckCartItemList()) {
			@SuppressWarnings("rawtypes")
			CommonResult result = isCartItemExistsById(updateCartItemVo.getCartItemId(), customerId);
			if (result.getCode() != 200) {
				return result;
			} else {
				// 若购物车中找到相应商品
	    		CartItem cartItem = (CartItem) result.getData();
	    		result = validationCartItem(cartItem, updateCartItemVo.getShopId(), updateCartItemVo.getSkuId(), updateCartItemVo.getNumber(), 
	    				updateCartItemVo.getPrice(), updateCartItemVo.getSpecValueId());
	    		if (result.getCode() != 200) {
	    			return result;
	    		}
	    		if (result.getData() != null) {
		    		reFreshCartItemVo(updateCartItemVo, (CartItem) result.getData());
		    	}
			}
    	}
    	String json = JacksonUtil.toJSON(checkCartItemVo);
    	return CommonResult.success(json);
    }
    
	/**
	 * 下订单
	 * @param cartItemList
	 * @throws Exception 
	 */
    @PutMapping("order")
    @ResponseBody
    public CommonResult orderShopCart(List<CartItem> cartItemList) throws Exception {
    	if (cartItemList == null) {
    		log.debug("不明BUG");
    		return CommonResult.internalServerFailed();
    	}
    	Customer customer = customerService.getCurrentCustomer();
    	Long customerId = customer.getCustomerId();
    	
    	InterProcessMutex lock = getShopCartLockById(customerId);
    	try {
    		lock.acquire();
    		
        	List<CartItem> normalList = new LinkedList<CartItem>();
        	List<CartItem> abnormalList = new LinkedList<CartItem>();
	    	//对购物车中每一项进行检查，分开可付款订单与不可付款订单
	    	for (CartItem cartItem : cartItemList) {
	    		if (!customerId.equals(cartItem.getCustomerId())) {
	    			log.debug("页面传入用户参数与当前用户不符");
	    			return CommonResult.validateFailed("页面传入参数有误，请刷新页面重试");
	    		}
	    		//显示在页面中
	    		normalList.add(cartItem);
	    	}
	    	List<List<CartItem>> tempCartItemList = new LinkedList<List<CartItem>>();
	    	tempCartItemList.add(normalList);
	    	tempCartItemList.add(abnormalList);
	    	ShowOrderVo showOrderVo = new ShowOrderVo();
	    	showOrderVo.setCartItemList(tempCartItemList);
	    	String json = JacksonUtil.toJSON(showOrderVo);
	    	return CommonResult.success(json);
    	} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new Exception(e);
		} finally {
			try {
				if (lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
				throw new Exception(e);
			}
		}
    }
    
	/**
	 * 清空购物车
	 */
	/*
	 * 这里可以优化的点：当用户太多商品的时候，试下使用队列然后异步删除？
	 */
    @DeleteMapping()
    @ResponseBody
    public CommonResult<String> clear(@RequestParam("cartItemList") List<CartItem> cartItemList,
    		HttpServletRequest request, HttpServletResponse response) {
    	Customer customer = customerService.getCurrentCustomer();
    	Long customerId = customer.getCustomerId();
    	//调用批量删除
    	batchRemoveSKUGoods(customerId, cartItemList);
    	return CommonResult.success("成功清空购物车");
    }
    
	/**
	 * 判断SKU商品是否合法
	 */
    @SuppressWarnings("rawtypes")
    public CommonResult isGoodsExistsById(Long skuId) {
		CommonResult result = skuGoodsController.isSKUGoodsExistsById(skuId);
		if (result.getCode() != 200) {
			return result;
		}
		return CommonResult.success(result.getData());
    }
    
	/**
	 * 根据cartItemId查找购物车选项Id是否存在
	 * @param cartItemId 购物车选项Id
	 * @param customerId 用户Id
	 */
    @SuppressWarnings("rawtypes")
	public CommonResult isCartItemExistsById(Long cartItemId, Long customerId) {
    	CartItem cartItem = shopCartService.getCartItemById(cartItemId);
    	if (cartItem != null) {
    		// 是否是本人操作
    		if (!cartItem.getCustomerId().equals(customerId)) {
    			log.debug("系统customerId：" + customerId + "与cartItem的customerId：" + cartItem.getCustomerId() + "不符");
    			return CommonResult.internalServerFailed();
    		}
    		return CommonResult.success(cartItem);
    	}
		return CommonResult.validateFailed("购物车找不到该商品");
    }
    
	/**
	 * 根据skuId查找购物车SKU商品是否存在
	 * @param customerId 用户Id
	 * @param shopId 商铺Id
	 * @param skuId SKU商品Id
	 */
    @SuppressWarnings("rawtypes")
	public CommonResult isCartItemExistsFromShopCartBySKUId(Long customerId, Long shopId, Long skuId) {
    	ShopCart shopCart = shopCartService.getShopCartById(customerId);
		ShopMap<Long, LinkedList<Long>> shopCartList = shopCart.getShopCartList();
		if(shopCartList != null) {
			LinkedList<Long> cartItemIdList = shopCartList.get(shopId);
			if (cartItemIdList != null) {
				Iterator<Long> iterator = cartItemIdList.iterator();
				while (iterator.hasNext()) {
					Long cartItemId = iterator.next();
					CartItem cartItem = shopCartService.getCartItemById(cartItemId);
					//skuId相同，且未被购买，则存在
					if(cartItem.getSkuId().equals(skuId) && cartItem.getBuyStatus() == 1) {
						log.debug("根据页面传入skuId:" + skuId + "找到cartItem实体：" + cartItem.toString());
						return CommonResult.success(cartItem);
					}
				}
			}
		}
		return CommonResult.validateFailed("购物车找不到该商品");
    }
    
    /**
     *  根据规格值验证购物车项是否合法
     */
	@SuppressWarnings("rawtypes")
	public CommonResult validationCartItemSpecValue(CartItem cartItem, Long specValueId, String specValueName) {
		// 若specValueId相等，则以缓存（数据库）cartItem为准
		if (specValueId.equals(cartItem.getSpecValueId())) {
			log.debug("页面传入specValueId:" + specValueId + "与缓存（数据库）specValueId：" 
		+ cartItem.getSpecValueId() + "相同");
			return CommonResult.failed("修改规格值与原先规格值相同");
		}
		// 否则，根据specValueId查找相应SKUGoods
		else {
			CartItem cartItemVo = new CartItem();
			SKUGoods skuGoods = skuGoodsController.getSKUGoodsBySpecValueId(specValueId);
			if (skuGoods == null) {
				log.debug("页面传入specValueId:" + specValueId + "找不到相应SKU商品");
				return CommonResult.validateFailed("选择规格值有误，请刷新页面重试");
			}
			cartItemVo.setShopId(skuGoods.getShopId());
			cartItemVo.setSkuId(skuGoods.getSkuId());
			// 购买数量还是按照缓存（数据库）cartItem数量
			cartItemVo.setNumber(cartItem.getNumber());
			cartItemVo.setPrice(skuGoods.getPrice());
			cartItemVo.setSpecValueId(skuGoods.getSpecValueId());
			cartItemVo.setSpecValueName(specValueName);
			// 如果原购买数量超过新SKU商品库存数量，则返回400
			if (cartItem.getNumber().longValue() > skuGoods.getStock().longValue()) {
				log.debug("缓存（数据库）number：" + cartItem.getNumber().longValue() + "大于SKU商品number：" + skuGoods.getStock().longValue());
				return CommonResult.failed("购买数量超过库存，最多只能购买");
			}
			return CommonResult.success(cartItemVo);
		}
    }
    
	/**
	 * 判断购物车项是否合法（全部验证）
	 * @param cartItem 缓存（数据库）中购物车选项数据
	 */
	@SuppressWarnings("rawtypes")
	public CommonResult validationCartItem(CartItem cartItem, Long shopId, Long skuId, Long number, BigDecimal price, Long specValueId) {
		// 若specValueId不相等，则以缓存（数据库）cartItem为准
		CartItem cartItemVo = null;
		CommonResult result = null;
		if (!specValueId.equals(cartItem.getSpecValueId())) {
			log.debug("页面传入specValueId:" + specValueId + "与缓存（数据库）specValueId：" + cartItem.getSpecValueId() + "不符，" + "更新页面数据");
			cartItemVo = new CartItem();
			cartItemVo.setShopId(cartItem.getShopId());
			cartItemVo.setSkuId(cartItem.getSkuId());
			cartItemVo.setNumber(cartItem.getNumber());
			cartItemVo.setPrice(cartItem.getPrice());
			cartItemVo.setSpecValueId(cartItem.getSpecValueId());
			cartItemVo.setSpecValueName(cartItem.getSpecValueName());
			// 调用SKU接口验证缓存（数据库）cartItem与SKU数据是否相同
			result = skuGoodsController.validationSKUGoods(cartItemVo.getShopId(), cartItemVo.getSkuId(), cartItemVo.getNumber(), 
					cartItemVo.getPrice(), cartItemVo.getSpecValueId());
		}
		// 否则，继续验证其余参数是否合法
		else {
			if (!shopId.equals(cartItem.getShopId())) {
				log.debug("页面传入shopId:" + shopId + "与缓存（数据库）shopId：" + cartItem.getShopId() + "不符");
				return CommonResult.internalServerFailed("页面传入参数有误，请刷新页面重试");
			}
			
			if (!skuId.equals(cartItem.getSkuId())) {
				log.debug("页面传入skuId:" + skuId + "与缓存（数据库）skuId：" + cartItem.getSkuId() + "不符");
				return CommonResult.internalServerFailed("页面传入参数有误，请刷新页面重试");
			}
			
			if (price.compareTo(cartItem.getPrice()) != 0) {
				log.debug("页面传入price:" + price + "与缓存（数据库）price：" + cartItem.getPrice() + "不符");
				return CommonResult.internalServerFailed("页面传入参数有误，请刷新页面重试");
			}
			result = skuGoodsController.validationSKUGoods(shopId, skuId, number, price, specValueId);
		}
		if (result.getCode() == 400) {
			// 400说明SPU或SKU商品有误，将cartItem设置为不可购买，具体哪里有误可以通过细分状态继续划分
			//TODO:cartItem设为不可购买
			return result;
		} else if (result.getCode() == -1) {
			// -1说明SPU或SKU商品已更新，将cartItem设置为只读
			//TODO:cartItem设为只读
			return result;
		}
		return CommonResult.success(cartItemVo);
    }
	
	/**
	 * 刷新页面数据
	 * @param updateCartItemVo 待刷新页面
	 * @param cartItem 刷新数据
	 */
	public void reFreshCartItemVo(UpdateCartItemVo updateCartItemVo, CartItem cartItem) {
		updateCartItemVo.setShopId(cartItem.getShopId());
		updateCartItemVo.setSkuId(cartItem.getSkuId());
		updateCartItemVo.setNumber(cartItem.getNumber());
		updateCartItemVo.setPrice(cartItem.getPrice());
		updateCartItemVo.setSpecValueId(cartItem.getSpecValueId());
		updateCartItemVo.setSpecValueName(cartItem.getSpecValueName());
	}
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param customerId 用户Id
	 */
	public InterProcessMutex setShopCartLockById(Long customerId) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/shopCarts/getShopCart/" + customerId);
		if (LockUtils.getLock(lockScope, "/shopCarts/getShopCart/" + customerId) == null) {
			LockUtils.putLock(lockScope, "/shopCarts/getShopCart/" + customerId, lock);
		}
		return lock;
	}
	
	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * @param customerId 用户Id
	 */
	public InterProcessMutex getShopCartLockById(Long customerId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/shopCarts/getShopCart/" + customerId);
		if (lock == null) {
			lock = new InterProcessMutex(client, "/shopCarts/getShopCart/" + customerId);
			LockUtils.putLock(lockScope, "/shopCarts/getShopCart/" + customerId, lock);
		}
		return lock;
	}
}