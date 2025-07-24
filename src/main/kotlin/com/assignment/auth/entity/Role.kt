package com.assignment.auth.entity

/**
 * 사용자 권한을 나타내는 열거형
 */
enum class Role(val description: String) {
    USER("일반 사용자"),
    ADMIN("관리자")
} 