package com.goodskill.auth.controller;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodskill.auth.entity.User;
import com.goodskill.auth.pojo.vo.UserInfoVO;
import com.goodskill.auth.service.UserService;
import com.goodskill.core.info.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@Tag(name = "用户管理")
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private StpInterface stpInterface;
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 分页获取用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页获取用户列表")
    public Result<IPage<User>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        IPage<User> userPage = userService.page(pageParam, keyword);
        return Result.ok(userPage);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    public Result<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        return Result.ok(user);
    }

    /**
     * 创建新用户
     */
    @PostMapping
    @Operation(summary = "创建新用户")
    public Result<String> createUser(@RequestBody User user) {
        try {
            userService.createUser(user);
            return Result.ok("创建成功");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息")
    public Result<String> updateUser(@PathVariable Integer id, @RequestBody User user) {
        try {
            user.setId(id);
            userService.updateUser(user);
            return Result.ok("更新成功");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<String> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return Result.ok("删除成功");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除用户")
    public Result<String> batchDeleteUsers(@RequestBody List<Integer> ids) {
        try {
            userService.batchDeleteUsers(ids);
            return Result.ok("批量删除成功");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

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

    @GetMapping("/listUserPermission")
    @Operation(summary = "获取用户权限")
    public List<String> listUserPermission(String loginId, String loginType) {
        return stpInterface.getPermissionList(loginId, loginType);
    }

    @GetMapping("/listUserRole")
    @Operation(summary = "获取用户角色")
    public List<String> listUserRole(String loginId, String loginType) {
        return stpInterface.getRoleList(loginId, loginType);
    }

}
