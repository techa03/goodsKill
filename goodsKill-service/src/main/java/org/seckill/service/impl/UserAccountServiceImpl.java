package org.seckill.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.seckill.dao.UserMapper;
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
import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserAccountServiceImpl implements UserAccountService{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserMapper userDao;
	@Resource
	private JmsTemplate jmsTemplate;

	@Override
	public void register(User user) {
		try {
			user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
			userDao.insert(user);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new CommonException(null);
		}

	}

	@Override
	public void login(User user) throws SeckillException{
		int count = userDao.updateByPrimaryKeySelective(user);
		if (count != 1) {
			throw new SeckillException("login fail");
		}
	}

	@Override
	public void sendMsgForLogin(Destination destination,final User user) {
		jmsTemplate.send(destination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				Map<String,Object> userInfo=new HashMap<String,Object>();
				userInfo.put("userName",user.getAccount());
				userInfo.put("password",user.getPassword());
				JSONObject user=new JSONObject(userInfo);
				return session.createTextMessage(user.toString());
			}
		});
	}


	@Override
	public void onMessage(Message message) {
		TextMessage textMessage=(TextMessage)message;
		try {
			logger.info("接受的用户信息："+textMessage.getText());
			JSONObject userInfo=JSONObject.parseObject(textMessage.getText());
			if(!"".equals(userInfo.get("userName"))&&!"".equals(userInfo.get("password"))){
				// TODO
			}else{
				logger.info("字段不许为空");
			}
		} catch (JMSException e) {
			logger.error("",e);
		}
	}
}
