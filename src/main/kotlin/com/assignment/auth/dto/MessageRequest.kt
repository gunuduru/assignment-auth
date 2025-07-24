package com.assignment.auth.dto

import jakarta.validation.constraints.*

/**
 * 연령대별 메시지 발송 요청 DTO
 */
data class MessageRequest(
    /**
     * 대상 연령대 (10, 20, 30, ..., 80)
     */
    @field:NotNull(message = "연령대는 필수입니다")
    @field:Min(value = 10, message = "연령대는 10 이상이어야 합니다")
    @field:Max(value = 80, message = "연령대는 80 이하여야 합니다")
    val ageGroup: Int,

    /**
     * 발송할 메시지 내용
     */
    @field:NotBlank(message = "메시지 내용은 필수입니다")
    @field:Size(min = 1, max = 1000, message = "메시지 내용은 1자 이상 1000자 이하여야 합니다")
    val message: String
) 