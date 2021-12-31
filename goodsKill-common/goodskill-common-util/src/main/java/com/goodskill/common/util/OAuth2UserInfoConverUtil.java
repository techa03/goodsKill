package com.goodskill.common.util;

import com.goodskill.common.info.OAuth2UserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * 用户授权信息转换工具类
 *
 * @author heng
 * @since 2021/3/27
 */
public class OAuth2UserInfoConverUtil {

    /**
     * 将已授权的信息转换成通用信息
     *
     * @param oAuth2User 已授权账户信息
     * @return 用户授权通用信息
     */
    public static OAuth2UserInfo convert(OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = new OAuth2UserInfo();
        oAuth2UserInfo.setAccount(oAuth2User.getAttribute("name"));
        return oAuth2UserInfo;
    }

    private OAuth2UserInfoConverUtil() {
    }
}
