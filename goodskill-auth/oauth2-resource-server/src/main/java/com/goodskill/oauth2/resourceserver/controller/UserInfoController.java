package com.goodskill.oauth2.resourceserver.controller;

import com.alibaba.fastjson2.JSON;
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
        return JSON.parse("""
                {
                    "id": 1242484,
                    "login": "techa",
                    "name": "techa",
                    "avatar_url": "https://gitee.com/assets/no_portrait.png",
                    "url": "https://gitee.com/api/v5/users/techa",
                    "html_url": "https://gitee.com/techa",
                    "remark": "",
                    "followers_url": "https://gitee.com/api/v5/users/techa/followers",
                    "following_url": "https://gitee.com/api/v5/users/techa/following_url{/other_user}",
                    "gists_url": "https://gitee.com/api/v5/users/techa/gists{/gist_id}",
                    "starred_url": "https://gitee.com/api/v5/users/techa/starred{/owner}{/repo}",
                    "subscriptions_url": "https://gitee.com/api/v5/users/techa/subscriptions",
                    "organizations_url": "https://gitee.com/api/v5/users/techa/orgs",
                    "repos_url": "https://gitee.com/api/v5/users/techa/repos",
                    "events_url": "https://gitee.com/api/v5/users/techa/events{/privacy}",
                    "received_events_url": "https://gitee.com/api/v5/users/techa/received_events",
                    "type": "User",
                    "blog": null,
                    "weibo": null,
                    "bio": "",
                    "public_repos": 3,
                    "public_gists": 0,
                    "followers": 0,
                    "following": 1,
                    "stared": 1,
                    "watched": 7,
                    "created_at": "2017-03-03T19:53:20+08:00",
                    "updated_at": "2021-09-24T10:27:10+08:00",
                    "email": null
                }""");
    }

    @GetMapping("/whoami")
    public String whoami(@AuthenticationPrincipal Object name) {
        ((Jwt) name).getClaims().get("user_name");
        SecurityContextHolder.getContext().getAuthentication();
        return name.toString();
    }
}
