package com.assignment.auth.dto

/**
 * API 공통 응답 형태
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val errors: List<ValidationError>? = null
) {
    companion object {
        /**
         * 성공 응답 생성
         */
        fun <T> success(message: String, data: T? = null): ApiResponse<T> {
            return ApiResponse(
                success = true,
                message = message,
                data = data
            )
        }

        /**
         * 실패 응답 생성
         */
        fun <T> failure(message: String, errors: List<ValidationError>? = null): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message,
                errors = errors
            )
        }
    }
}

/**
 * 유효성 검증 오류 정보
 */
data class ValidationError(
    val field: String,
    val message: String
) 