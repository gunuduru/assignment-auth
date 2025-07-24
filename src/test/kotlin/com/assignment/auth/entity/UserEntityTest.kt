package com.assignment.auth.entity

import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * User 엔티티 단위 테스트
 */
class UserEntityTest {

    private lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `유효한 사용자 정보로 User 생성 테스트`() {
        // Given
        val user = User(
            username = "testuser123",
            password = "password123!",
            name = "홍길동",
            ssn = "12345678901",
            phoneNumber = "01012345678",
            address = "서울특별시 강남구 테헤란로 123번길 45",
            role = Role.USER
        )

        // When
        val violations: Set<ConstraintViolation<User>> = validator.validate(user)

        // Then
        assertTrue(violations.isEmpty(), "유효한 사용자 정보에는 검증 오류가 없어야 합니다")
        assertEquals("testuser123", user.username)
        assertEquals("홍길동", user.name)
        assertEquals(Role.USER, user.role)
        assertFalse(user.isAdmin())
        assertTrue(user.isActiveUser())
    }

    @Test
    fun `관리자 사용자 생성 및 권한 확인 테스트`() {
        // Given
        val admin = User(
            username = "admin123",
            password = "adminpass123!",
            name = "관리자",
            ssn = "99999999999",
            phoneNumber = "01099999999",
            address = "서울특별시 중구 세종대로 110",
            role = Role.ADMIN
        )

        // When & Then
        assertEquals(Role.ADMIN, admin.role)
        assertTrue(admin.isAdmin())
        assertTrue(admin.isActiveUser())
    }

    @Test
    fun `잘못된 계정명으로 User 생성 시 유효성 검증 실패`() {
        // Given - 계정명이 너무 짧음
        val userWithShortUsername = User(
            username = "abc", // 4자 미만
            password = "password123!",
            name = "홍길동",
            ssn = "12345678901",
            phoneNumber = "01012345678",
            address = "서울특별시 강남구 테헤란로 123번길 45"
        )

        // When
        val violations = validator.validate(userWithShortUsername)

        // Then
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "username" })
    }

    @Test
    fun `잘못된 주민등록번호로 User 생성 시 유효성 검증 실패`() {
        // Given - 주민등록번호가 11자리가 아님
        val userWithInvalidSsn = User(
            username = "testuser123",
            password = "password123!",
            name = "홍길동",
            ssn = "123456789", // 11자리가 아님
            phoneNumber = "01012345678",
            address = "서울특별시 강남구 테헤란로 123번길 45"
        )

        // When
        val violations = validator.validate(userWithInvalidSsn)

        // Then
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "ssn" })
    }

    @Test
    fun `잘못된 핸드폰번호로 User 생성 시 유효성 검증 실패`() {
        // Given - 핸드폰번호가 11자리가 아님
        val userWithInvalidPhone = User(
            username = "testuser123",
            password = "password123!",
            name = "홍길동",
            ssn = "12345678901",
            phoneNumber = "010123456", // 11자리가 아님
            address = "서울특별시 강남구 테헤란로 123번길 45"
        )

        // When
        val violations = validator.validate(userWithInvalidPhone)

        // Then
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "phoneNumber" })
    }

    @Test
    fun `비어있는 필수 필드로 User 생성 시 유효성 검증 실패`() {
        // Given - 이름이 비어있음
        val userWithEmptyName = User(
            username = "testuser123",
            password = "password123!",
            name = "", // 비어있음
            ssn = "12345678901",
            phoneNumber = "01012345678",
            address = "서울특별시 강남구 테헤란로 123번길 45"
        )

        // When
        val violations = validator.validate(userWithEmptyName)

        // Then
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `User equals 및 hashCode 테스트`() {
        // Given
        val user1 = User(
            id = 1L,
            username = "testuser123",
            password = "password123!",
            name = "홍길동",
            ssn = "12345678901",
            phoneNumber = "01012345678",
            address = "서울특별시 강남구 테헤란로 123번길 45"
        )

        val user2 = User(
            id = 1L,
            username = "testuser123",
            password = "differentpass",
            name = "다른이름",
            ssn = "12345678901",
            phoneNumber = "01099999999",
            address = "다른주소"
        )

        val user3 = User(
            id = 2L,
            username = "otheruser",
            password = "password123!",
            name = "김영희",
            ssn = "98765432109",
            phoneNumber = "01087654321",
            address = "부산광역시 해운대구"
        )

        // When & Then
        assertEquals(user1, user2) // 같은 id, username, ssn이면 같은 객체
        assertEquals(user1.hashCode(), user2.hashCode())
        assertFalse(user1.equals(user3)) // 다른 사용자
    }
} 