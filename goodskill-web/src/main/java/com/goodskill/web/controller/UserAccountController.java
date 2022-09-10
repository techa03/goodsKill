package com.goodskill.web.controller;

import com.goodskill.api.service.GoodsService;
import com.goodskill.api.service.SeckillService;
import com.goodskill.api.service.UserAccountService;
import com.goodskill.entity.User;
import com.goodskill.web.util.HttpUrlUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

@Tag(name = "登录注册控制")
@Controller
public class UserAccountController {
    @DubboReference
    private UserAccountService userAccountService;
    @DubboReference
    private GoodsService goodsService;
    @Resource
    private SeckillService seckillService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/register/create", method = RequestMethod.POST)
    public String register(User user) {
        User userEncrypt = new User();
        BeanUtils.copyProperties(user,userEncrypt);
        userEncrypt.setPassword(new SimpleHash("MD5", user.getPassword(), ByteSource.Util.bytes(user.getAccount()), 2).toString());
        userAccountService.register(userEncrypt);
        // 注册成功后直接登录
        login(user);
        return HttpUrlUtil.replaceRedirectUrl("redirect:/seckill/list");
    }

    @GetMapping(value = "/login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping(value = "/login/session", method = RequestMethod.POST)
    public String login(User user) {
        UsernamePasswordToken token = new UsernamePasswordToken(user.getAccount(), user.getPassword());
        Subject subject = SecurityUtils.getSubject();

        Session session = subject.getSession();
        try {
            subject.login(token);
            session.setAttribute("user", user);
        } catch (Exception e) {
            session.setAttribute("user", null);
            logger.error(e.getMessage(), e);
            return HttpUrlUtil.replaceRedirectUrl("redirect:/login");
        }
        return HttpUrlUtil.replaceRedirectUrl("redirect:/seckill/list");
    }

    @GetMapping(value = "/register")
    public String register() {
        return "register";
    }

    @GetMapping(value = "/sign-out")
    public String signOut() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return HttpUrlUtil.replaceRedirectUrl("redirect:/login");
    }


}
