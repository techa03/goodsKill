package com.goodskill.auth.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.goodskill.auth.entity.User;
import com.goodskill.auth.pojo.dto.UserDTO;
import com.goodskill.auth.service.UserService;
import com.goodskill.common.core.info.R;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 登录测试
 */
@RestController
public class LoginController {
    @Resource
    private UserService userService;

    /**
     * 测试登录
     *
     * @return
     */
    @RequestMapping("/isLogin")
    public R<String> isLogin() {
        return R.ok("是否登录：" + StpUtil.isLogin());
    }

    @RequestMapping("/tokenInfo")
    public R<SaTokenInfo> tokenInfo() {
        return R.ok(StpUtil.getTokenInfo());
    }

    @PostMapping("/logout")
    public R<String> logout() {
        StpUtil.logout();
        return R.ok();
    }

    @GetMapping("/user")
    public R<?> getUser() {
        Object loginId = StpUtil.getLoginId();
        StpUtil.hasPermission("/test");
        StpUtil.hasRole("test");
        User info = userService.getUserInfoById(loginId.toString());
        return R.ok(info);
    }

    @PostMapping("/register")
    public R<String> register(@RequestBody UserDTO userDTO) {
        userService.register(userDTO);
        return R.ok("注册成功");
    }

    @PostMapping("/login")
    public R<String> login(@RequestBody UserDTO userDTO) {
        User userAccount = userService.findByUserAccount(userDTO.getUsername());
        if (userAccount != null) {
            // 校验账户与用户输入的密码是否一致
            boolean b = userService.checkPassword(userAccount.getPassword(), userDTO.getPassword());
            if (!b) {
                return R.fail("用户名或密码错误");
            }
            StpUtil.login(userAccount.getId());
            return R.ok("登录成功");
        }
        return R.fail("用户名或密码错误");
    }

}
