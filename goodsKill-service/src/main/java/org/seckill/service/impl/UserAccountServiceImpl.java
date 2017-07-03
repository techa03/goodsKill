package org.seckill.service.impl;

import org.seckill.dao.UserMapper;
import org.seckill.entity.User;
import org.seckill.entity.UserExample;
import org.seckill.exception.CommonException;
import org.seckill.exception.SeckillException;
import org.seckill.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserMapper userMapper;
    @Resource
    private JmsTemplate jmsTemplate;

    @Override
    public void register(User user) {
        try {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
            userMapper.insert(user);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new CommonException(null);
        }

    }

    @Override
    public void login(User user) throws SeckillException {
        UserExample example = new UserExample();
        example.createCriteria().andAccountEqualTo(user.getAccount()).andPasswordEqualTo(user.getPassword());
        List<User> list = userMapper.selectByExample(example);
        if (list.size() != 1) {
            throw new SeckillException("login fail");
        }
    }

    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

}
