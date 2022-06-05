package com.goodskill.service.controller;

import com.goodskill.api.dto.AuthResponseDTO;
import com.goodskill.common.util.JwtUtils;
import com.goodskill.entity.User;
import com.goodskill.service.common.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;

import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Mock
    UserService userService;
    @Mock
    StringRedisTemplate redisTemplate;
    @Mock
    Logger log;
    @InjectMocks
    AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToken() {
        User value = new User();
        value.setUsername("test");
        when(userService.getOne(any())).thenReturn(value);
        ValueOperations<String, String> operations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(operations);
        AuthResponseDTO result = authController.token("test", "password");
        Assertions.assertEquals(result.getCode(), "200");
    }

    @Test
    void testVerifyUser() {
        String token = JwtUtils.createToken(new HashMap<>());
        AuthResponseDTO result = authController.verifyUser(token, "userName");
        Assertions.assertEquals(AuthResponseDTO.builder().token(token).code("500").build(), result);
    }

    @Test
    void testVerifyUserExists() {
        HashMap<String, Object> claimMap = new HashMap<>();
        claimMap.put("username", "test");
        String token = JwtUtils.createToken(claimMap);
        AuthResponseDTO result = authController.verifyUser(token, "test");
        Assertions.assertEquals(AuthResponseDTO.builder().token(token)
                .userName("test").code("200").build(), result);
    }

    @Test
    void testVerifyToken() {
        String token = JwtUtils.createToken(new HashMap<>());
        AuthResponseDTO result = authController.verifyToken(token);
        Assertions.assertEquals(result.getCode(), "200");
    }

    @Test
    void testRefresh() {
        ValueOperations<String, String> operations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(operations);

        String token = JwtUtils.createToken(new HashMap<>());
        AuthResponseDTO r = authController.verifyToken(token);
        when(operations.get(r.getRefreshKey())).thenReturn(JwtUtils.createToken(new HashMap<>()));
        AuthResponseDTO result = authController.refresh(r.getRefreshKey());
        Assertions.assertNotEquals(token, result.getToken());
    }
}
