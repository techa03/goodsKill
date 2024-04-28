package com.goodskill.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.auth.entity.*;
import com.goodskill.auth.mapper.*;
import com.goodskill.auth.pojo.dto.UserDTO;
import com.goodskill.auth.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
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
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public void register(UserDTO userDTO) {
        User user = new User();
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setUsername(userDTO.getUsername());
        user.setAccount(userDTO.getUsername());
        baseMapper.insert(user);
    }

    @Override
    public Set<Role> findRoles(String account) {
        User entity = new User();
        entity.setAccount(account);
        List<User> users = baseMapper.selectList(new QueryWrapper(entity));
        Set<Role> roles = new HashSet<>();
        if (CollectionUtils.hasUniqueObject(users)) {
            User user = users.getFirst();

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
        for (Role role : roles) {
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
            return userList.getFirst();
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

    @Override
    public boolean checkPassword(String password, String passwordInput) {
        return passwordEncoder.matches(passwordInput, password);
    }
}
