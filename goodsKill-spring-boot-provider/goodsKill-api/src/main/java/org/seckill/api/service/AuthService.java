package org.seckill.api.service;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * 用户鉴权服务
 * @author techa03
 * @since 2020/12/26
 */
@FeignClient("goodskill-service-provider")
public interface AuthService {
}
