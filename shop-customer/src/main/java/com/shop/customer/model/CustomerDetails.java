package com.shop.customer.model;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户详情封装
 */
public class CustomerDetails implements UserDetails {
	private static final long serialVersionUID = 1L;
	
	private Customer customer;
	private CustomerInfo customerInfo;
	
    public CustomerDetails(Customer customer, CustomerInfo customerInfo) {
        this.customer = customer;
        this.customerInfo = customerInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //返回当前用户的权限
        return Arrays.asList(new SimpleGrantedAuthority("TEST"));
    }
    
    @Override
    public String getPassword() {
        return customerInfo.getCustomerPwd();
    }

    @Override
    public String getUsername() {
        return String.valueOf(customer.getCustomermobileNum());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return (customer.getCustomerStatus() == 0) ? true : false;
    }

    public Customer getCustomer() {
        return customer;
    }

	@Override
	public String toString() {
		return "CustomerDetails [customer=" + customer + ", customerInfo=" + customerInfo + "]";
	}

    
}
