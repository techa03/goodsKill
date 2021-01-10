package org.seckill.web.controller;

import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.seckill.api.service.GoodsService;
import org.seckill.api.service.SeckillService;
import org.seckill.api.service.UserAccountService;
import org.seckill.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

@Api(tags = "登录注册控制")
@Controller
public class UserAccountController {
    @Reference
    private UserAccountService userAccountService;
    @Reference
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
        return "redirect:/seckill/list";
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
            return "redirect:/login";
        }
        return "redirect:/seckill/list";
    }

    @GetMapping(value = "/register")
    public String register() {
        return "register";
    }

    @GetMapping(value = "seckill/signOut")
    public String signOut() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:login";
    }


}
