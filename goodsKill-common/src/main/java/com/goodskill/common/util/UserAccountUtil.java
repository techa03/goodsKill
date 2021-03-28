package com.goodskill.common.util;

/**
 * 用户授权信息转换工具类
 *
 * @author heng
 * @since 2021/3/27
 */
public class UserAccountUtil {

    /**
     * 将已授权的信息转换成通用信息
     *
     * @param username 用户信息
     * @param extraInfo 补充信息
     * @return 用户授权通用信息
     */
    public static String generateUsername(String username, String extraInfo) {
        StringBuilder sb = new StringBuilder(username).append("-").append(extraInfo);
        return sb.toString();
    }

    private UserAccountUtil() {
    }
}
