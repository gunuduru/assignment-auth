package com.assignment.auth.controller

import com.assignment.auth.dto.*
import com.assignment.auth.service.AuthService
import com.assignment.auth.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService,
    private val authService: AuthService
) {
    
    /**
     * 테스트 엔드포인트
     */
    @GetMapping("/test")
    fun test(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("message" to "AuthController is working"))
    }
    
    /**
     * 회원가입 API
     */
    @PostMapping("/register")
    fun registerUser(
        @Valid @RequestBody request: UserRegisterRequest
    ): ResponseEntity<UserRegisterResponse> {
        val response = userService.registerUser(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<LoginResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    /**
     * 로그인한 사용자의 프로필 조회 API
     * JWT 토큰을 통해 인증된 사용자만 접근 가능
     */
    @GetMapping("/profile")
    fun getUserProfile(
        request: HttpServletRequest
    ): ResponseEntity<UserProfileResponse> {
        // JwtAuthenticationFilter에서 설정한 사용자 ID 추출
        val userId = request.getAttribute("userId") as? Long
            ?: throw IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.")

        val profile = authService.getUserProfile(userId)
        return ResponseEntity.ok(profile)
    }
} 