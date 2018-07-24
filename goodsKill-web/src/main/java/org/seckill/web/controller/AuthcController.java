package org.seckill.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.seckill.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seckill")
@Slf4j
public class AuthcController {
    /**
     * /seckill/** = authc 任何通过表单登录的用户都可以访问
     * @return
     */
    @RequestMapping("/**")
    public String anyuser() {
        return "redirect:/seckill/list";
    }

    /**
     *  /seckill/admin = user[admin] 只有具备admin角色的用户才可以访问，否则请求将被重定向至登录界面
     * @return
     */
    @RequestMapping("/admin")
    public String admin() {
        // TODO 管理员页面待完成
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        return "admin/admin";
    }
}