package com.shop.security.interceptor;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(RUNTIME)
public @interface AccessRefreshBuffered {
	//时间限制
	int seconds() default 60;
	//最大刷新次数
	int maxRefresh() default 5;
}
