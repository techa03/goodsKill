package com.goodskill.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goodskill.auth.entity.Permission;
import com.goodskill.auth.entity.Role;
import com.goodskill.auth.entity.User;
import com.goodskill.auth.pojo.dto.UserDTO;

import java.util.Set;

/**
 * @author heng
 */
public interface UserService extends IService<User> {
    /**
     * 注册用户信息
     *
     * @param user 用户信息
     */
    void register(UserDTO user);

    /**
     * 获取当前用户所有角色
     *
     * @param username 用户名
     * @return 用户角色集合
     */
    Set<Role> findRoles(String username);

    /**
     * 获取用户权限集合
     *
     * @param username 用户名
     * @return 用户权限集合
     */
    Set<Permission> findPermissions(String username);

    /**
     * 获取用户账户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findByUserAccount(String username);

    /**
     * @param userId
     * @return
     */
    User getUserInfoById(String userId);

    IPage<User> page(Page<User> page);

    boolean removeById(int userId);

    boolean updateLastLoginTime(Integer id);

    /**
     * 校验密码
     *
     * @param password      数据库密码
     * @param passwordInput 用户输入密码
     * @return 是否一致
     */
    boolean checkPassword(String password, String passwordInput);
}
