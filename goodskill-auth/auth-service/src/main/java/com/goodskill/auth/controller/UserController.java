package com.goodskill.auth.controller;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.goodskill.auth.entity.User;
import com.goodskill.auth.pojo.vo.UserInfoVO;
import com.goodskill.auth.service.UserService;
import com.goodskill.common.core.info.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录测试
 */
@RestController
@Tag(name = "用户管理")
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 为用户增加角色
     */
    @PostMapping("/role/add")
    @Operation(summary = "为用户增加角色")
    public Result<String> addRole(@RequestParam("userId") int userId, @RequestParam("roleId") int roleId) {
        userService.addRole(userId, roleId);
        SaSession sessionByLoginId = StpUtil.getSessionByLoginId(userId);
        sessionByLoginId.delete("Role_List");
        return Result.ok();
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    public Result<?> getUser() {
        Object loginId = StpUtil.getLoginId();
        StpUtil.hasPermission("/test");
        StpUtil.hasRole("test");
        User info = userService.getUserInfoById(loginId.toString());
        List<String> permissionList = StpUtil.getPermissionList();
        List<String> roleList = StpUtil.getRoleList();

        UserInfoVO userInfoVO =  new UserInfoVO();
        userInfoVO.setUser(info);
        userInfoVO.setPermissions(permissionList);
        userInfoVO.setRoles(roleList);
        return Result.ok(userInfoVO);
    }

}
