//package com.goodskill.service.controller;
//
//import cn.hutool.crypto.digest.MD5;
//import com.goodskill.api.service.GoodsService;
//import com.goodskill.api.service.SeckillService;
//import com.goodskill.entity.User;
//import com.goodskill.service.inner.UserAccountService;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//
//@Tag(name = "登录注册控制")
//@Controller
//@Slf4j
//public class UserAccountController {
//    @Resource
//    private UserAccountService userAccountService;
//    @Resource
//    private GoodsService goodsService;
//    @Resource
//    private SeckillService seckillService;
//
//
//    @PostMapping("/sign_up")
//    public String register(User user) {
//        User userEncrypt = new User();
//        BeanUtils.copyProperties(user,userEncrypt);
//        userEncrypt.setPassword(MD5.create().digestHex(user.getPassword()));
//        userAccountService.register(userEncrypt);
//        // 注册成功后直接登录
//        login(user);
//        return null;
//    }
//
//    @PostMapping("/sign_in")
//    public String login(User user) {
//        UsernamePasswordToken token = new UsernamePasswordToken(user.getAccount(), user.getPassword());
//        Subject subject = SecurityUtils.getSubject();
//
//        Session session = subject.getSession();
//        try {
//            subject.login(token);
//            session.setAttribute("user", user);
//        } catch (Exception e) {
//            session.setAttribute("user", null);
//            log.error(e.getMessage(), e);
//            return "fail";
//        }
//        return null;
//    }
//
//    @GetMapping(value = "/sign_out")
//    public String signOut() {
//        Subject subject = SecurityUtils.getSubject();
//        subject.logout();
//        return null;
//    }
//
//
//}
