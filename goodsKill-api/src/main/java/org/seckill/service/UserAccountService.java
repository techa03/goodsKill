package org.seckill.service;

import org.seckill.entity.User;
import org.seckill.exception.SeckillException;

public interface UserAccountService{
	void register(User user);

	void login(User user) throws SeckillException;

}
