package com.assignment.auth.dto

/**
 * 사용자 통계 조회 응답 DTO
 */
data class UserStatisticsResponse(
    val totalUsers: Long,
    val activeUsers: Long,
    val inactiveUsers: Long
) {
    companion object {
        /**
         * Map으로부터 응답 DTO 생성
         */
        fun from(statistics: Map<String, Long>): UserStatisticsResponse {
            return UserStatisticsResponse(
                totalUsers = statistics["totalUsers"] ?: 0L,
                activeUsers = statistics["activeUsers"] ?: 0L,
                inactiveUsers = statistics["inactiveUsers"] ?: 0L
            )
        }
    }
} 