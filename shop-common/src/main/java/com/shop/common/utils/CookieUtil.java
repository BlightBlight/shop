package com.shop.common.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shop.common.exception.ServiceException;

public class CookieUtil {
	/**
	 * 创建cookie
	 * @param request
	 * @param response
	 * @param cookieName 为cookie设置的名称
	 * @param value cookie中携带的信息
	 * @param expiry cookie过期时间
	 */
	public static void saveCookies(HttpServletRequest request, HttpServletResponse response, String cookieName,
			String value, int expiry) {
		Cookie[] cookies = request.getCookies();
		Cookie cookie = null;
		if (cookies != null) {
			for (Cookie ck : cookies) {
				if (ck.getName().equals(cookieName)) {
					cookie = ck;
				}
			}
		}

		if (cookie == null) {
			cookie = new Cookie(cookieName, value);
			cookie.setDomain("localhost");
			cookie.setPath("/");
			cookie.setMaxAge(expiry);
			response.addCookie(cookie);
		}
	}
	
	/**
	 * 设置cookieMaxAge
	 * @param 
	 */
	public static void updateCookiesMaxAge(HttpServletRequest request, HttpServletResponse response, String cookieName,
			int expiry) {
		Cookie[] cookies = request.getCookies();
		Cookie cookie = null;
		if (cookies != null) {
			for (Cookie ck : cookies) {
				if (ck.getName().equals(cookieName)) {
					cookie = ck;
				}
			}
		}

		if (cookie == null) {
			throw new ServiceException("修改空cookie的过期时间");
		}
		cookie.setMaxAge(expiry);
	}
	
	/**
	 * 获取cookie
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
        Cookie cookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie ck : cookies) {
                String name = ck.getName();
                if (cookieName.equals(name)) {
                    cookie = ck;
                }
            }
        }
        return cookie;
	}
}
