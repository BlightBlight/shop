package com.shop.coupon.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券实体类
 */
public class Coupon implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long couponId;	//优惠券Id
	private String couponDescription;	//优惠券描述
	private Long placeId;	//渠道Id
	private String placeName;	//渠道名称
	private Long rangeId;	//作用范围Id
	private String rangeName;	//作用范围名称
	private List<Long> effectIds;	//作用Id
	private List<Long> repulsionEffectIds;	//排斥作用Id
	private Long totalNumber;	//优惠券总数量
	private Long customerLimitNumber;	//用户限制总数量
	private LocalDateTime createTime;	//创建时间
	private LocalDateTime updateTime;	//修改时间
	private Integer onlineStatus;	//0,未上线；1,已上线
	private Integer deleteStatus;	//0,未删除；1,已删除
	
}
