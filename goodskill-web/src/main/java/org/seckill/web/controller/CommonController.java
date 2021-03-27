package org.seckill.web.controller;

import com.goodskill.common.info.OAuth2UserInfo;
import com.goodskill.common.util.OAuth2UserInfoConverUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.seckill.api.service.UserAuthAccountService;
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

    @GetMapping("/")
    public void handleError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object obj = authentication.getPrincipal();
        if (obj != null) {
            OAuth2User oAuth2User;
            try {
                oAuth2User = (OAuth2User) obj;
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
                response.sendRedirect("/goodskill/login");
                return;
            }
            OAuth2UserInfo userInfo = OAuth2UserInfoConverUtil.convert(oAuth2User);
            String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
            if (StringUtils.isEmpty(userInfo.getAccount())
                    || userAuthAccountService.ifThirdAccountExists(userInfo.getAccount(), registrationId)) {
                response.sendRedirect("/goodskill/login");
                return;
            }
        }
        response.sendRedirect("/goodskill/seckill/list");
    }

    @GetMapping("/user")
    public Map<String, Object> user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Collections.singletonMap("name", ((OAuth2User) authentication.getPrincipal()).getAttribute("name"));
    }

}
