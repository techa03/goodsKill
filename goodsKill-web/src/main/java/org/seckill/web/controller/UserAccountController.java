package org.seckill.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.seckill.entity.User;
import org.seckill.api.service.SeckillService;
import org.seckill.api.service.UserAccountService;
import org.seckill.api.service.GoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class UserAccountController {
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SeckillService seckillService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional
    @RequestMapping(value = "/register/create", method = RequestMethod.POST)
    @ResponseBody
    public String register(User user) {
        userAccountService.register(user);
        return "success";
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String toLogin() {
        return "login";
    }

    @RequestMapping(value = "/login/session", method = RequestMethod.POST)
    public String login(User user) {
        UsernamePasswordToken token = new UsernamePasswordToken(user.getAccount(), user.getPassword());
        Subject subject = SecurityUtils.getSubject();

        try {
            subject.login(token);
            subject.getSession().setAttribute("user", user);
        } catch (Exception e) {
            subject.getSession().setAttribute("user", null);
            logger.error(e.getMessage(), e);
            return "redirect:/login";
        }
        return "redirect:/seckill/list";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register() {
        return "register";
    }
}
