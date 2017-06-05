package org.seckill.service;

import org.seckill.entity.User;
import org.seckill.exception.SeckillException;

import javax.jms.Destination;
import javax.jms.MessageListener;

public interface UserAccountService extends MessageListener{
	void register(User user);

	void login(User user) throws SeckillException;

	void sendMsgForLogin(Destination destination,final User user);
}
