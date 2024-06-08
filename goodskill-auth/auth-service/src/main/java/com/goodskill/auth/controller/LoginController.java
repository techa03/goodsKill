package com.goodskill.auth.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.goodskill.auth.entity.User;
import com.goodskill.auth.pojo.dto.UserDTO;
import com.goodskill.auth.service.UserService;
import com.goodskill.common.core.info.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 登录测试
 */
@RestController
@Tag(name = "登录测试")
public class LoginController {
    @Resource
    private UserService userService;

    /**
     * 测试登录
     *
     * @return
     */
    @RequestMapping("/isLogin")
    @Operation(summary = "测试登录")
    public Result<String> isLogin() {
        return Result.ok("是否登录：" + StpUtil.isLogin());
    }

    @RequestMapping("/tokenInfo")
    @Operation(summary = "获取token信息")
    public Result<SaTokenInfo> tokenInfo() {
        return Result.ok(StpUtil.getTokenInfo());
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<String> logout() {
        StpUtil.logout();
        return Result.ok();
    }

    @GetMapping("/user")
    @Operation(summary = "获取用户信息")
    public Result<?> getUser() {
        Object loginId = StpUtil.getLoginId();
        StpUtil.hasPermission("/test");
        StpUtil.hasRole("test");
        User info = userService.getUserInfoById(loginId.toString());
        return Result.ok(info);
    }

    @PostMapping("/register")
    @Operation(summary = "注册")
    public Result<String> register(@RequestBody UserDTO userDTO) {
        userService.register(userDTO);
        return Result.ok("注册成功");
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<String> login(@RequestBody UserDTO userDTO) {
        User userAccount = userService.findByUserAccount(userDTO.getUsername());
        if (userAccount != null) {
            // 校验账户与用户输入的密码是否一致
            boolean b = userService.checkPassword(userAccount.getPassword(), userDTO.getPassword());
            if (!b) {
                return Result.fail("用户名或密码错误");
            }
            StpUtil.login(userAccount.getId());
            return Result.ok("登录成功");
        }
        return Result.fail("用户名或密码错误");
    }

}
