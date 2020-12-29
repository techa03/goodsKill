package org.seckill.api.service;

import org.seckill.api.dto.AuthResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户鉴权服务
 *
 * @author techa03
 * @since 2020/12/26
 */
@FeignClient("goodskill-service-provider")
public interface AuthService {

    /**
     * 获取用户口令
     *
     * @param userName 用户名
     * @param password 密码
     * @return AuthResponseDTO
     */
    @PostMapping("/token")
    AuthResponseDTO token(@RequestParam String userName, @RequestParam String password);

    /**
     * 验证用户口令合法性
     *
     * @param userName 用户名
     * @param token    待验证口令
     * @return AuthResponseDTO
     */
    @PostMapping("/verify")
    AuthResponseDTO verify(@RequestParam String token, @RequestParam String userName);

    /**
     * 刷新token，如未过期则获取原token，过期重新生成token
     *
     * @param userName   待验证用户名
     * @param refreshKey 待刷新的token主键
     * @param password   密码
     * @return AuthResponseDTO
     */
    @PostMapping("/refresh")
    AuthResponseDTO refresh(@RequestParam String refreshKey, @RequestParam String userName,
                            @RequestParam String password);

}
