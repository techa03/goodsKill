package com.goodskill.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodskill.auth.config.AuthTokenProperties;
import com.goodskill.auth.entity.User;
import com.goodskill.auth.service.AuthTokenFacade;
import com.goodskill.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @Mock
    private UserService userService;
    @Mock
    private AuthTokenProperties authTokenProperties;
    @Mock
    private AuthTokenFacade authTokenFacade;
    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    void shouldLoginAndReturnTokens() throws Exception {
        User user = new User();
        user.setId(21);
        user.setPassword("encoded");
        Mockito.when(userService.findByUserAccount("admin123")).thenReturn(user);
        Mockito.when(userService.checkPassword("encoded", "aa123456")).thenReturn(true);
        Mockito.when(authTokenProperties.getRefreshExpireSeconds()).thenReturn(604800L);
        Mockito.when(authTokenFacade.createAccessToken(eq(21))).thenReturn("access-token");
        Mockito.when(authTokenFacade.getAccessTokenTimeout("access-token")).thenReturn(30L);
        Mockito.when(authTokenFacade.createRefreshToken(eq(21), eq(604800L))).thenReturn("refresh-token");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "admin123", "password", "aa123456"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.accessExpireSeconds").value(30))
                .andExpect(jsonPath("$.data.refreshExpireSeconds").value(604800));
    }

    @Test
    void shouldRejectLoginWhenPasswordInvalid() throws Exception {
        User user = new User();
        user.setId(21);
        user.setPassword("encoded");
        Mockito.when(userService.findByUserAccount("admin123")).thenReturn(user);
        Mockito.when(userService.checkPassword("encoded", "wrong")).thenReturn(false);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "admin123", "password", "wrong"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("用户名或密码错误"));

        Mockito.verify(authTokenFacade, Mockito.never()).createAccessToken(eq(21));
        Mockito.verify(authTokenFacade, Mockito.never()).createRefreshToken(eq(21), anyLong());
    }

    @Test
    void shouldRefreshTokenAndRotateRefreshToken() throws Exception {
        User user = new User();
        user.setId(21);
        Mockito.when(authTokenFacade.parseRefreshToken("old-refresh")).thenReturn(21);
        Mockito.when(userService.getById(21)).thenReturn(user);
        Mockito.when(authTokenProperties.getRefreshExpireSeconds()).thenReturn(604800L);
        Mockito.when(authTokenFacade.createAccessToken(eq(21))).thenReturn("new-access");
        Mockito.when(authTokenFacade.getAccessTokenTimeout("new-access")).thenReturn(30L);
        Mockito.when(authTokenFacade.createRefreshToken(eq(21), eq(604800L))).thenReturn("new-refresh");

        mockMvc.perform(post("/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", "old-refresh"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.accessToken").value("new-access"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh"));

        Mockito.verify(authTokenFacade).deleteRefreshToken("old-refresh");
    }

    @Test
    void shouldRejectRefreshWhenTokenInvalid() throws Exception {
        Mockito.when(authTokenFacade.parseRefreshToken("invalid-refresh")).thenReturn(null);

        mockMvc.perform(post("/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", "invalid-refresh"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.msg").value("refresh token无效或已过期"));

        Mockito.verify(authTokenFacade, Mockito.never()).createAccessToken(Mockito.anyInt());
    }

    @Test
    void shouldLogoutAndDeleteRefreshToken() throws Exception {
        mockMvc.perform(post("/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", "rt-1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        Mockito.verify(authTokenFacade).deleteRefreshToken("rt-1");
        Mockito.verify(authTokenFacade).logout();
    }
}
