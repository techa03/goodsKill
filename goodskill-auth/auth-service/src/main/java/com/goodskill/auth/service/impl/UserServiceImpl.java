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

import java.time.LocalDateTime;
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
        user.setLastLoginTime(LocalDateTime.now());
        return this.updateById(user);
    }

    @Override
    public boolean checkPassword(String password, String passwordInput) {
        return passwordEncoder.matches(passwordInput, password);
    }

    @Override
    public boolean addRole(int userId, int roleId) {
        // 校验角色id是否存在
        if (roleMapper.selectById(roleId) != null) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            return userRoleMapper.insert(userRole) > 0;
        } else {
            throw new RuntimeException("角色id不存在");
        }
    }

    @Override
    public IPage<User> page(Page<User> page, String keyword) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getAccount, keyword)
                    .or()
                    .like(User::getEmailAddr, keyword);
        }
        queryWrapper.orderByDesc(User::getCreateTime);
        return super.page(page, queryWrapper);
    }

    @Override
    public User getUserById(Integer id) {
        User user = baseMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public boolean createUser(User user) {
        // 检查用户名是否已存在
        User existUser = findByUserAccount(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setAccount(user.getUsername());
        return baseMapper.insert(user) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        User existUser = baseMapper.selectById(user.getId());
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }
        // 如果修改了用户名，检查是否重复
        if (!existUser.getUsername().equals(user.getUsername())) {
            User checkUser = findByUserAccount(user.getUsername());
            if (checkUser != null) {
                throw new RuntimeException("用户名已存在");
            }
        }
        // 不更新密码字段
        user.setPassword(null);
        user.setAccount(user.getUsername());
        return baseMapper.updateById(user) > 0;
    }

    @Override
    public boolean deleteUser(Integer id) {
        User existUser = baseMapper.selectById(id);
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public boolean batchDeleteUsers(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return baseMapper.deleteBatchIds(ids) == ids.size();
    }
}
