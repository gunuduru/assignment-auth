package com.assignment.auth.dto

import com.assignment.auth.entity.User
import java.time.LocalDateTime

/**
 * 회원가입 응답 DTO
 */
data class UserRegisterResponse(
    val id: Long,
    val username: String,
    val name: String,
    val createdAt: LocalDateTime
) {
    companion object {
        /**
         * User 엔티티로부터 응답 DTO 생성
         */
        fun from(user: User): UserRegisterResponse {
            return UserRegisterResponse(
                id = user.id,
                username = user.username,
                name = user.name,
                createdAt = user.createdAt
            )
        }
    }
} 