package com.assignment.auth.dto

/**
 * 유효성 검증 오류 정보
 */
data class ValidationError(
    val field: String,
    val message: String
) 