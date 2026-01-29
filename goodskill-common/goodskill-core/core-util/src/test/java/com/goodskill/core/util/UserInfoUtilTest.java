package com.goodskill.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class UserInfoUtilTest {

    @Test
    void testGetUserId() {
        try (MockedStatic<HeaderUtil> mockedHeaderUtil = mockStatic(HeaderUtil.class)) {
            String expectedUserId = "12345";
            mockedHeaderUtil.when(HeaderUtil::getUserId).thenReturn(expectedUserId);

            String userId = UserInfoUtil.getUserId();
            assertEquals(expectedUserId, userId);
            mockedHeaderUtil.verify(HeaderUtil::getUserId);
        }
    }

    @Test
    void testGetUserIdWhenHeaderUtilReturnsNull() {
        try (MockedStatic<HeaderUtil> mockedHeaderUtil = mockStatic(HeaderUtil.class)) {
            mockedHeaderUtil.when(HeaderUtil::getUserId).thenReturn(null);

            String userId = UserInfoUtil.getUserId();
            assertEquals("", userId);
            mockedHeaderUtil.verify(HeaderUtil::getUserId);
        }
    }

    @Test
    void testGetUserIdWhenHeaderUtilReturnsEmptyString() {
        try (MockedStatic<HeaderUtil> mockedHeaderUtil = mockStatic(HeaderUtil.class)) {
            String expectedUserId = "";
            mockedHeaderUtil.when(HeaderUtil::getUserId).thenReturn(expectedUserId);

            String userId = UserInfoUtil.getUserId();
            assertEquals(expectedUserId, userId);
            mockedHeaderUtil.verify(HeaderUtil::getUserId);
        }
    }
}
