package com.shop.coupon.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 作用效果实体类
 */
public class Effect implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long effectId;	//作用Id
	private String effectTypeId;	//作用类型Id
	private BigDecimal effectNumber;	//作用效果
	private long effectPriority;	//作用优先级
	private List<Long> repulsionEffectIds;	//排斥作用Id
	private long overlapTimes;	//叠加次数
	private LocalDateTime createTime;	//创建时间
	private LocalDateTime updateTIme;	//修改时间
	
	public long getEffectId() {
		return effectId;
	}
	public String getEffectTypeId() {
		return effectTypeId;
	}
	public BigDecimal getEffectNumber() {
		return effectNumber;
	}
	public long getEffectPriority() {
		return effectPriority;
	}
	public List<Long> getRepulsionEffectIds() {
		return repulsionEffectIds;
	}
	public long getOverlapTimes() {
		return overlapTimes;
	}
	public LocalDateTime getCreateTime() {
		return createTime;
	}
	public LocalDateTime getUpdateTIme() {
		return updateTIme;
	}
	public void setEffectId(long effectId) {
		this.effectId = effectId;
	}
	public void setEffectTypeId(String effectTypeId) {
		this.effectTypeId = effectTypeId;
	}
	public void setEffectNumber(BigDecimal effectNumber) {
		this.effectNumber = effectNumber;
	}
	public void setEffectPriority(long effectPriority) {
		this.effectPriority = effectPriority;
	}
	public void setRepulsionEffectIds(List<Long> repulsionEffectIds) {
		this.repulsionEffectIds = repulsionEffectIds;
	}
	public void setOverlapTimes(long overlapTimes) {
		this.overlapTimes = overlapTimes;
	}
	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
	public void setUpdateTIme(LocalDateTime updateTIme) {
		this.updateTIme = updateTIme;
	}
	
	@Override
	public String toString() {
		return "Effect [effectId=" + effectId + ", effectTypeId=" + effectTypeId + ", effectNumber=" + effectNumber
				+ ", effectPriority=" + effectPriority + ", repulsionEffectIds=" + repulsionEffectIds
				+ ", overlapTimes=" + overlapTimes + ", createTime=" + createTime + ", updateTIme=" + updateTIme + "]";
	}
}
