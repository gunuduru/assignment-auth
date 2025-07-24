package com.assignment.auth.dto

import com.assignment.auth.entity.User
import java.time.LocalDateTime

/**
 * 사용자 정보 조회 응답 DTO
 */
data class UserResponse(
    val id: Long,
    val username: String,
    val name: String,
    val ssn: String,
    val phoneNumber: String,
    val address: String,

    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        /**
         * User 엔티티로부터 응답 DTO 생성
         */
        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                username = user.username,
                name = user.name,
                ssn = user.ssn,
                phoneNumber = user.phoneNumber,
                address = user.address,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
        }
    }
} 