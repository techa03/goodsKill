package org.seckill.service.impl;

import org.seckill.dao.UserDao;
import org.seckill.entity.User;
import org.seckill.exception.CommonException;
import org.seckill.exception.SeckillException;
import org.seckill.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Service
public class UserAccountServiceImpl implements UserAccountService{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserDao userDao;
	@Resource
	private JmsTemplate jmsTemplate;

	@Override
	public void register(User user) {
		try {
			user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
			userDao.addUser(user);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new CommonException(null);
		}

	}

	@Override
	public void login(User user) {
		int count = userDao.selectUserByAccountAndPsw(user);
		if (count != 1) {
			throw new SeckillException("login fail");
		}
	}

	@Override
	public void sendMsgForLogin(Destination destination,final User user) {
		jmsTemplate.send(destination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(user.toString());
			}
		});
	}

}
