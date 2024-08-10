
package com.goodskill.core.util;

import com.goodskill.core.enums.CommonConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HeaderUtilTest {

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        // Mock the RequestAttributes being set in the RequestContextHolder
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @Test
    public void testGetUserIdFromHeader() {
        // Set up the expected user ID in the mock request object
        String expectedUserId = "12345";
        when(request.getHeader(CommonConstants.USER_ID_HEADER)).thenReturn(expectedUserId);

        // Call the method under test
        String userId = HeaderUtil.getUserId();

        // Assert the expected result
        assertEquals(expectedUserId, userId);
    }

    @Test
    public void testGetUserIdFromHeaderWhenMissing() {
        // Prepare the scenario where the header is missing
        when(request.getHeader(CommonConstants.USER_ID_HEADER)).thenReturn(null);

        // Call the method under test
        String userId = HeaderUtil.getUserId();

        // Assert that an empty string is returned when the header is missing
        assertNull(userId);
    }

    @Test
    public void testGetUserIdWhenNoRequestAvailable() {
        // Clear request attributes to simulate no request being available
        RequestContextHolder.setRequestAttributes(null);

        // Call the method under test
        String userId = HeaderUtil.getUserId();

        // Assert that an empty string is returned when no request is available
        assertEquals("", userId);
    }
}
