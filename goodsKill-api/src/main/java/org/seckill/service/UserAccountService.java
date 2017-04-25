package org.seckill.service;

import org.seckill.entity.User;

import javax.jms.Destination;
import javax.jms.MessageListener;

public interface UserAccountService extends MessageListener{
	void register(User user);

	void login(User user);

	void sendMsgForLogin(Destination destination,final User user);
}
