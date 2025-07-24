package com.assignment.auth.dto

import java.time.LocalDateTime

/**
 * 사용자 프로필 조회 API 응답 DTO
 * 주소는 가장 큰 단위의 행정구역만 포함
 */
data class UserProfileResponse(
    /**
     * 사용자 ID
     */
    val id: Long,

    /**
     * 계정명
     */
    val account: String,

    /**
     * 사용자 이름
     */
    val name: String,

    /**
     * 주민등록번호 (마스킹 처리)
     */
    val ssn: String,

    /**
     * 휴대폰 번호
     */
    val phoneNumber: String,

    /**
     * 주소 (행정구역만)
     * 예: "서울특별시", "경기도", "강원특별자치도"
     */
    val administrativeRegion: String,

    /**
     * 계정 활성화 상태
     */
    val isActive: Boolean,

    /**
     * 계정 생성일시
     */
    val createdAt: LocalDateTime,

    /**
     * 마지막 수정일시
     */
    val updatedAt: LocalDateTime
) 