package com.goodskill.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "goodskill-auth")
public interface AuthFeignClient {
    @GetMapping("/user/listUserPermission")
    List<String> listUserPermission(@RequestParam String loginId, @RequestParam String loginType);

    @GetMapping("/user/listUserRole")
    List<String> listUserRole(@RequestParam String loginId, @RequestParam String loginType);
}
