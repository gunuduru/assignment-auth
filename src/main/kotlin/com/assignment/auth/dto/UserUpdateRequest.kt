package com.assignment.auth.dto

import jakarta.validation.constraints.*

/**
 * 사용자 정보 수정 요청 DTO (관리자용)
 * 비밀번호와 주소만 수정 가능
 */
data class UserUpdateRequest(
    @field:Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    @field:Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&].*$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    val password: String? = null,

    @field:Size(min = 10, max = 100, message = "주소는 10자 이상 100자 이하여야 합니다")
    val address: String? = null
) {
    /**
     * 수정할 항목이 있는지 확인
     */
    fun hasUpdates(): Boolean = password != null || address != null
} 