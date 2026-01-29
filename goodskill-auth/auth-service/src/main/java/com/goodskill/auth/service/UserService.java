package com.goodskill.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goodskill.auth.entity.Permission;
import com.goodskill.auth.entity.Role;
import com.goodskill.auth.entity.User;
import com.goodskill.auth.pojo.dto.UserDTO;

import java.util.List;
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

    /**
     * 为用户添加角色
     *
     * @param userId 用户id
     * @param roleId 角色id
     */
    boolean addRole(int userId, int roleId);

    /**
     * 分页查询用户列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    IPage<User> page(Page<User> page, String keyword);

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Integer id);

    /**
     * 创建新用户
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean createUser(User user);

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Integer id);

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 是否成功
     */
    boolean batchDeleteUsers(List<Integer> ids);
}
