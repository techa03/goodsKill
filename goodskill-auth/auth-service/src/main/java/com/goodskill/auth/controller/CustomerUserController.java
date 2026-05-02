package com.goodskill.auth.controller;

import com.goodskill.auth.pojo.dto.CustomerUserInfoUpdateDTO;
import com.goodskill.auth.pojo.vo.CustomerUserInfoVO;
import com.goodskill.auth.service.UserService;
import com.goodskill.core.info.Result;
import com.goodskill.core.util.UserInfoUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.commons.lang.StringUtils;
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
        String userId = UserInfoUtil.getUserId();
        if (StringUtils.isBlank(userId)) {
            return Result.fail("用户未登录");
        }
        CustomerUserInfoVO userInfo = userService.getCustomerUserInfo(userId);
        if (userInfo == null) {
            return Result.fail("用户不存在");
        }
        return Result.ok(userInfo);
    }

    @PutMapping("/userinfo")
    @Operation(summary = "更新当前登录C端用户信息")
    public Result<CustomerUserInfoVO> updateCurrentCustomerUserInfo(@Valid @RequestBody CustomerUserInfoUpdateDTO updateDTO) {
        String userId = UserInfoUtil.getUserId();
        if (StringUtils.isBlank(userId)) {
            return Result.fail("用户未登录");
        }
        CustomerUserInfoVO updatedUserInfo = userService.updateCustomerUserInfo(userId, updateDTO);
        if (updatedUserInfo == null) {
            return Result.fail("用户不存在");
        }
        return Result.ok(updatedUserInfo);
    }

    @PutMapping("/avatar")
    @Operation(summary = "更新当前登录C端用户头像")
    public Result<CustomerUserInfoVO> updateCurrentCustomerUserAvatar(@RequestParam("avatarUrl") String avatarUrl) {
        String userId = UserInfoUtil.getUserId();
        if (StringUtils.isBlank(userId)) {
            return Result.fail("用户未登录");
        }
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            return Result.fail("头像URL不能为空");
        }
        CustomerUserInfoVO updatedUserInfo = userService.updateCustomerUserAvatar(userId, avatarUrl);
        if (updatedUserInfo == null) {
            return Result.fail("用户不存在");
        }
        return Result.ok(updatedUserInfo);
    }

}
