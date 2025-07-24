package com.assignment.auth.dto

import com.assignment.auth.entity.User
import java.time.LocalDateTime

/**
 * 로그인 응답 DTO
 */
data class LoginResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long, // 토큰 만료 시간 (초)
    val user: UserInfo
) {
    /**
     * 로그인한 사용자 기본 정보
     */
    data class UserInfo(
        val id: Long,
        val username: String,
        val name: String,
        val isActive: Boolean,
        val lastLoginAt: LocalDateTime = LocalDateTime.now()
    )

    companion object {
        /**
         * User 엔티티와 토큰 정보로부터 응답 DTO 생성
         */
        fun from(user: User, accessToken: String, expiresIn: Long): LoginResponse {
            return LoginResponse(
                accessToken = accessToken,
                expiresIn = expiresIn,
                user = UserInfo(
                    id = user.id,
                    username = user.username,
                    name = user.name,
                    isActive = user.isActive
                )
            )
        }
    }
} 