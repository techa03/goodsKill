package com.goodskill.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * JSON Web Token 工具类
 */
public class JwtUtils {

    /**
     * key（按照签名算法的字节长度设置key）
     */
    private final static String SECRET_KEY = "0123456789_0123456789_0123456789";
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
            e.printStackTrace();
            return 1;
        } catch (UnsupportedJwtException e) {
            e.printStackTrace();
            return 2;
        } catch (MalformedJwtException e) {
            e.printStackTrace();
            return 3;
        } catch (SignatureException e) {
            e.printStackTrace();
            return 4;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
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
        return new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    public static void main(String[] args) throws InterruptedException {
        String token = createToken(null);
        System.out.println(token);
        Thread.sleep(2000);
        System.out.println(verifyToken(token));
    }
}