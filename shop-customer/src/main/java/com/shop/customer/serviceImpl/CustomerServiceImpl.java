package com.shop.customer.serviceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.common.exception.DAOException;
import com.shop.common.utils.LockUtils;
import com.shop.common.utils.SnowflakeIdUtil;
import com.shop.customer.dao.CustomerDao;
import com.shop.customer.model.Customer;
import com.shop.customer.model.CustomerDetails;
import com.shop.customer.model.CustomerInfo;
import com.shop.customer.service.CustomerCacheService;
import com.shop.customer.service.CustomerService;
import com.shop.customer.vo.RegisterVo;
import com.shop.customer.vo.UpdateCustomerVo;
import com.shop.security.utils.JwtTokenUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户Service接口实现类
 */
@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {
	@Autowired
	CustomerDao customerDao;
	
	@Autowired
	CustomerCacheService customerCacheService;
	
    @Autowired
    CuratorFramework client;
	
    @Value("${zookeeper.lockScope.customers}")
    private String lockScope;
    
	/*
	 * 这里可以改进，对Snowflake的部署应该要考虑到分布式，不过鉴于目前只有一台机器。。。
	 * 将SnowFlake的机器注册到ZooKeeper上当做持久顺序节点，节点ID为wordID
	 * 本地缓存节点ID，防止ZooKeeper出问题时，恰好机器也出问题需要重启，导致机器找不到ID无法启动
	 * 时钟问题不可避免，只能尽量缓解。
	 * 第一次注册将系统时间上传与其他节点做对比，若相差不大，正常，若相差大，注册失败
	 * 运行过程中，每隔一段时间上传系统时间对比，两种解决方案
	 * 1.无视，继续运行，不做回拨，等到半夜时再统一停机处理
	 * 2.返回不可服务，等待时钟追上
	 */
	SnowflakeIdUtil sf = new SnowflakeIdUtil(0,0);	
	
	private JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public boolean saveCustomer(RegisterVo registerVo) {
		// 用户基本信息
		Customer customer = new Customer();
		customer.setCustomerId(sf.nextId());
		customer.setCustomermobileNum(registerVo.getMobileNum());
		customer.setCustomernickName(registerVo.getNickName());
		customer.setCustomerStatus(new Integer(1));
		customer.setDeleteStatus(new Integer(1));
		log.debug("customer实体：" + customer.toString());
		/*
		 * 方案1.手机为主，用户使用手机注册即可，个人信息（包括账号、密码等）可填可不填
		 * 这种方法对用户体验好，但是需要注意以后要引导用户填个人信息。
		 * 方案2.传统，手机注册同时用户需要填写个人信息（账号、密码等）
		 * 密码使用MD5算法 通过前端加随机盐，最大程度加大黑客破解成本。
		 * 方案3.第三方注册，个人信息通过用户授权最大程度填好，其余信息选填
		 * 关于绑定手机号与否，看文档。
		 */
		// 原始密码
		//String formPwd = registerVo.getPwd();
		//String formSalt = registerVo.getSalt();
		// 经过了加盐后md5的密码
		//String dbPwd = MD5Util.formPassToDBPass(formPwd, formSalt);
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerLoginId", sf.nextId());
		map.put("customerId", customer.getCustomerId());
		map.put("identityType", "mobile");
		map.put("identifiler", customer.getCustomermobileNum());
		map.put("credential", null);
		log.debug("存入数据库customer_login实体：" + map.toString());
		int effectRow = 0;
		try {
			effectRow = customerDao.saveCustomerLogin(map); //插入用户登录表
			if (effectRow != 1) {
				throw new DAOException("新增customer_login表异常");
			}
			map.clear();
		} catch (DataAccessException e) {
			log.error("新增customer_login表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("新增customer_login表异常");
			throw new DAOException(e);
		}
		try {
			// 存入customer_info信息
			map.put("customerId", customer.getCustomerId());
			map.put("customermobileNum", customer.getCustomermobileNum());
			map.put("customerSalt", registerVo.getSalt());
			map.put("customernickName", customer.getCustomernickName());
			map.put("registerTime", registerVo.getRegisterTime());
			map.put("customerStatus", customer.getCustomerStatus());
			map.put("deleteStatus", customer.getDeleteStatus());
			log.debug("存入数据库customer_info实体：" + map.toString());
			effectRow = customerDao.saveCustomer(map);	//插入用户基本信息表
			if (effectRow != 1) {
				throw new DAOException("新增customer_info表异常");
			}
			log.debug("根据mobileNum：" + customer.getCustomermobileNum() + ",设置customer缓存" + customer.toString());
			customerCacheService.setCustomer(customer);
			log.debug("根据customerId：" + customer.getCustomerId() + ",设置mobileNum：" + customer.getCustomermobileNum() + "缓存" );
			customerCacheService.setCustomermobileNumById(customer.getCustomerId(), customer.getCustomermobileNum());
		} catch (DataAccessException e) {
			log.error("新增customer_info表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("新增customer_info表异常");
			throw new DAOException(e);
		}
		return true;
	}
	
	@Override
	public String generateAuthCode(String telephone) {
		return null;
	}
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int removeCustomerBymobileNum(String mobileNum) {
		/*
		 * 这里暂且逻辑先这样，等我其他弄好了，再进行延迟队列的操作
		 * 将删除操作放在延迟队列中，设置7天延迟时间，7天后延迟队列进行操作
		 * 若用户没取消删除操作，则数据库中相应标记状态为1，否则，检测到标记状态为0则消费队列不进行操作
		 */
		Customer customer = getCustomerBymobileNum(mobileNum);
		InterProcessMutex lock = getCustomerLockBymobileNum(mobileNum);
		int effectRow = 0;
		try {
			lock.acquire();
			effectRow = customerDao.delayRemoveCustomerBymobileNum(mobileNum, LocalDateTime.now());
			if (effectRow != 1) {
				throw new DAOException("修改customer_info表异常");
			}
			log.debug("根据mobileNum：" + mobileNum + ",删除customer缓存");
			customerCacheService.delCustomerBymobileNum(mobileNum);
			log.debug("根据customerId：" + customer.getCustomerId() + ",删除mobileNum：" + customer.getCustomermobileNum() + "缓存");
			customerCacheService.delCustomermobileNumById(customer.getCustomerId());
			log.debug("根据mobileNum：" + mobileNum + ",删除customerInfo缓存" );
			customerCacheService.delCustomerInfoBymobileNum(mobileNum);
		} catch (DataAccessException e) {
			log.error("修改customer_info表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改customer_info表异常");
			throw new DAOException(e);
		} catch (Exception e) {
			log.error(Thread.currentThread() + "不明异常");
			throw new DAOException(e);
		} finally {
			try {
				if (lock.isOwnedByCurrentThread()) {
					lock.release();
				}
			} catch (Exception e) {
				log.error(Thread.currentThread() + "释放锁出现BUG");
			}
		}
		return effectRow;
	}
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int cancelRemoveCustomerBymobileNum(String mobileNum) {
		int effectRow = 0;
		try {
			effectRow = customerDao.updateCancelDeleteCustomerBymobileNum(mobileNum);
			if (effectRow != 1) {
				throw new DAOException("修改customer_info表异常");
			}
		} catch (DataAccessException e) {
			log.error("修改customer_info表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改customer_info表异常");
			throw new DAOException(e);
		}
		
		return effectRow;
	}
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public int updateCustomer(UpdateCustomerVo updateCustomerVo) {
		int effectRow = 0;
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("customerId", updateCustomerVo.getCustomerId());
			map.put("customernickName", updateCustomerVo.getNickName());
			map.put("updateTime", updateCustomerVo.getUpdateTime());
			log.debug("存入数据库customer信息：" + map.toString() + ",修改时间：" + updateCustomerVo.getUpdateTime());
			effectRow = customerDao.updateCustomer(map);
			if (effectRow != 1) {
				throw new DAOException("修改customer_info表异常");
			}
			log.debug("根据mobileNum：" + updateCustomerVo.getMobileNum() + ",删除customerInfo缓存" );
			customerCacheService.delCustomerInfoBymobileNum(updateCustomerVo.getMobileNum());
		} catch (DataAccessException e) {
			log.error("修改customer_info表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改customer_info表异常");
			throw new DAOException(e);
		}
		return effectRow;
	}
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public void updatePasswordBymobileNum(String mobileNum, String password, String authCode) {
		
	}
	
	@Override
	public String getVerifyCodeBymobileNum(String mobileNum) {
		String verifyCode = customerCacheService.getAuthCodeBymobileNum(mobileNum);
		log.debug("mobileNum：" + mobileNum + "在" + LocalDateTime.now() + "时的验证码为：" + verifyCode);
		/*
		 * 没接入短信登录接口，没钱
		 */
		//return verifyCode;
		return "123456";
	}
	
	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public Customer getCustomerBymobileNum(String mobileNum) {
		/*
		 * TODO:担心在用户被删除后，遭受大量用户查询请求
		 * 这里需要有一个黑名单，这个黑名单记录被删除的用户Id
		 * 首先使用黑名单过滤删除用户Id，再使用布隆过滤器过滤恶意用户Id
		 * 黑名单与布隆过滤器每隔一段时间更新一次，比如每天晚上凌晨进行黑名单重置和布隆过滤器重新生成
		 * 这里布隆过滤器可以用Google那个开源框架，我懒得找了，也可以用Redis
		 */
		Customer customer = customerCacheService.getCustomerBymobileNum(mobileNum);
		if (customer == null) {
			InterProcessMutex lock = getCustomerLockBymobileNum(mobileNum);
			try {
				if (lock.acquire(5L, TimeUnit.SECONDS)) {
					// 一次性查找全部用户，无论删除与否
					customer = customerDao.getCustomerBymobileNum(mobileNum, new Integer(0));
					if (customer != null) {
						// 若用户已删除，则置空
						if (customer.getDeleteStatus().intValue() == 3) {
							log.debug("根据mobileNum：" + mobileNum + "查找出customer已删");
							return null;
						} else {
							log.debug("根据mobileNum：" + mobileNum + ",查找出customer为：" + customer.toString());
							log.debug("根据mobileNum：" + mobileNum + ",设置customer缓存" + customer.toString());
							customerCacheService.setCustomer(customer);
							return customer;
						}
					}
					return null;
				} else {
					customer = customerCacheService.getCustomerBymobileNum(mobileNum);
					if (customer == null) {
						//这里可以取得第几次重试吗？查下先
						log.debug("重试次数");
						throw new DAOException("customer查找重试失败");
					}
					return customer;
				}
			} catch (DataAccessException e) {
				log.info("查找customer_info表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error(Thread.currentThread() + "不明异常");
				throw new DAOException(e);
			} finally {
				try {
					if (lock.isOwnedByCurrentThread()) {
						lock.release();
					}
				} catch (Exception e) {
					log.error(Thread.currentThread() + "释放写锁出现BUG");
				}
			}
		}
		return customer;
	}
	
	@Override
	/*
	 * 注意，retry使用aspect增强，也就是代理，所以要注意方法内部调用不使用代理，所以无法使用retry
	 * 要不再另开一个接口调用retry方法，要不强行获取方法代理，使用retry（这里没试过，知道可以这么做而已）
	 */
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public Customer getCustomerById(Long customerId) {
		String mobileNum = customerCacheService.getCustomermobileNumById(customerId);
		if (mobileNum == null) {
			InterProcessMutex lock = getCustomerLockById(customerId);
			try {
				if (lock.acquire(5L, TimeUnit.SECONDS)) {
					/*
					 * 这里有两种处理方式
					 * 1.直接返回customer，这种情况下会导致脏数据
					 * 线程1修改customer，线程2根据Id查找customer
					 * 若redis缓存有效，则跳转getCustomerBymobileNum，分布锁保障安全
					 * 若redis缓存失效，则此时customer可能返回线程1修改前的customer，也可能返回修改后的customer
					 * 即：当redis中Id的缓存失效的前提下，且线程2读数据库比线程1写数据库慢时，线程2会返回修改后的customer
					 * 这种情况出现的概率较小，且除非对数据一致性有绝对严格要求，否则不太需要处理。
					 * 但是仔细想想，能透到数据库最多也就2-3个请求吧，剩下的一样要到getCustomerBymobileNum去找
					 * 为了这么2-3个请求的性能真的有必要直接返回customer？
					 * 2.不返回customer，继续去getCustomerBymobileNum
					 * 这种方法保障线程安全，稍微损耗一点点性能，保险起见就使用这种吧
					 */
					Customer customer = customerDao.getCustomerBymobileNum(mobileNum, new Integer(0));
					if (customer != null) {
						// 若用户已删除，则置空
						if (customer.getDeleteStatus().intValue() == 3) {
							log.debug("根据mobileNum：" + mobileNum + "查找出customer已删除");
							return null;
						} else {
							log.debug("根据Id：" + customerId + "查找出customer实体为：" + customer.toString());
							log.debug("根据Id：" + customerId + ",设置mobileNum：" + customer.getCustomermobileNum() + "缓存");
							customerCacheService.setCustomermobileNumById(customerId, customer.getCustomermobileNum());
							return getCustomerBymobileNum(customer.getCustomermobileNum());
						}
					}
					return null;
				} else {
					mobileNum = customerCacheService.getCustomermobileNumById(customerId);
					if (mobileNum == null) {
						//这里可以取得第几次重试吗？查下先
						log.debug("重试次数");
						throw new DAOException("mobileNum查找重试失败");
					}
					return getCustomerBymobileNum(mobileNum);
				}
			} catch (DataAccessException e) {
				log.info("查找customer_info表异常");
				throw new DAOException(e);
			} catch (Exception e) {
				log.error(Thread.currentThread() + "不明异常");
				throw new DAOException(e);
			} finally {
				try {
					if (lock.isOwnedByCurrentThread()) {
						lock.release();
					}
				} catch (Exception e) {
					log.error(Thread.currentThread() + "释放写锁出现BUG");
				}
			}
		}
		return getCustomerBymobileNum(mobileNum);
	}
	
	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public Customer getCustomerByName(String customerName) {
		//TODO:
		Customer customer = customerDao.getCustomerBymobileNum(customerName, new Integer(0));
		return customer;
	}
	
	/**
	 * 根据手机号查找用户是否存在
	 */
	@Override
	@Retryable(value = { DAOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public CustomerInfo getCustomerInfoBymobileNum(String mobileNum) {
		CustomerInfo customerInfo = customerCacheService.getCustomerInfoBymobileNum(mobileNum);
		if (customerInfo == null) {
			InterProcessMutex lock = getCustomerLockBymobileNum(mobileNum);
			try {
				if (lock.acquire(5L, TimeUnit.SECONDS)) {
					customerInfo = customerDao.getCustomerInfoBymobileNum(mobileNum, new Integer(0));
					if (customerInfo != null) {
						log.debug("根据mobileNum：" + mobileNum + "查找出customerInfo实体为：" + customerInfo.toString());
						log.debug("根据mobileNum：" + mobileNum + "设置customerInfo缓存" + customerInfo.toString());
						customerCacheService.setCustomerInfo(customerInfo);
						return customerInfo;
					}
					return null;
				} else {
					customerInfo = customerCacheService.getCustomerInfoBymobileNum(mobileNum);
					if (customerInfo == null) {
						//这里可以取得第几次重试吗？查下先
						log.debug("customer查找重试次数");
						throw new DAOException("customer查找重试失败");
					}
					return customerInfo;
				}
			} catch (DataAccessException e) {
				log.info("查找customer_info表异常：" + e);
				throw new DAOException(e);
			} catch (Exception e) {
				log.error(Thread.currentThread() + "不明异常");
				throw new DAOException(e);
			} finally {
				try {
					if (lock.isOwnedByCurrentThread()) {
						lock.release();
					}
				} catch (Exception e) {
					log.error(Thread.currentThread() + "释放锁出现BUG");
				}
			}
		}
		return customerInfo;
	}
	
	/*
	 * 1.网络真的差，重试3次都不行，你的问题
	 * 2.查找一个已删除的用户，该SPU还存有用户，且恰好该用户未被纳入黑名单且布隆过滤器过滤失败，请人工反馈
	 * 或者我查找逻辑更改为数据库查找所有用户，判断若用户被删除，则将其设为空返回，这样也行。
	 */
	@Recover
	public int recover(DAOException e) {
		log.error("customer查找重试3次失败！！！");
		throw new DAOException(e);
	}
	
	@Override
	@Transactional(rollbackFor = DAOException.class)
	public String loginBymobileNum(String mobileNum) {
		String token = null;
		// 将用户信息放入Security中
		try {
			UserDetails userDetails = loadCustomerByName(mobileNum);
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			token = jwtTokenUtil.generateToken(userDetails);
			log.debug("根据mobileNum：" + mobileNum + "生成的token：" + token);
		} catch (AuthenticationException e) {
            log.debug("登录异常");
            throw new DAOException(e);
        }
		
		/*
		 * 修改用户登录表
		 */
		try {
            int effectRow = customerDao.updateCustomerLogin(mobileNum, LocalDateTime.now());
            if (effectRow != 1) {
            	throw new DAOException("修改customer_login表异常");
            }
		} catch (DataAccessException e) {
			log.error("修改customer_login表异常");
			throw new DAOException(e);
		} catch (DAOException e) {
			log.error("修改customer_login表异常");
			throw new DAOException(e);
		}
		
		/*
		 * 将token放入缓存
		 * 将来分布式的时候这里要做好缓存写入失败的打算
		 * 可以使用RabbitMQ进行重发，重发n次后若还不成功则返回服务器内部错误提示，同时监测报警
		 * 好像不太好，这样受限于RabbitMQ，额，再想想吧，RabbitMQ集群？
		 */
		if (token != null) {
			log.debug("根据mobileNum：" + mobileNum + "设置token：" + token + "缓存");
			customerCacheService.setTokenBymobileNum(mobileNum, token);
		}
		return token;
	}
	
    @Override
    public UserDetails loadCustomerByName(String mobileNum) {
        Customer customer = getCustomerBymobileNum(mobileNum);
        CustomerInfo customerInfo = getCustomerInfoBymobileNum(mobileNum);
        if (customer != null) {
            return new CustomerDetails(customer, customerInfo);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }
	
	@Override
	public Customer getCurrentCustomer() {
		SecurityContext ctx = SecurityContextHolder.getContext();
		Authentication auth = ctx.getAuthentication();
		CustomerDetails customerDetails = (CustomerDetails) auth.getPrincipal();
		return customerDetails.getCustomer();
	}
	
	@Override
	public boolean logoutCustomerBymobileNum(String mobileNum) {
		Customer customer = getCurrentCustomer();
		//清理缓存
		customerCacheService.delCustomerBymobileNum(mobileNum);
		customerCacheService.delCustomermobileNumById(customer.getCustomerId());
		customerCacheService.delCustomerInfoBymobileNum(mobileNum);
		customerCacheService.delTokenBymobileNum(mobileNum);
		//清理Security
		SecurityContextHolder.getContext().setAuthentication(null);
		return true;
	}
	
	@Override
	public InterProcessMutex setCustomerLockBymobileNum(String mobileNum) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/customers/getCustomer/" + mobileNum);
		if (LockUtils.getLock(lockScope, "/customers/getCustomer/" + mobileNum) == null) {
			LockUtils.putLock(lockScope, "/customers/getCustomer/" + mobileNum, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getCustomerLockBymobileNum(String mobileNum) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/customers/getCustomer/" + mobileNum);
		if (lock == null) {
			lock =  new InterProcessMutex(client, "/customers/getCustomer/" + mobileNum);
			LockUtils.putLock(lockScope, "/customers/getCustomer/" + mobileNum, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex setCustomerLockById(Long customerId) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/customers/getCustomer/" + customerId);
		if (LockUtils.getLock(lockScope, "/customers/getCustomer/" + customerId) == null) {
			LockUtils.putLock(lockScope, "/customers/getCustomer/" + customerId, lock);
		}
		return lock;
	}

	@Override
	public InterProcessMutex getCustomerLockById(Long customerId) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/customers/getCustomer/" + customerId);
		if (lock == null) {
			lock =  new InterProcessMutex(client, "/customers/getCustomer/" + customerId);
			LockUtils.putLock(lockScope, "/customers/getCustomer/" + customerId, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex setCustomerInfoLockBymobileNum(String mobileNum) {
		InterProcessMutex lock =  new InterProcessMutex(client, "/customers/getCustomerInfo/" + mobileNum);
		if (LockUtils.getLock(lockScope, "/customers/getCustomerInfo/" + mobileNum) == null) {
			LockUtils.putLock(lockScope, "/customers/getCustomerInfo/" + mobileNum, lock);
		}
		return lock;
	}
	
	@Override
	public InterProcessMutex getCustomerInfoLockBymobileNum(String mobileNum) {
		InterProcessMutex lock = LockUtils.getLock(lockScope, "/customers/getCustomerInfo/" + mobileNum);
		if (lock == null) {
			lock =  new InterProcessMutex(client, "/customers/getCustomerInfo/" + mobileNum);
			LockUtils.putLock(lockScope, "/customers/getCustomerInfo/" + mobileNum, lock);
		}
		return lock;
	}
}
