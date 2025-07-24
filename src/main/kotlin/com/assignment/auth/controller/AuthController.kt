package com.assignment.auth.controller

import com.assignment.auth.dto.ApiResponse
import com.assignment.auth.dto.UserRegisterRequest
import com.assignment.auth.dto.UserRegisterResponse
import com.assignment.auth.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 인증 관련 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService
) {

    /**
     * 일반 사용자 회원가입 API
     * 
     * @param request 회원가입 요청 정보
     * @return 회원가입 성공 응답
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
} 