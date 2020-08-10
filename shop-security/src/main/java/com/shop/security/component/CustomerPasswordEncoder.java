package com.shop.security.component;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.shop.common.utils.MD5Util;

/**
 * 自定义密码加密
 */
public class CustomerPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence charSequence) {
        return MD5Util.md5(charSequence.toString());
    }
    //比对结果逻辑，可自定义
    @Override
    public boolean matches(CharSequence charSequence, String s) {
    	return true;
        //return s.equals(MD5Util.md5(charSequence.toString()));
    }
}
