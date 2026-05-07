package com.nuaa.club_manage_backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtils {
    // 秘钥，绝对不能泄露（这里随便写一段长字符串作为打样）
    private static final String SECRET = "NuaaClubManageSystemSecretKey2026123456";
    // 过期时间：设为 7 天
    private static final long EXPIRE = 7 * 24 * 60 * 60 * 1000;

    /**
     * 根据用户 ID 生成 Token
     */
    public static String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId) // 将 userID 存入 token
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE)) // 过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET) // 签名算法和秘钥
                .compact();
    }

    /**
     * 解析 Token，获取里面存的 userId
     */
    public static String getUserIdByToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            // 解析失败（比如被篡改、过期等）
            return null;
        }
    }
}