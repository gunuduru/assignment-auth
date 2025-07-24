package com.assignment.auth.controller

import com.assignment.auth.dto.ApiResponse
import com.assignment.auth.dto.LoginRequest
import com.assignment.auth.dto.LoginResponse
import com.assignment.auth.dto.UserRegisterRequest
import com.assignment.auth.dto.UserRegisterResponse
import com.assignment.auth.service.AuthService
import com.assignment.auth.service.UserService
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
    ): ResponseEntity<ApiResponse<UserRegisterResponse>> {
        val response = userService.registerUser(request)
        val apiResponse = ApiResponse.success(
            message = "회원가입이 완료되었습니다.",
            data = response
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse)
    }

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<ApiResponse<LoginResponse>> {
        val response = authService.login(request)
        val apiResponse = ApiResponse.success(
            message = "로그인이 완료되었습니다.",
            data = response
        )
        return ResponseEntity.ok(apiResponse)
    }
} 