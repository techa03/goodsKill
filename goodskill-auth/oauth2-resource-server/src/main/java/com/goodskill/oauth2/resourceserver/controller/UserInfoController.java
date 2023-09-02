package com.goodskill.oauth2.resourceserver.controller;

import com.alibaba.fastjson2.JSON;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v5")
public class UserInfoController {

    @GetMapping("/user")
    public Object user() {
//        System.out.println(obj);
        Map map = new HashMap();
        map.put("id", "1");
        return JSON.parse("{\n" +
                "    \"id\": 1242484,\n" +
                "    \"login\": \"techa\",\n" +
                "    \"name\": \"techa\",\n" +
                "    \"avatar_url\": \"https://gitee.com/assets/no_portrait.png\",\n" +
                "    \"url\": \"https://gitee.com/api/v5/users/techa\",\n" +
                "    \"html_url\": \"https://gitee.com/techa\",\n" +
                "    \"remark\": \"\",\n" +
                "    \"followers_url\": \"https://gitee.com/api/v5/users/techa/followers\",\n" +
                "    \"following_url\": \"https://gitee.com/api/v5/users/techa/following_url{/other_user}\",\n" +
                "    \"gists_url\": \"https://gitee.com/api/v5/users/techa/gists{/gist_id}\",\n" +
                "    \"starred_url\": \"https://gitee.com/api/v5/users/techa/starred{/owner}{/repo}\",\n" +
                "    \"subscriptions_url\": \"https://gitee.com/api/v5/users/techa/subscriptions\",\n" +
                "    \"organizations_url\": \"https://gitee.com/api/v5/users/techa/orgs\",\n" +
                "    \"repos_url\": \"https://gitee.com/api/v5/users/techa/repos\",\n" +
                "    \"events_url\": \"https://gitee.com/api/v5/users/techa/events{/privacy}\",\n" +
                "    \"received_events_url\": \"https://gitee.com/api/v5/users/techa/received_events\",\n" +
                "    \"type\": \"User\",\n" +
                "    \"blog\": null,\n" +
                "    \"weibo\": null,\n" +
                "    \"bio\": \"\",\n" +
                "    \"public_repos\": 3,\n" +
                "    \"public_gists\": 0,\n" +
                "    \"followers\": 0,\n" +
                "    \"following\": 1,\n" +
                "    \"stared\": 1,\n" +
                "    \"watched\": 7,\n" +
                "    \"created_at\": \"2017-03-03T19:53:20+08:00\",\n" +
                "    \"updated_at\": \"2021-09-24T10:27:10+08:00\",\n" +
                "    \"email\": null\n" +
                "}");
    }

    @GetMapping("/whoami")
    public String whoami(@AuthenticationPrincipal Object name) {
        Jwt jwt = (Jwt) name;
        ((Jwt) name).getClaims().get("user_name");
        SecurityContextHolder.getContext().getAuthentication();
        return name.toString();
    }
}
