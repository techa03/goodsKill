package com.goodskill.core.jwt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilsTest {

    private static final String SECRET_PROPERTY = "goodskill.jwt.secret";
    private static final String VALID_SECRET = "0123456789abcdef0123456789abcdef";

    private String originalSecret;

    @BeforeEach
    void saveSecretProperty() {
        originalSecret = System.getProperty(SECRET_PROPERTY);
    }

    @AfterEach
    void restoreSecretProperty() {
        if (originalSecret == null) {
            System.clearProperty(SECRET_PROPERTY);
        } else {
            System.setProperty(SECRET_PROPERTY, originalSecret);
        }
    }

    @Test
    void createsAndParsesTokenWithConfiguredSecret() {
        System.setProperty(SECRET_PROPERTY, VALID_SECRET);

        String token = JwtUtils.createToken(Map.of("account", "test-account"));

        assertEquals("test-account", JwtUtils.parseToken(token).get("account"));
    }

    @Test
    void rejectsShortConfiguredSecret() {
        System.setProperty(SECRET_PROPERTY, "too-short");

        assertThrows(IllegalStateException.class, JwtUtils::generateKey);
    }
}
