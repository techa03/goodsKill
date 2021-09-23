package com.goodskill.oauth2.resourceserver.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public String whoami(@AuthenticationPrincipal Object name) {
        Jwt jwt = (Jwt) name;
        ((Jwt) name).getClaims().get("user_name");
        SecurityContextHolder.getContext().getAuthentication();
        return name.toString();
    }
}
