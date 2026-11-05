package com.goodskill.core.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JSON Web Token 工具类
 */
@Slf4j
public class JwtUtils {
    /**
     * JVM system property used to configure the JWT signing key.
     */
    private static final String SECRET_KEY_PROPERTY = "goodskill.jwt.secret";
    /**
     * Environment variable used when the system property is not present.
     */
    private static final String SECRET_KEY_ENV = "GOODSKILL_JWT_SECRET";
    /**
     * HS256 requires at least 256 bits of key material.
     */
    private static final int MIN_SECRET_KEY_BYTES = 32;
    /**
     * 过期时间（毫秒单位）
     */
    private final static long TOKEN_EXPIRE_MILLIS = 1000 * 60 * 60 * 24;

    /**
     * 创建token
     *
     * @param claimMap
     * @return
     */
    public static String createToken(Map<String, Object> claimMap) {
        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                // 设置签发时间
                .setIssuedAt(new Date(currentTimeMillis))
                // 设置过期时间
                .setExpiration(new Date(currentTimeMillis + TOKEN_EXPIRE_MILLIS))
                .addClaims(claimMap)
                .signWith(generateKey())
                .compact();
    }

    /**
     * 验证token
     *
     * @param token
     * @return 0 验证成功，1、2、3、4、5 验证失败
     */
    public static int verifyToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(generateKey()).build().parseClaimsJws(token);
            return 0;
        } catch (ExpiredJwtException e) {
            log.warn(e.getMessage(), e);
            return 1;
        } catch (UnsupportedJwtException e) {
            log.warn(e.getMessage(), e);
            return 2;
        } catch (MalformedJwtException e) {
            log.warn(e.getMessage(), e);
            return 3;
        } catch (SignatureException e) {
            log.warn(e.getMessage(), e);
            return 4;
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
            return 5;
        }
    }

    /**
     * 解析token
     *
     * @param token
     * @return
     */
    public static Map<String, Object> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(generateKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 生成安全密钥
     *
     * @return
     */
    public static Key generateKey() {
        String secret = System.getProperty(SECRET_KEY_PROPERTY);
        if (secret == null || secret.isBlank()) {
            secret = System.getenv(SECRET_KEY_ENV);
        }
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT signing key is not configured. Set "
                    + SECRET_KEY_PROPERTY + " or " + SECRET_KEY_ENV + ".");
        }

        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_KEY_BYTES) {
            throw new IllegalStateException("JWT signing key must contain at least "
                    + MIN_SECRET_KEY_BYTES + " bytes.");
        }
        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public static void main(String[] args) throws InterruptedException {

        Map<String, Object> map = new HashMap<>();
        map.put("name", "1");
        map.put("age", "1");
        String token = createToken(map);
        System.out.println(token);
        Thread.sleep(2000);
        System.out.println(parseToken(token));
    }
}
