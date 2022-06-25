package com.goodskill.web.shiro;

import com.goodskill.api.service.UserAccountService;
import com.goodskill.entity.Permission;
import com.goodskill.entity.Role;
import com.goodskill.entity.User;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.HashSet;
import java.util.Set;

/**
 * 通过用户名密码鉴权
 *
 * @author heng
 */
public class UserRealm extends AuthorizingRealm {
    /**
     *  用户对应的角色信息与权限信息都保存在数据库中，通过UserService获取数据
     */
    @DubboReference
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
        // 登录成功刷新最近登录时间
        userService.updateLastLoginTime(user.getId());
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getAccount(),
                user.getPassword(), ByteSource.Util.bytes(user.getAccount()), getName());
        return authenticationInfo;
    }

}
