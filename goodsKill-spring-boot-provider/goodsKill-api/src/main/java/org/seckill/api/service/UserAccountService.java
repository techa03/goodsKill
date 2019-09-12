package org.seckill.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.seckill.entity.Permission;
import org.seckill.entity.Role;
import org.seckill.entity.User;

import java.util.Set;


/**
 * @author heng
 */
public interface UserAccountService extends IService<User> {
    /**
     * 注册用户信息
     *
     * @param user 用户信息
     */
    void register(User user);

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

}
