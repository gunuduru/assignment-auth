package com.assignment.auth.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.*

/**
 * 회원가입 요청 DTO
 */
data class UserRegisterRequest(
    @field:NotBlank(message = "계정명은 필수입니다")
    @field:Size(min = 4, max = 20, message = "계정명은 4자 이상 20자 이하여야 합니다")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9]+$",
        message = "계정명은 영문과 숫자만 사용 가능합니다"
    )
    @JsonAlias("account") // account 필드명도 허용
    val username: String,

    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    @field:Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&].*$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    val password: String,

    @field:NotBlank(message = "이름은 필수입니다")
    @field:Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하여야 합니다")
    val name: String,

    @field:NotBlank(message = "주민등록번호는 필수입니다")
    @field:Pattern(
        regexp = "^[0-9]{6}-[0-9]{7}$",
        message = "주민등록번호는 6자리-7자리 형태여야 합니다 (예: 123456-1234567)"
    )
    val ssn: String,

    @field:NotBlank(message = "휴대폰번호는 필수입니다")
    @field:Pattern(
        regexp = "^[0-9]{3}-[0-9]{4}-[0-9]{4}$",
        message = "휴대폰번호는 xxx-xxxx-xxxx 형태여야 합니다 (예: 010-1234-5678)"
    )
    val phoneNumber: String,

    @field:NotBlank(message = "주소는 필수입니다")
    @field:Size(min = 10, max = 100, message = "주소는 10자 이상 100자 이하여야 합니다")
    val address: String
) 