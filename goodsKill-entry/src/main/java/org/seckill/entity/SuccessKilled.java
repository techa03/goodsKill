package org.seckill.entity;

import java.io.Serializable;
import java.util.Date;

public class SuccessKilled implements Serializable{
	private static final long serialVersionUID = 1L;
	private long seckilled;
	private long userPhone;
	private short state;
	private Date createTime;
	private Seckill seckill;

	public Seckill getSeckill() {
		return seckill;
	}

	public void setSeckill(Seckill seckill) {
		this.seckill = seckill;
	}

	public long getSeckilled() {
		return seckilled;
	}

	public void setSeckilled(long seckilled) {
		this.seckilled = seckilled;
	}

	public long getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(long userPhone) {
		this.userPhone = userPhone;
	}

	public short getState() {
		return state;
	}

	public void setState(short state) {
		this.state = state;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "SuccessKilled [seckilled=" + seckilled + ", userPhone=" + userPhone + ", state=" + state
				+ ", createTime=" + createTime + "]";
	}

}
