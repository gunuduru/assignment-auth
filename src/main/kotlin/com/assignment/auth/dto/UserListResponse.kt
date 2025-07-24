package com.assignment.auth.dto

import org.springframework.data.domain.Page

/**
 * 사용자 목록 조회 응답 DTO (페이지네이션 포함)
 */
data class UserListResponse(
    val users: List<UserResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    companion object {
        /**
         * Page<UserResponse>로부터 응답 DTO 생성
         */
        fun from(page: Page<UserResponse>): UserListResponse {
            return UserListResponse(
                users = page.content,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                currentPage = page.number,
                pageSize = page.size,
                hasNext = page.hasNext(),
                hasPrevious = page.hasPrevious()
            )
        }
    }
} 