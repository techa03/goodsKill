package com.goodskill.api.service;

import com.goodskill.api.dto.AuthResponseDTO;
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
    AuthResponseDTO token(@RequestParam("userName") String userName, @RequestParam("password") String password);

    /**
     * 验证用户口令合法性
     *
     * @param userName 用户名
     * @param token    待验证口令
     * @return AuthResponseDTO
     */
    @PostMapping("/verifyUser")
    AuthResponseDTO verifyUser(@RequestParam("token") String token, @RequestParam("userName") String userName);

    /**
     * 验证用户口令合法性
     *
     * @param token    待验证口令
     * @return AuthResponseDTO
     */
    @PostMapping("/verifyToken")
    AuthResponseDTO verifyToken(@RequestParam("token") String token);

    /**
     * 刷新token，如未过期则获取原token，过期重新生成token
     *
     * @param refreshKey 待刷新的token主键
     * @return AuthResponseDTO
     */
    @PostMapping("/refresh")
    AuthResponseDTO refresh(@RequestParam("refreshKey") String refreshKey);

}
