package com.goodskill.oauth2.authserver.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v5")
public class UserInfoController {

    @GetMapping("/user")
    public Object user() {
//        System.out.println(obj);
        return new Object();
    }

    @GetMapping("/whoami")
    public String whoami(@AuthenticationPrincipal(expression="name") String name) {
        return name;
    }
}
