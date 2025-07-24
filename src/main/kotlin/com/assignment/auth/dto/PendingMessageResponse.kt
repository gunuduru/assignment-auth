package com.assignment.auth.dto

/**
 * 대기 중인 메시지 수 조회 응답 DTO
 */
data class PendingMessageResponse(
    val pendingMessageCount: Long
) 