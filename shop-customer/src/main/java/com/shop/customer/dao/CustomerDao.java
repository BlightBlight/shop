package com.shop.customer.dao;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shop.customer.model.Customer;
import com.shop.customer.model.CustomerInfo;

@Mapper
public interface CustomerDao {
	/**
	 * 新增用户基本信息
	 */
	@Insert("insert into customer_info(customer_id, customer_mobileNum, "
			+ "customer_salt, customer_nickName, register_time, customer_status, delete_status) "
			+ "values(#{customerId}, #{customermobileNum}, #{customerSalt}, "
			+ "#{customernickName}, #{registerTime}, #{customerStatus}, #{deleteStatus})")
	public int saveCustomer(Map<String, Object> map);
	
	/**
	 * 新增用户登录信息(不保存密码凭证)
	 */
	@Insert("insert into customer_login(customer_login_id, customer_id, customer_identity_type, "
			+ "customer_identifiler, customer_credential, delete_status) "
			+ "values(#{customerLoginId}, #{customerId}, #{identityType}, #{identifiler}, #{credential}, 0)")
	public int saveCustomerLogin(Map<String, Object> map);
	
	/**
	 * 新增用户登录信息(保存密码凭证)
	 * @param customerLoginId 用户登录表Id
	 * @param customerId 用户Id
	 * @param identityType 登录类型
	 * @param identifiler 登录凭证
	 * @param credential 密码凭证
	 */
	@Insert("insert into customer_login(customer_login_id, customer_id, customer_identity_type, "
			+ "customer_identifiler, customer_credential) "
			+ "values(#{customerLoginId}, #{customerId}, #{identityType}, #{customermobileNum}, #{customerCredential})")
	public int saveCustomerLoginCredential(@Param("customerLoginId")Long customerLoginId, @Param("customerId") Long customerId, 
			@Param("identityType") String identityType, @Param("identifiler") String identifiler, 
			@Param("credential") String credential);
	
	/**
	 * 延迟删除用户
	 * @param mobileNum 用户手机号
	 * @param deleteTime 删除时间
	 */
	@Update("update customer_info "
			+ "set delete_status = 2, "
			+ "delete_time = #{deleteTime} "
			+ "where customer_mobileNum = #{mobileNum} ")
	public int delayRemoveCustomerBymobileNum(@Param("mobileNum") String mobileNum, @Param("deleteTime") LocalDateTime deleteTime);
	
	/**
	 * 取消删除用户
	 * @param mobileNum 用户手机号
	 */
	@Update("update customer_info "
			+ "set delete_status = 1, "
			+ "delete_time = null "
			+ "where customer_mobileNum = #{mobileNum} ")
	public int updateCancelDeleteCustomerBymobileNum(@Param("mobileNum") String mobileNum);
	
	/**
	 * 真正删除用户
	 * @param mobileNum 用户手机号
	 */
	@Update("update customer_info "
			+ "set delete_status = 3 "
			+ "where customer_mobileNum = #{mobileNum} ")
	public int removeCustomerBymobileNum(@Param("mobileNum") String mobileNum);
	
	/**
	 * 修改用户
	 */
	@Update({"<script>"
			+ "update customer_info set "
			+ "<trim suffixOverrides=\",\">"
			+ "<if test=\"customernickName != null and customernickName != '' \">"
			+ "customer_nickName = #{customernickName}, "
			+ "</if>"
			+ "<if test=\"updateTime != null\">"
			+ "update_time = #{updateTime}, "
			+ "</if>"
			+ "</trim>"
			+ "where customer_id = #{customerId} "
			+ "and delete_status != 3 "
			+ "</script>"})
	public int updateCustomer(Map<String, Object> map);
	
	/**
	 * 登录后修改用户
	 * @param mobileNum 用户手机号
	 * @param loginTime 登录时间
	 */
	@Update({"<script>"
			+ "update customer_login set "
			+ "<trim suffixOverrides=\",\">"
			+ "<if test=\"loginTime != null \">"
			+ "login_time = #{loginTime}, "
			+ "</if>"
			+ "</trim>"
			+ "where customer_identifiler = #{customerIdentifier} "
			+ "and delete_status != 3"
			+ "</script>"})
	public int updateCustomerLogin(@Param("customerIdentifier") String customerIdentifier, @Param("loginTime") LocalDateTime loginTime);
	
	/**
	 * 根据手机号查询用户
	 * @param mobileNum 用户手机号
	 * @param deleteStatus 0,全部；1,未删除；2,删除中；3,已删除
	 */
	@Select({"<script>"
			+ "select customer_id, customer_mobileNum, customer_nickName, customer_status, delete_status from customer_info "
			+ "where customer_mobileNum = #{mobileNum} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus}"
			+ "</when>"
			+ "</script>"})
	public Customer getCustomerBymobileNum(@Param("mobileNum") String mobileNum, @Param("deleteStatus") Integer deleteStatus);
	
	/**
	 * 根据Id查询用户
	 * @param customerId 用户Id
	 * @param deleteStatus 0,全部；1,未删除；2,删除中；3,已删除
	 */
	@Select({"<script>"
			+ "select customer_id, customer_mobileNum, customer_nickName, customer_status, delete_status from customer_info "
			+ "where customer_id = #{customerId} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public Customer getCustomerById(@Param("customerId") Long customerId, @Param("deleteStatus") Integer deleteStatus);
	
	/**
	 * 根据手机号查询用户信息
	 * @param mobileNum 用户手机号
	 * @param deleteStatus 0,全部；1,未删除；2,删除中；3,已删除
	 */
	@Select({"<script>"
			+ "select c.customer_id, c.customer_mobileNum, c.customer_pwd, c.customer_salt, c.register_time, l.login_time, c.update_time, "
			+ "c.delete_time from customer_info c, customer_login l "
			+ "where c.customer_id = l.customer_id "
			+ "and customer_mobileNum = #{mobileNum} "
			+ "<when test = \"deleteStatus != null and deleteStatus != 0\">"
			+ "and delete_status = #{deleteStatus} "
			+ "</when>"
			+ "</script>"})
	public CustomerInfo getCustomerInfoBymobileNum(@Param("mobileNum") String mobileNum, @Param("deleteStatus") Integer deleteStatus);
}
