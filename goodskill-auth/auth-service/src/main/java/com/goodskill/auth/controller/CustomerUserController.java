package com.goodskill.auth.controller;

import com.goodskill.auth.pojo.vo.CustomerUserInfoVO;
import com.goodskill.auth.service.UserService;
import com.goodskill.core.info.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "C端用户")
@RestController
@RequestMapping("/customer")
public class CustomerUserController {

    @Resource
    private UserService userService;

    @GetMapping("/userinfo")
    @Operation(summary = "获取当前登录C端用户信息")
    public Result<CustomerUserInfoVO> getCurrentCustomerUserInfo() {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        CustomerUserInfoVO userInfo = userService.getCustomerUserInfo(userId);
        if (userInfo == null) {
            return Result.fail("用户不存在");
        }
        return Result.ok(userInfo);
    }

    private Integer getCurrentUserId() {
        try {
            Object loginId = cn.dev33.satoken.stp.StpUtil.getLoginId();
            if (loginId != null) {
                return Integer.parseInt(loginId.toString());
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}