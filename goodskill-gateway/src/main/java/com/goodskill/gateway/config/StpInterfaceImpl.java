package com.goodskill.gateway.config;

import cn.dev33.satoken.stp.StpInterface;
import com.goodskill.gateway.feign.AuthFeignClient;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * sa-token自定义权限加载接口实现类
 */
@Component
public class StpInterfaceImpl implements StpInterface {
    @Resource
    private AuthFeignClient authFeignClient;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return CompletableFuture.supplyAsync(() -> authFeignClient.listUserPermission(String.valueOf(loginId), loginType)).join();
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return CompletableFuture.supplyAsync(() -> authFeignClient.listUserRole(String.valueOf(loginId), loginType)).join();
    }

}
