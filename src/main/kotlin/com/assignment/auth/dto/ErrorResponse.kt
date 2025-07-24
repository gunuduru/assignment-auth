package com.assignment.auth.dto

/**
 * 에러 응답 DTO
 */
data class ErrorResponse(
    val error: String,
    val message: String,
    val details: List<ValidationError>? = null
) 