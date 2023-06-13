package com.goodskill.web.shiro;

import com.goodskill.api.bo.UserBO;
import com.goodskill.api.service.UserAccountService;
import com.goodskill.api.service.UserAuthAccountService;
import com.goodskill.entity.Permission;
import com.goodskill.entity.Role;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.HashSet;
import java.util.Set;

/**
 * 通过第三方网站进行身份认证
 *
 * @author heng
 */
public class AuthUserRealm extends AuthorizingRealm {
    /**
     *  用户对应的角色信息与权限信息都保存在数据库中，通过UserService获取数据
     */
    @DubboReference
    private UserAccountService userService;

    @DubboReference
    private UserAuthAccountService userAuthAccountService;

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
        char[] charArray = (char[]) token.getCredentials();
        String authAccount = String.valueOf(charArray);
        String principal = (String) token.getPrincipal();
        UserBO user = userAuthAccountService.findByThirdAccount(authAccount, principal.split("-")[1]);
        if (user == null || (user.getAccount().equals(principal))) {
            // 用户名不存在抛出异常
            throw new UnknownAccountException();
        }
        String md5 = new SimpleHash("MD5", authAccount, ByteSource.Util.bytes(user.getAccount()), 2).toString();
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getAccount(),
                md5, ByteSource.Util.bytes(user.getAccount()), getName());
        return authenticationInfo;
    }

}
