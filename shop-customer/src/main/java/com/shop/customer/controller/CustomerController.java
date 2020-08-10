package com.shop.customer.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.result.CommonResult;
import com.shop.common.utils.LockUtils;
import com.shop.customer.model.Customer;
import com.shop.customer.service.CustomerService;
import com.shop.customer.vo.MobileLoginVo;
import com.shop.customer.vo.RegisterVo;
import com.shop.customer.vo.UpdateCustomerVo;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户控制层
 */
@RequestMapping("/customers")
@Controller
@Slf4j
public class CustomerController {
	@Autowired
	private CustomerService customerService;
	
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    
    @Value("${jwt.tokenHead}")
    private String tokenHead;
	
    @Value("${zookeeper.lockScope.customers}")
    private String lockScope;
    
    @Autowired
    CuratorFramework client;
	
	/**
	 * 新增用户
	 * 先手机校验是否本人，不通过，返回。
	 * @throws Exception 
	 */
	/*
	 * 关于第三方注册的问题写在文档里了
	 */
	@PostMapping()
	@ResponseBody
	public CommonResult<String> saveCustomer(@RequestBody @Valid RegisterVo registerVo) throws Exception {
		log.debug("待新增用户Vo实体：" + registerVo.toString());
		
		/*
		 * 这里还有另一种实现方法，就是界面中只有手机号获取了验证码后才能点登录按钮
		 * 这样就不用加锁了，严格保证一个手机号一个验证码，必须获取验证码才能登录
		 * 但是问题是用户体验会下降，比如说我登录输入了手机号，然后不小心点退出了，哪怕只是切屏了都不行
		 * 必须在同一个页面完成一整套流程才行。一旦检测到了切换屏幕，回来后还是一样要重新获取验证码。
		 * 所以选择锁住手机号，提高用户体验。
		 */
		/*
		 * 锁住手机号，防止同一手机号注册多个账号
		 * 这里可以继续优化一下，来个布隆过滤器或者HashMap之类的
		 * 先对手机号进行录入，判断是否存在手机号，再进行加锁操作
		 * 如果在分布式的情况下，可以使用分布式锁进行锁操作
		 */
		InterProcessMutex lock = getCustomerLockBymobileNum(registerVo.getMobileNum());
		try {
			lock.acquire();
			
			@SuppressWarnings("rawtypes")
			CommonResult result = isCustomerExists(registerVo.getMobileNum());
			if (result.getCode() == 200) {
				Customer customer = (Customer) result.getData();
				if (customer.getCustomerStatus() != 1) {
					log.info("该账号已被冻结，请使用人工服务" + registerVo.getMobileNum());
					return CommonResult.failed("该账号已被冻结，请使用人工服务");
				}
				log.info("该账号已存在，请勿重复注册");
				return CommonResult.failed("该账号已存在，请勿重复注册");
			}
			
			if (customerService.saveCustomer(registerVo)) {
				log.info("成功注册");
				return CommonResult.success("成功注册");
			}
		} catch (SessionExpiredException e) {
			/*
			 * TODO:这里出异常，证明分布锁上不了，可能是ZooKeeper服务器宕机，可能是网络抖动，可能是业务服务器宕机之类的
			 * ZooKeeper宕机，会通过重连机制连到其他ZooKeeper上，除非大范围宕机，否则不太会出问题
			 * 网络抖动，client与ZooKeeper连接不上，同样使用重连机制，但是有可能网络真的差，就连不上了
			 * 处理方法有很多，如，单纯的查找不需要处理，因为只要等待ZooKeeper重连，重新执行业务逻辑就好了
			 * 如果是像新增、修改等业务逻辑不幂等的操作，则需要进行相应处理
			 * ZooKeeper与client的连接放在session中，该session由ZooKeeper集群管理，若连接失败，有几种异常
			 * CONNECTIONLOSS（连接断开），不用管，由ZooKeeper重连处理
			 * SESSIONEXPIRED（Session过期），多次重连不上或业务端主动断开连接，该异常就是要处理的
			 * 当出现此异常后，可以直接打断业务逻辑，回滚事务，也可以依情况看是否需要回滚。
			 * 业务服务器宕机，不用处理。。。服务器都宕机了，肯定执行不下去了，看日志回滚咯
			 */
		}
		catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new Exception(e);
		} finally {
			try {
				if (lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
				//TODO:这里倒是不用担心，因为是临时节点，宕机了会自动释放，只是BUG还是要解决的
				throw new Exception(e);
			}
		}
		return CommonResult.internalServerFailed();
	}
	
	/**
	 * 根据手机号登录
	 * 先查询手机号码是否存在，若存在，则验证验证码与输入的是否相同
	 * 验证码存在缓存中，只要登录成功就删除（验证码没做，发短信要钱，没钱）
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	@PostMapping("/mobile")
	@ResponseBody
	public CommonResult loginBymobileNum(@RequestBody @Valid MobileLoginVo mobileLoginVo,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.debug("待登录手机号码： "+ mobileLoginVo.getMobileNum() + "验证码：" + mobileLoginVo.getVerifyCode());
		
		/*
		 * 这里是否需要校验呢？分情况
		 * 如果手机号在未注册的情况下无法获取验证码，则这里不需校验
		 * 但是这样现实吗？每当用户输入一个号码后，先经过JSP正则表达式验证格式
		 * 然后Ajax判断是否存在相应号码，这里可以用布隆过滤器进行过滤。那么，值吗？
		 * 手机号码，11位纯数字，比较难Hash吧！可能需要进行转换字符串，字符串再Hash
		 * 一条短信就几分钱，也许成本上不校验更好吧
		 * 如果手机号在未注册的情况下也可以获取验证码，则这里需要校验
		 */
		InterProcessMutex lock = getCustomerLockBymobileNum(mobileLoginVo.getMobileNum());
		try {
			lock.acquire();
			
			CommonResult result = isCustomerExists(mobileLoginVo.getMobileNum());
			if (result.getCode() != 200) {
				//TODO:用户手机号未注册时，自动注册
				return CommonResult.validateFailed("该手机号无法查询到用户");
			} else {
				Customer customer = (Customer) result.getData();
				if (customer.getCustomerStatus() != 1) {
					log.info("该账户已被冻结，请使用人工服务" + mobileLoginVo.getMobileNum());
					return CommonResult.failed("该账号已被冻结，请使用人工服务");
				}
			}
			String tempVerifyCode = customerService.getVerifyCodeBymobileNum(mobileLoginVo.getMobileNum());
			if (!tempVerifyCode.equals(mobileLoginVo.getVerifyCode())) {
				log.info("用户输入验证码：" + mobileLoginVo.getVerifyCode() + "与系统验证码：" + tempVerifyCode + "不符");
				return CommonResult.validateFailed("验证码不正确");
			}
			String token = customerService.loginBymobileNum(mobileLoginVo.getMobileNum());
			if (token == null) {
				log.info("用户token生成失败");
				return CommonResult.internalServerFailed();
			}
			Map<String, String> tokenMap = new HashMap<>();
			tokenMap.put("token", token);
			tokenMap.put("tokenHead", tokenHead);
			return CommonResult.success(tokenMap);
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
	 * 登出用户
	 */
	@SuppressWarnings("rawtypes")
	@PostMapping("/logout")
	@ResponseBody
	public CommonResult logoutCustomerBymobileNum(@RequestBody String mobileNum,
			HttpServletRequest request, HttpServletResponse response) {
		log.debug("在" + LocalDateTime.now() + "时间，" +  mobileNum + "用户尝试登出");
		Customer customer = customerService.getCurrentCustomer();
		if (!customer.getCustomermobileNum().equals(mobileNum)) {
			log.debug("页面手机号：" + mobileNum + "与Security保存用户手机号：" + customer.getCustomermobileNum() + "不符");
			return CommonResult.validateFailed("页面传入手机号有误");
		}
		
		CommonResult result = isCustomerExists(mobileNum);
		if (result.getCode() != 200) {
			log.info(result.getMessage());
			return result;
		}
		
		if (!customerService.logoutCustomerBymobileNum(mobileNum)) {
			log.error("在" + LocalDateTime.now() + "时间，" +  mobileNum + "用户登出异常，强制登出");
			//TODO:强制登出手段
			Cookie cookieHead = new Cookie("tokenHead", "");
			Cookie cookieToken = new Cookie("token", "");
			cookieHead.setMaxAge(0);
			cookieToken.setMaxAge(0);
			response.addCookie(cookieHead);
			response.addCookie(cookieToken);
		}
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("token") || cookie.getName().equals("tokenHead")) {
				cookie.setMaxAge(0);
				cookie.setPath("/");
				response.addCookie(cookie);
			}
		}
		return CommonResult.unauthorized("成功登出");
	}
	
	/**
	 * 删除用户
	 * 先手机校验是否本人，不通过，返回。
	 * 再将删除操作放入延迟队列中，暂定7天时间后，若用户不取消，则删除。
	 * 删除只是将用户状态标记为删除状态，并不直接删除用户。
	 * @throws Exception 
	 */
	@DeleteMapping("/{mobileNum}")
	@ResponseBody
	public CommonResult<String> deleteCustomerBymobileNum(@PathVariable("mobileNum") String mobileNum) throws Exception {
		log.debug("待删除用户：" + mobileNum);
		
		Customer customer = customerService.getCurrentCustomer();
		if (customer.getCustomermobileNum() != mobileNum) {
			log.debug("用户传入Id与Security保存用户不同");
			return CommonResult.validateFailed("用户传入Id有误");
		}
		
		InterProcessMutex lock = getCustomerLockBymobileNum(mobileNum);
		try {
			lock.acquire();
			
			customerService.removeCustomerBymobileNum(mobileNum);
			log.info("成功删除用户");
			return CommonResult.success("成功删除");
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
	 * 取消删除用户
	 * 先手机校验是否本人，不通过，返回。
	 * 取消删除只是将用户状态标记为正常状态
	 * @throws Exception 
	 */
	@PutMapping("/cancelDelete")
	@ResponseBody
	public CommonResult<String> cancelRemoveCustomer(@RequestParam("mobileNum") String mobileNum) throws Exception {
		log.debug("待取消删除用户：" + mobileNum);
		
		InterProcessMutex lock = getCustomerLockBymobileNum(mobileNum);
		try {
			lock.acquire();
			
			customerService.cancelRemoveCustomerBymobileNum(mobileNum);
			log.info("成功取消删除用户");
			return CommonResult.success("成功取消删除");
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
	 * 修改用户
	 * @throws Exception 
	 */
	@PutMapping()
	@ResponseBody
	public CommonResult<String> updateCustomer(@RequestBody @Valid UpdateCustomerVo updateCustomerVo) throws Exception {
		log.debug("待修改用户Vo实体" + updateCustomerVo.toString());
		Customer customer = customerService.getCurrentCustomer();
		if (!customer.getCustomerId().equals(updateCustomerVo.getCustomerId())) {
			log.debug("用户传入Id" + updateCustomerVo.getCustomerId() + "与Security保存用户Id" + customer.getCustomerId() + "不同");
			return CommonResult.validateFailed("用户传入Id有误");
		}
		InterProcessMutex lock = getCustomerLockBymobileNum(updateCustomerVo.getMobileNum());
		try {
			lock.acquire();
			customerService.updateCustomer(updateCustomerVo);
			log.info("成功修改用户");
			return CommonResult.success("成功修改用户");
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
	 * 根据手机号查找用户，用户操作，查找未删除用户
	 * @param mobileNum
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	/*
	 * 讲道理，我打算把查找操作都放到Elasticsearch中进行，这个只作为保险用，所以不需太多操作，直接查数据库就行。
	 * 但是用户放到Elasticsearch有用吗？感觉没用啊，又不需要模糊搜索之类的。。。吧？主要是电商平台，不是社交平台
	 * 所以Redis+数据库应该就能解决需求了
	 */
	@PostMapping("/{mobileNum}")
	@ResponseBody
	public CommonResult getCustomerBymobileNum(@PathVariable("mobileNum") String mobileNum) {
		log.debug("待查找用户手机号为：" + mobileNum);
		CommonResult result = isCustomerExists(mobileNum);
		if (result.getCode() != 200) {
			return result;
		}
		Customer customer = customerService.getCustomerBymobileNum(mobileNum);
		return CommonResult.success(customer);
	}
	
	@SuppressWarnings("rawtypes")
	public CommonResult isCustomerExists(String mobileNum) {
		Customer customer = customerService.getCustomerBymobileNum(mobileNum);
		if (customer == null) {
			log.debug("根据手机号：" + mobileNum + "查找不到相应用户");
			return CommonResult.validateFailed("根据手机号：" + mobileNum + "查找不到相应用户");
		}
		return CommonResult.success(customer);
	}
	
	@SuppressWarnings("rawtypes")
	public CommonResult validationCustomer(Long customerId, String mobileNum, String customernickName) {
		Customer customer = customerService.getCurrentCustomer();
		if (customer.getCustomerId().equals(customerId)) {
			log.debug("页面传入用户Id：" + customerId +  "与缓存(数据库)用户Id：" + customer.getCustomerId() + "不符");
			return CommonResult.validateFailed("页面传入参数有误");
		}
		
		if (!customer.getCustomermobileNum().equals(mobileNum)) {
			log.debug("页面传入用户手机号：" + mobileNum +  "与缓存(数据库)用户手机号：" + customer.getCustomermobileNum() + "不符");
			return CommonResult.validateFailed("页面传入参数有误");
		}
		
		if (!customer.getCustomernickName().equals(customernickName)) {
			log.debug("页面传入用户昵称：" + customernickName +  "与缓存(数据库)用户昵称：" + customer.getCustomernickName() + "不符");
			return CommonResult.validateFailed("页面传入参数有误");
		}
		return CommonResult.success(customer);
	}
	
	/**
	 * 生成一个新的分布锁，不管原先有无。
	 * 适用于需要对同一个锁上多个不同节点的情况。
	 * 假设锁A已有A1节点，则使用该方法会再增加一个A2节点
	 * @param skuName sku商品名称
	 */
	public InterProcessMutex setCustomerLockBymobileNum(String mobileNum) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/customers/getCustomer/" + mobileNum);
		if (LockUtils.getLock("customers", "/customers/getCustomer/" + mobileNum) == null) {
			LockUtils.putLock("customers", "/customers/getCustomer/" + mobileNum, lock);
		}
		return lock;
	}
	
	/**
	 * 获取一个分布锁，若已有，则取回，若无，则生成新的
	 * @param mobileNum 用户手机号
	 */
	public InterProcessMutex getCustomerLockBymobileNum(String mobileNum) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/customers/getCustomer/" + mobileNum);
		if (lock == null) {
			lock =  new InterProcessMutex(client, "/customers/getCustomer/" + mobileNum);
			LockUtils.putLock(lockScope, "/customers/getCustomer/" + mobileNum, lock);
		}
		return lock;
	}
}
