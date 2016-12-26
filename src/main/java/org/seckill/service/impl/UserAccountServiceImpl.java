package org.seckill.service.impl;

import org.seckill.dao.UserDao;
import org.seckill.entity.User;
import org.seckill.exception.HengException;
import org.seckill.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserAccountServiceImpl implements UserAccountService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserDao userDao;

	@Override
	public void register(User user) {
		try {
			user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
			userDao.addUser(user);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new HengException(null);
		}

	}

}
