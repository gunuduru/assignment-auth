package com.assignment.auth.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 로그인 요청 DTO
 */
data class LoginRequest(
    @field:NotBlank(message = "계정명은 필수입니다")
    @field:Size(min = 4, max = 20, message = "계정명은 4자 이상 20자 이하여야 합니다")
    val username: String,

    @field:NotBlank(message = "비밀번호는 필수입니다")
    val password: String
) 