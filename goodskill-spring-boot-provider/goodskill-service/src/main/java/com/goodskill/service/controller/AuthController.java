package com.goodskill.service.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.common.utils.UuidUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.goodskill.api.dto.AuthResponseDTO;
import com.goodskill.api.service.AuthService;
import com.goodskill.common.util.JwtUtils;
import com.goodskill.entity.User;
import com.goodskill.service.common.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * 提供用户鉴权服务
 *
 * @author techa03
 * @since 2020/12/29
 */
@RestController
@Slf4j
public class AuthController implements AuthService {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public AuthResponseDTO token(String userName, String password) {
        // MD5加密后的密码
        String passwordEncrypt = new SimpleHash("MD5", password,
                ByteSource.Util.bytes(userName), 2).toString();
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userName).eq(User::getPassword, passwordEncrypt));
        user.setPassword(null);
        String token = JwtUtils.createToken(BeanUtil.beanToMap(user));
        String refreshKey = UuidUtils.generateUuid();
        redisTemplate.opsForValue().set(refreshKey, token, Duration.ofHours(2L));
        return AuthResponseDTO.builder()
                .token(token)
                .userName(userName)
                .refreshKey(refreshKey)
                .code("200")
                .build();
    }

    @Override
    public AuthResponseDTO verifyUser(String token, String userName) {
        User user = null;
        String errorMsg = null;
        try {
            user = BeanUtil.mapToBean(JwtUtils.parseToken(token), User.class, true, null);
        } catch (Exception e) {
            errorMsg = e.getMessage();
            log.warn(e.getMessage(), e);
        }
        if (user != null && userName.equals(user.getUsername())) {
            return AuthResponseDTO.builder()
                    .token(token)
                    .userId(user.getId() != null ? String.valueOf(user.getId()) : null)
                    .userName(userName)
                    .code("200")
                    .build();
        } else {
            return AuthResponseDTO.builder()
                    .token(token)
                    .code("500")
                    .message(errorMsg)
                    .build();
        }
    }

    @Override
    public AuthResponseDTO verifyToken(String token) {
        User user = null;
        String errorMsg = null;
        try {
            user = BeanUtil.mapToBean(JwtUtils.parseToken(token), User.class, true, null);
        } catch (Exception e) {
            errorMsg = e.getMessage();
            log.warn(e.getMessage(), e);
        }
        if (user != null) {
            return AuthResponseDTO.builder()
                    .token(token)
                    .userId(user.getId() != null ? String.valueOf(user.getId()) : null)
                    .userName(user.getUsername())
                    .code("200")
                    .build();
        } else {
            return AuthResponseDTO.builder()
                    .token(token)
                    .code("500")
                    .message(errorMsg)
                    .build();
        }
    }

    @Override
    public AuthResponseDTO refresh(String refreshKey) {
        String token = redisTemplate.opsForValue().get(refreshKey);
        if (!StringUtils.isEmpty(token)) {
            User user = BeanUtil.mapToBean(JwtUtils.parseToken(token), User.class, true, null);
            token = JwtUtils.createToken(BeanUtil.beanToMap(user));
            redisTemplate.delete(refreshKey);
            refreshKey = UuidUtils.generateUuid();
            redisTemplate.opsForValue().set(refreshKey, token, Duration.ofHours(2L));
            return AuthResponseDTO.builder()
                    .token(token)
                    .userName(user.getUsername())
                    .refreshKey(refreshKey)
                    .code("200")
                    .build();
        } else {
            return AuthResponseDTO.builder()
                    .token(token)
                    .code("500")
                    .message("invalid refresh key!")
                    .build();
        }
    }
}
