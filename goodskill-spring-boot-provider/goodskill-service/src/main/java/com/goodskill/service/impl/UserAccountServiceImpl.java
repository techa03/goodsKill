package com.goodskill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.common.exception.CommonException;
import com.goodskill.service.entity.*;
import com.goodskill.service.inner.UserAccountService;
import com.goodskill.service.mapper.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author heng
 */
@Service
@Slf4j
public class UserAccountServiceImpl extends ServiceImpl<UserMapper, User> implements UserAccountService {
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserMapper baseMapper;

    @Override
    public void register(User user) {
        try {
            user.setPassword(user.getPassword());
            user.setUsername(user.getAccount());
            baseMapper.insert(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
