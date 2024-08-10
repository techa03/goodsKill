package com.goodskill.core.orm.mybatis.config;

import com.goodskill.core.enums.CommonConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class HeaderUtils {

    /**
     * 从当前请求的 Header 中提取 userId。
     *
     * @return 用户 ID
     */
    public static String getUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader(CommonConstants.USER_ID_HEADER);
        }
        return "";
    }
}
