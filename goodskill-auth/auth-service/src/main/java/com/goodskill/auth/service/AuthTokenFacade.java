package com.goodskill.auth.service;

import cn.dev33.satoken.stp.SaTokenInfo;

/**
 * 认证令牌门面接口。
 * <p>
 * 对 Sa-Token 的静态调用做一层封装，便于控制器解耦与单元测试。
 */
public interface AuthTokenFacade {
    /**
     * 判断当前请求上下文是否已登录。
     */
    boolean isLogin();

    /**
     * 获取当前请求上下文的 token 信息。
     */
    SaTokenInfo getTokenInfo();

    /**
     * 为指定用户签发 access token。
     *
     * @param userId               用户ID
     * @return access token
     */
    String createAccessToken(Integer userId);

    /**
     * 获取指定 access token 的剩余有效期（秒）。
     *
     * @param accessToken access token
     * @return 剩余有效期（秒）
     */
    long getAccessTokenTimeout(String accessToken);

    /**
     * 为指定用户签发 refresh token。
     *
     * @param userId                用户ID
     * @param refreshExpireSeconds  refresh token 过期秒数
     * @return refresh token
     */
    String createRefreshToken(Integer userId, long refreshExpireSeconds);

    /**
     * 解析 refresh token 获取用户ID。
     *
     * @param refreshToken refresh token
     * @return 用户ID，若无效或已过期返回 null
     */
    Integer parseRefreshToken(String refreshToken);

    /**
     * 删除（作废）指定 refresh token。
     */
    void deleteRefreshToken(String refreshToken);

    /**
     * 退出当前登录态。
     */
    void logout();
}
