package com.assignment.auth.dto

/**
 * 카카오톡 메시지 API 요청 DTO
 */
data class KakaoTalkMessageRequest(
    val phone: String,
    val message: String
)

/**
 * SMS 메시지 API 요청 DTO
 */
data class SmsMessageRequest(
    val message: String
)

/**
 * SMS API 응답 DTO
 */
data class SmsResponse(
    val result: String
) 