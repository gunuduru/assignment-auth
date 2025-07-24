package com.assignment.auth.controller

import com.assignment.auth.dto.UserRegisterRequest
import com.assignment.auth.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

/**
 * AuthController 통합 테스트
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("dev") // H2 테스트 데이터베이스 사용
@Transactional
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        // 각 테스트 전에 데이터베이스 정리
        userRepository.deleteAll()
    }

    @Test
    fun `회원가입 성공 테스트`() {
        // Given
        val request = UserRegisterRequest(
            username = "testuser123",
            password = "password123!",
            name = "홍길동",
            ssn = "123456-1234567",
            phoneNumber = "01012345678",
            address = "서울특별시 강남구 테헤란로 123번길 45"
        )

        // When & Then
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
            .andExpect(jsonPath("$.data.username").value("testuser123"))
            .andExpect(jsonPath("$.data.name").value("홍길동"))
            .andExpect(jsonPath("$.data.id").exists())
            .andExpect(jsonPath("$.data.createdAt").exists())

        // 데이터베이스 확인
        val savedUser = userRepository.findByUsername("testuser123")
        assert(savedUser.isPresent)
        assert(savedUser.get().isActive)
    }

    @Test
    fun `계정명 중복으로 회원가입 실패 테스트`() {
        // Given - 먼저 사용자 등록
        val firstRequest = UserRegisterRequest(
            username = "testuser123",
            password = "password123!",
            name = "홍길동",
            ssn = "123456-1234567",
            phoneNumber = "01012345678",
            address = "서울특별시 강남구 테헤란로 123번길 45"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest))
        )

        // When - 같은 계정명으로 다시 등록 시도
        val duplicateRequest = UserRegisterRequest(
            username = "testuser123", // 중복 계정명
            password = "newpassword123!",
            name = "김영희",
            ssn = "987654-3210987", // 다른 주민번호
            phoneNumber = "01087654321",
            address = "부산광역시 해운대구"
        )

        // Then
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("회원가입에 실패했습니다."))
            .andExpect(jsonPath("$.errors[0].field").value("username"))
            .andExpect(jsonPath("$.errors[0].message").value("계정명 'testuser123'은 이미 사용 중입니다."))
    }

    @Test
    fun `주민등록번호 중복으로 회원가입 실패 테스트`() {
        // Given - 먼저 사용자 등록
        val firstRequest = UserRegisterRequest(
            username = "testuser123",
            password = "password123!",
            name = "홍길동",
            ssn = "123456-1234567",
            phoneNumber = "01012345678",
            address = "서울특별시 강남구 테헤란로 123번길 45"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest))
        )

        // When - 같은 주민번호로 다시 등록 시도
        val duplicateRequest = UserRegisterRequest(
            username = "newuser456", // 다른 계정명
            password = "newpassword123!",
            name = "김영희",
            ssn = "123456-1234567", // 중복 주민번호
            phoneNumber = "01087654321",
            address = "부산광역시 해운대구"
        )

        // Then
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errors[0].field").value("ssn"))
    }

    @Test
    fun `유효성 검증 실패 테스트 - 잘못된 계정명`() {
        // Given - 계정명이 너무 짧음
        val request = UserRegisterRequest(
            username = "abc", // 4자 미만
            password = "password123!",
            name = "홍길동",
            ssn = "123456-1234567",
            phoneNumber = "01012345678",
            address = "서울특별시 강남구 테헤란로 123번길 45"
        )

        // When & Then
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
            .andExpect(jsonPath("$.errors").isArray)
    }
} 