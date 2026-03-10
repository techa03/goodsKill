package com.goodskill.auth.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.goodskill.auth.config.AuthTokenProperties;
import com.goodskill.auth.entity.User;
import com.goodskill.auth.pojo.dto.RefreshTokenDTO;
import com.goodskill.auth.pojo.dto.UserDTO;
import com.goodskill.auth.pojo.vo.LoginTokenVO;
import com.goodskill.auth.service.AuthTokenFacade;
import com.goodskill.auth.service.UserService;
import com.goodskill.core.info.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 登录测试
 */
@RestController
@Tag(name = "登录测试")
public class LoginController {
    @Resource
    private UserService userService;
    @Resource
    private AuthTokenProperties authTokenProperties;
    @Resource
    private AuthTokenFacade authTokenFacade;

    /**
     * 测试登录
     *
     * @return
     */
    @RequestMapping("/isLogin")
    @Operation(summary = "测试登录")
    public Result<String> isLogin(HttpServletRequest request) {
        return Result.ok("是否登录：" + authTokenFacade.isLogin());
    }

    @RequestMapping("/tokenInfo")
    @Operation(summary = "获取token信息")
    public Result<SaTokenInfo> tokenInfo() {
        return Result.ok(authTokenFacade.getTokenInfo());
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<String> logout(@RequestBody(required = false) Map<String, String> payload) {
        if (payload != null) {
            String refreshToken = payload.get("refreshToken");
            if (StringUtils.hasText(refreshToken)) {
                authTokenFacade.deleteRefreshToken(refreshToken);
            }
        }
        authTokenFacade.logout();
        return Result.ok();
    }

    @PostMapping("/register")
    @Operation(summary = "注册")
    public Result<String> register(@RequestBody UserDTO userDTO) {
        userService.register(userDTO);
        return Result.ok("注册成功");
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<LoginTokenVO> login(@Valid @RequestBody UserDTO userDTO) {
        User userAccount = userService.findByUserAccount(userDTO.getUsername());
        if (userAccount != null) {
            // 校验账户与用户输入的密码是否一致
            boolean b = userService.checkPassword(userAccount.getPassword(), userDTO.getPassword());
            if (!b) {
                return Result.fail("用户名或密码错误");
            }
            String accessToken = authTokenFacade.createAccessToken(userAccount.getId());
            String refreshToken = authTokenFacade.createRefreshToken(userAccount.getId(), authTokenProperties.getRefreshExpireSeconds());
            long accessExpireSeconds = authTokenFacade.getAccessTokenTimeout(accessToken);
            return Result.ok(buildLoginTokenVO(accessToken, refreshToken, accessExpireSeconds));
        }
        return Result.fail("用户名或密码错误");
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新访问令牌")
    public Result<LoginTokenVO> refresh(@Valid @RequestBody RefreshTokenDTO request) {
        String refreshToken = request.getRefreshToken();
        Integer userId = authTokenFacade.parseRefreshToken(refreshToken);
        if (userId == null) {
            return Result.fail(401, "refresh token无效或已过期");
        }
        if (userService.getById(userId) == null) {
            authTokenFacade.deleteRefreshToken(refreshToken);
            return Result.fail(401, "refresh token无效或已过期");
        }
        String accessToken = authTokenFacade.createAccessToken(userId);
        authTokenFacade.deleteRefreshToken(refreshToken);
        String newRefreshToken = authTokenFacade.createRefreshToken(userId, authTokenProperties.getRefreshExpireSeconds());
        long accessExpireSeconds = authTokenFacade.getAccessTokenTimeout(accessToken);
        return Result.ok(buildLoginTokenVO(accessToken, newRefreshToken, accessExpireSeconds));
    }

    private LoginTokenVO buildLoginTokenVO(String accessToken, String refreshToken, long accessExpireSeconds) {
        return LoginTokenVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .accessExpireSeconds(accessExpireSeconds)
                .refreshExpireSeconds(authTokenProperties.getRefreshExpireSeconds())
                .build();
    }
}
