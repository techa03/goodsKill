package org.seckill.service;

import org.seckill.entity.User;

public interface UserAccountService {
	void register(User user);

	void login(User user);
}
