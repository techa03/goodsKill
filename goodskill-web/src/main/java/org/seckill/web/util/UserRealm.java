package org.seckill.web.util;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.seckill.api.service.UserAccountService;
import org.seckill.entity.Permission;
import org.seckill.entity.Role;
import org.seckill.entity.User;

import java.util.HashSet;
import java.util.Set;

/**
 * @author heng
 */
public class UserRealm extends AuthorizingRealm {
    /**
     *  用户对应的角色信息与权限信息都保存在数据库中，通过UserService获取数据
     */
    @Reference
    private UserAccountService userService;

    /**
     * 提供用户信息返回权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String account = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 根据用户名查询当前用户拥有的角色
        Set<Role> roles = userService.findRoles(account);
        Set<String> roleNames = new HashSet<>();
        for (Role role : roles) {
            roleNames.add(role.getRoleName());
        }
        // 将角色名称提供给info
        authorizationInfo.setRoles(roleNames);
        // 根据用户名查询当前用户权限
        Set<Permission> permissions = userService.findPermissions(account);
        Set<String> permissionNames = new HashSet<>();
        for (Permission permission : permissions) {
            permissionNames.add(permission.getPermissionName());
        }
        // 将权限名称提供给info
        authorizationInfo.setStringPermissions(permissionNames);

        return authorizationInfo;
    }

    /**
     * 提供账户信息返回认证信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userAccount = (String) token.getPrincipal();
        User user = userService.findByUserAccount(userAccount);
        if (user == null) {
            // 用户名不存在抛出异常
            throw new UnknownAccountException();
        }
        if ("1".equals(user.getLocked())) {
            // 用户被管理员锁定抛出异常
            throw new LockedAccountException();
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getAccount(),
                user.getPassword(), ByteSource.Util.bytes(user.getAccount()), getName());
        return authenticationInfo;
    }

}
