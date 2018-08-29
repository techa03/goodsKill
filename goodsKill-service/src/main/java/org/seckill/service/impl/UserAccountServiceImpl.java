package org.seckill.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.seckill.api.exception.CommonException;
import org.seckill.api.exception.SeckillException;
import org.seckill.api.service.UserAccountService;
import org.seckill.dao.*;
import org.seckill.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class UserAccountServiceImpl extends AbstractServiceImpl<UserMapper, UserExample, User> implements UserAccountService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Resource
    private JmsTemplate jmsTemplate;

    @Override
    public void register(User user) {
        try {
            user.setPassword(user.getPassword());
            user.setUsername(user.getAccount());
            userMapper.insertSelective(user);
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
    public Set<Role> findRoles(String account) {
        UserExample example = new UserExample();
        example.createCriteria().andAccountEqualTo(account);
        List<User> users = userMapper.selectByExample(example);
        Set<Role> roles = new HashSet<>();
        if (CollectionUtils.hasUniqueObject(users)) {
            User user = users.get(0);
            UserRoleExample secUserRoleExample = new UserRoleExample();
            secUserRoleExample.createCriteria().andUserIdEqualTo(user.getId());
            List<UserRole> userRoles = userRoleMapper.selectByExample(secUserRoleExample);
            for (UserRole userRole : userRoles) {
                roles.add(roleMapper.selectByPrimaryKey(userRole.getRoleId()));
            }
        }
        return roles;
    }

    @Override
    public Set<Permission> findPermissions(String account) {
        Permission permission;
        Set<Role> roles = findRoles(account);
        Set<Permission> permissions = new HashSet<>();
        for (Iterator<Role> iterator = roles.iterator(); iterator.hasNext(); ) {
            Role role = iterator.next();
            RolePermissionExample rolePermissionExample = new RolePermissionExample();
            rolePermissionExample.createCriteria().andRoleIdEqualTo(role.getRoleId());
            List<RolePermission> rolePermissions = rolePermissionMapper.selectByExample(rolePermissionExample);
            for (RolePermission rolePermission : rolePermissions) {
                permission = permissionMapper.selectByPrimaryKey(rolePermission.getPermissionId());
                permissions.add(permission);
            }
        }
        return permissions;
    }

    @Override
    public User findByUserAccount(String userAccount) {
        UserExample example = new UserExample();
        example.createCriteria().andAccountEqualTo(userAccount);
        List<User> userList = userMapper.selectByExample(example);
        if (CollectionUtils.hasUniqueObject(userList)) {
            return userList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public PageInfo<User> getSeckillList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectByExample(null);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}
