package com.assignment.auth.util

import com.assignment.auth.config.JwtConfig
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

/**
 * JWT 토큰 생성 및 검증을 담당하는 유틸리티 클래스
 */
@Component
class JwtUtil(
    private val jwtConfig: JwtConfig
) {
    
    private val key: Key = Keys.hmacShaKeyFor(jwtConfig.secret.toByteArray())

    /**
     * 사용자 정보로부터 JWT 토큰 생성
     * 
     * @param userId 사용자 ID
     * @param username 사용자명
     * @return 생성된 JWT 토큰
     */
    fun generateToken(userId: Long, username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtConfig.expiration)

        return Jwts.builder()
            .setSubject(username)
            .claim("userId", userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    /**
     * JWT 토큰에서 사용자명 추출
     * 
     * @param token JWT 토큰
     * @return 사용자명
     */
    fun getUsernameFromToken(token: String): String {
        return getClaims(token).subject
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    fun getUserIdFromToken(token: String): Long {
        return getClaims(token).get("userId", Long::class.java)
    }

    /**
     * JWT 토큰에서 만료일 추출
     * 
     * @param token JWT 토큰
     * @return 만료일
     */
    fun getExpirationDateFromToken(token: String): Date {
        return getClaims(token).expiration
    }

    /**
     * JWT 토큰 유효성 검증
     * 
     * @param token JWT 토큰
     * @return 유효성 여부
     */
    fun validateToken(token: String): Boolean {
        return try {
            getClaims(token)
            true
        } catch (e: JwtException) {
            false
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    /**
     * JWT 토큰 만료 여부 확인
     * 
     * @param token JWT 토큰
     * @return 만료 여부
     */
    fun isTokenExpired(token: String): Boolean {
        return try {
            val expiration = getExpirationDateFromToken(token)
            expiration.before(Date())
        } catch (e: Exception) {
            true
        }
    }

    /**
     * JWT 토큰에서 Claims 추출
     * 
     * @param token JWT 토큰
     * @return Claims 객체
     */
    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
} 