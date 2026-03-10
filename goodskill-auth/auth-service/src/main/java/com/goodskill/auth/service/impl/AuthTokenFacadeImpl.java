package com.goodskill.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import com.goodskill.auth.service.AuthTokenFacade;
import org.springframework.stereotype.Service;

@Service
public class AuthTokenFacadeImpl implements AuthTokenFacade {
    @Override
    public boolean isLogin() {
        return StpUtil.isLogin();
    }

    @Override
    public SaTokenInfo getTokenInfo() {
        return StpUtil.getTokenInfo();
    }

    @Override
    public String createAccessToken(Integer userId) {
        return StpUtil.createLoginSession(userId);
    }

    @Override
    public long getAccessTokenTimeout(String accessToken) {
        return StpUtil.getTokenTimeout(accessToken);
    }

    @Override
    public String createRefreshToken(Integer userId, long refreshExpireSeconds) {
        return SaTempUtil.createToken(userId, refreshExpireSeconds);
    }

    @Override
    public Integer parseRefreshToken(String refreshToken) {
        try {
            return SaTempUtil.parseToken(refreshToken, Integer.class);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        SaTempUtil.deleteToken(refreshToken);
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }
}
