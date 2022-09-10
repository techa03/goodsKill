package com.goodskill.web.controller;

import com.goodskill.api.bo.UserBO;
import com.goodskill.api.service.UserAuthAccountService;
import com.goodskill.common.info.OAuth2UserInfo;
import com.goodskill.common.util.OAuth2UserInfoConverUtil;
import com.goodskill.common.util.UserAccountUtil;
import com.goodskill.web.util.HttpUrlUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Tag(name = "公共控制")
@RestController
@Slf4j
public class CommonController {
    @DubboReference
    private UserAuthAccountService userAuthAccountService;
    @Value("${server.servlet.context-path}")
    private String serverContextPath;

    @GetMapping("/error")
    public void handleError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        if (subject.isAuthenticated()) {
            response.sendRedirect(HttpUrlUtil.replaceRedirectUrl(serverContextPath + "/seckill/list"));
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object obj = authentication.getPrincipal();
        String loginPath = serverContextPath + "/login";
        if (obj != null) {
            OAuth2User oAuth2User;
            try {
                oAuth2User = (OAuth2User) obj;
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
                response.sendRedirect(HttpUrlUtil.replaceRedirectUrl(loginPath));
                return;
            }
            // 将已授权的第三方账户信息进行实体类转换
            OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoConverUtil.convert(oAuth2User);
            String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
            if (StringUtils.isEmpty(oAuth2UserInfo.getAccount())
                    || !userAuthAccountService.ifThirdAccountExists(oAuth2UserInfo.getAccount(), registrationId)) {
                response.sendRedirect(HttpUrlUtil.replaceRedirectUrl(loginPath));
                return;
            }
            // 从系统中查找第三方账户对应的用户信息
            UserBO user = userAuthAccountService.findByThirdAccount(oAuth2UserInfo.getAccount(), registrationId);
            UsernamePasswordToken usernamePasswordToken =
                    new UsernamePasswordToken(UserAccountUtil.generateUsername(user.getAccount(), registrationId),
                            oAuth2UserInfo.getAccount());
            try {
                subject.login(usernamePasswordToken);
                session.setAttribute("user", user);
            } catch (Exception e) {
                session.setAttribute("user", null);
                log.warn(e.getMessage(), e);
                response.sendRedirect(HttpUrlUtil.replaceRedirectUrl(loginPath));
                return;
            }
            response.sendRedirect(HttpUrlUtil.replaceRedirectUrl(serverContextPath + "/seckill/list"));
            return;
        }
        response.sendRedirect(HttpUrlUtil.replaceRedirectUrl(loginPath));
    }

    @GetMapping("/user")
    public Map<String, Object> user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Collections.singletonMap("name", ((OAuth2User) authentication.getPrincipal()).getAttribute("name"));
    }

}
