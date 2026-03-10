package com.goodskill.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

/**
 * AuthTokenFacadeImpl 单元测试
 * 测试认证令牌门面服务
 */
@ExtendWith(MockitoExtension.class)
class AuthTokenFacadeImplTest {

    @InjectMocks
    private AuthTokenFacadeImpl authTokenFacade;

    /**
     * 测试：判断是否登录
     */
    @Test
    void shouldCheckIsLogin() {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // Given
            stpUtilMock.when(StpUtil::isLogin).thenReturn(true);

            // When
            boolean result = authTokenFacade.isLogin();

            // Then
            assertTrue(result);
            stpUtilMock.verify(StpUtil::isLogin);
        }
    }

    /**
     * 测试：获取Token信息
     */
    @Test
    void shouldGetTokenInfo() {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // Given
            SaTokenInfo mockTokenInfo = new SaTokenInfo();
            stpUtilMock.when(StpUtil::getTokenInfo).thenReturn(mockTokenInfo);

            // When
            SaTokenInfo result = authTokenFacade.getTokenInfo();

            // Then
            assertNotNull(result);
            stpUtilMock.verify(StpUtil::getTokenInfo);
        }
    }

    /**
     * 测试：创建Access Token
     */
    @Test
    void shouldCreateAccessToken() {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // Given
            Integer userId = 1;
            String expectedToken = "access-token-123";
            stpUtilMock.when(() -> StpUtil.createLoginSession(userId)).thenReturn(expectedToken);

            // When
            String result = authTokenFacade.createAccessToken(userId);

            // Then
            assertEquals(expectedToken, result);
            stpUtilMock.verify(() -> StpUtil.createLoginSession(userId));
        }
    }

    /**
     * 测试：获取Access Token超时时间
     */
    @Test
    void shouldGetAccessTokenTimeout() {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // Given
            String accessToken = "access-token-123";
            long expectedTimeout = 3600L;
            stpUtilMock.when(() -> StpUtil.getTokenTimeout(accessToken)).thenReturn(expectedTimeout);

            // When
            long result = authTokenFacade.getAccessTokenTimeout(accessToken);

            // Then
            assertEquals(expectedTimeout, result);
            stpUtilMock.verify(() -> StpUtil.getTokenTimeout(accessToken));
        }
    }

    /**
     * 测试：创建Refresh Token
     */
    @Test
    void shouldCreateRefreshToken() {
        try (MockedStatic<SaTempUtil> saTempUtilMock = mockStatic(SaTempUtil.class)) {
            // Given
            Integer userId = 1;
            long refreshExpireSeconds = 604800L;
            String expectedToken = "refresh-token-123";
            saTempUtilMock.when(() -> SaTempUtil.createToken(userId, refreshExpireSeconds)).thenReturn(expectedToken);

            // When
            String result = authTokenFacade.createRefreshToken(userId, refreshExpireSeconds);

            // Then
            assertEquals(expectedToken, result);
            saTempUtilMock.verify(() -> SaTempUtil.createToken(userId, refreshExpireSeconds));
        }
    }

    /**
     * 测试：解析Refresh Token成功
     */
    @Test
    void shouldParseRefreshTokenSuccessfully() {
        try (MockedStatic<SaTempUtil> saTempUtilMock = mockStatic(SaTempUtil.class)) {
            // Given
            String refreshToken = "refresh-token-123";
            Integer expectedUserId = 1;
            saTempUtilMock.when(() -> SaTempUtil.parseToken(refreshToken, Integer.class)).thenReturn(expectedUserId);

            // When
            Integer result = authTokenFacade.parseRefreshToken(refreshToken);

            // Then
            assertEquals(expectedUserId, result);
        }
    }

    /**
     * 测试：解析无效Refresh Token返回null
     */
    @Test
    void shouldReturnNullWhenParseInvalidRefreshToken() {
        try (MockedStatic<SaTempUtil> saTempUtilMock = mockStatic(SaTempUtil.class)) {
            // Given
            String invalidToken = "invalid-token";
            saTempUtilMock.when(() -> SaTempUtil.parseToken(invalidToken, Integer.class))
                    .thenThrow(new RuntimeException("Invalid token"));

            // When
            Integer result = authTokenFacade.parseRefreshToken(invalidToken);

            // Then
            assertNull(result);
        }
    }

    /**
     * 测试：删除Refresh Token
     */
    @Test
    void shouldDeleteRefreshToken() {
        try (MockedStatic<SaTempUtil> saTempUtilMock = mockStatic(SaTempUtil.class)) {
            // Given
            String refreshToken = "refresh-token-123";

            // When
            authTokenFacade.deleteRefreshToken(refreshToken);

            // Then
            saTempUtilMock.verify(() -> SaTempUtil.deleteToken(refreshToken));
        }
    }

    /**
     * 测试：登出
     */
    @Test
    void shouldLogout() {
        try (MockedStatic<StpUtil> stpUtilMock = mockStatic(StpUtil.class)) {
            // When
            authTokenFacade.logout();

            // Then
            stpUtilMock.verify(StpUtil::logout);
        }
    }
}
