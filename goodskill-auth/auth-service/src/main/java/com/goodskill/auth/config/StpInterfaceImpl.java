package com.goodskill.auth.config;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaSessionCustomUtil;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.goodskill.auth.entity.Permission;
import com.goodskill.auth.entity.Role;
import com.goodskill.auth.entity.RolePermission;
import com.goodskill.auth.entity.UserRole;
import com.goodskill.auth.service.PermissionService;
import com.goodskill.auth.service.RolePermissionService;
import com.goodskill.auth.service.RoleService;
import com.goodskill.auth.service.UserRoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * sa-token自定义权限加载接口实现类
 */
@Component
public class StpInterfaceImpl implements StpInterface {
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private RolePermissionService rolePermissionService;
    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 1. 声明权限码集合
        List<String> permissionList = new ArrayList<>();

        // 2. 遍历角色列表，查询拥有的权限码
        for (String roleCode : getRoleList(loginId, loginType)) {
            SaSession roleSession = SaSessionCustomUtil.getSessionById("role-" + roleCode);
            List<String> list = roleSession.get("Permission_List", () -> {
                List<Integer> roleIdList = roleService.list(Wrappers.<Role>lambdaQuery().eq(Role::getRoleCode, roleCode))
                        .stream().map(Role::getRoleId).collect(Collectors.toList());
                List<Integer> permissionIdList = rolePermissionService.list(Wrappers.<RolePermission>lambdaQuery().in(RolePermission::getRoleId, roleIdList))
                        .stream().map(RolePermission::getPermissionId).distinct().collect(Collectors.toList());
                List<Permission> permissions = permissionService.list(Wrappers.<Permission>lambdaQuery().in(Permission::getPermissionId, permissionIdList));
                return permissions.stream().map(Permission::getPermissionCode).collect(Collectors.toList());
            });
            permissionList.addAll(list);
        }
        // 3. 返回权限码集合
        return permissionList;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        SaSession session = StpUtil.getSessionByLoginId(loginId);
        return session.get("Role_List", () -> {
            // 从数据库查询这个账号id拥有的角色列表
            List<UserRole> userRoleList = userRoleService.list(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, loginId));
            List<Integer> roleIds = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toList());
            return roleService.list(Wrappers.<Role>lambdaQuery().in(Role::getRoleId, roleIds)).stream().map(Role::getRoleCode)
                    .collect(Collectors.toList());
        });
    }

}
