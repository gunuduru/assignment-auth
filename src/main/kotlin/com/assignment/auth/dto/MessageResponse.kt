package com.assignment.auth.dto

/**
 * 메시지 발송 응답 DTO
 */
data class MessageResponse(
    /**
     * 대상 연령대
     */
    val ageGroup: Int,

    /**
     * 대상 사용자 수
     */
    val targetUserCount: Int,

    /**
     * 스케줄링된 메시지 수
     */
    val scheduledMessageCount: Int,

    /**
     * 발송 시작 예상 시간 (분 단위)
     */
    val estimatedStartTime: String
) 