package com.goodskill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.api.service.UserAccountService;
import com.goodskill.common.exception.CommonException;
import com.goodskill.entity.*;
import com.goodskill.mp.dao.mapper.*;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author heng
 */
@DubboService
public class UserAccountServiceImpl extends ServiceImpl<UserMapper, User> implements UserAccountService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public void register(User user) {
        try {
            user.setPassword(user.getPassword());
            user.setUsername(user.getAccount());
            this.save(user);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new CommonException(null);
        }

    }

    @Override
    public Set<Role> findRoles(String account) {
        User entity = new User();
        entity.setAccount(account);
        List<User> users = baseMapper.selectList(new QueryWrapper(entity));
        Set<Role> roles = new HashSet<>();
        if (CollectionUtils.hasUniqueObject(users)) {
            User user = users.get(0);

            UserRole role = new UserRole();
            role.setUserId(user.getId());
            List<UserRole> userRoles = userRoleMapper.selectList(new QueryWrapper(role));
            for (UserRole userRole : userRoles) {
                roles.add(roleMapper.selectById(userRole.getRoleId()));
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
            RolePermission rolePermission1 = new RolePermission();
            rolePermission1.setRoleId(role.getRoleId());
            List<RolePermission> rolePermissions = rolePermissionMapper.selectList(new QueryWrapper<>(rolePermission1));
            for (RolePermission rolePermission : rolePermissions) {
                permission = permissionMapper.selectById(rolePermission.getPermissionId());
                permissions.add(permission);
            }
        }
        return permissions;
    }

    @Override
    public User findByUserAccount(String userAccount) {
        User user = new User();
        user.setAccount(userAccount);
        List<User> userList = baseMapper.selectList(new LambdaQueryWrapper<>(user));
        if (CollectionUtils.hasUniqueObject(userList)) {
            return userList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public User getUserInfoById(String userId) {
        User user = baseMapper.selectById(userId);
        user.setPassword(null);
        return user;
    }

    @Override
    public IPage<User> page(Page<User> page) {
        return super.page(page);
    }

    @Override
    public boolean removeById(int userId) {
        return super.removeById(userId);
    }

    @Override
    public boolean updateLastLoginTime(Integer id) {
        User user = new User();
        user.setId(id);
        user.setLastLoginTime(new Date());
        return this.updateById(user);
    }

}
