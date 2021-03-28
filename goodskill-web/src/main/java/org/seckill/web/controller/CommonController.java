package org.seckill.web.controller;

import com.goodskill.common.info.OAuth2UserInfo;
import com.goodskill.common.util.OAuth2UserInfoConverUtil;
import com.goodskill.common.util.UserAccountUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.seckill.api.service.UserAuthAccountService;
import org.seckill.api.user.bo.UserBo;
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

@Api(tags = "公共控制")
@RestController
@Slf4j
public class CommonController {
    @DubboReference
    private UserAuthAccountService userAuthAccountService;
    @Value("${server.servlet.context-path}")
    private String serverContextPath;

    @GetMapping("/")
    public void handleError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        if (subject.isAuthenticated()) {
            response.sendRedirect(serverContextPath + "/seckill/list");
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
                response.sendRedirect(loginPath);
                return;
            }
            // 将已授权的第三方账户信息进行实体类转换
            OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoConverUtil.convert(oAuth2User);
            String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
            if (StringUtils.isEmpty(oAuth2UserInfo.getAccount())
                    || !userAuthAccountService.ifThirdAccountExists(oAuth2UserInfo.getAccount(), registrationId)) {
                response.sendRedirect(loginPath);
                return;
            }
            // 从系统中查找第三方账户对应的用户信息
            UserBo user = userAuthAccountService.findByThirdAccount(oAuth2UserInfo.getAccount(), registrationId);
            UsernamePasswordToken usernamePasswordToken =
                    new UsernamePasswordToken(UserAccountUtil.generateUsername(user.getAccount(), registrationId),
                            oAuth2UserInfo.getAccount());
            try {
                subject.login(usernamePasswordToken);
                session.setAttribute("user", user);
            } catch (Exception e) {
                session.setAttribute("user", null);
                log.error(e.getMessage(), e);
                response.sendRedirect(loginPath);
                return;
            }
            response.sendRedirect(serverContextPath + "/seckill/list");
            return;
        }
        response.sendRedirect(loginPath);
    }

    @GetMapping("/user")
    public Map<String, Object> user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Collections.singletonMap("name", ((OAuth2User) authentication.getPrincipal()).getAttribute("name"));
    }

}
