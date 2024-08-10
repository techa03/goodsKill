package com.goodskill.core.util;

/**
 * 用户信息工具类，主要用于从当前请求中提取用户信息。
 */
public class UserInfoUtil {

    /**
     * 从当前请求的 Header 中提取 userId。
     *
     * @return 用户 ID
     */
    public static String getUserId() {
        // 使用HeaderUtil的方法从请求头中获取userId，这里省略了具体的实现细节
        String userId = HeaderUtil.getUserId();
        return userId == null ? "" : userId;
    }

}
