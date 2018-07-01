package org.seckill.service.impl;

import org.seckill.entity.User;
import org.seckill.dao.UserMapper;
import org.seckill.entity.Permission;
import org.seckill.entity.Role;
import org.seckill.entity.UserExample;
import org.seckill.api.exception.CommonException;
import org.seckill.api.exception.SeckillException;
import org.seckill.api.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public Set<Role> findRoles(String username) {
        return new HashSet<>();
    }

    @Override
    public Set<Permission> findPermissions(String username) {
        return new HashSet<>();
    }

    @Override
    public User findByUserAccount(String userAccount) {
        UserExample example = new UserExample();
        example.createCriteria().andAccountEqualTo(userAccount);
        List<User> userList = userMapper.selectByExample(example);
        if(CollectionUtils.hasUniqueObject(userList)){
            return userList.get(0);
        }else{
            return null;
        }
    }

    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}
