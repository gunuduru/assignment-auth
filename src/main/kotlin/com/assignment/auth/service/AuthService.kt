package com.assignment.auth.service

import com.assignment.auth.config.JwtConfig
import com.assignment.auth.dto.LoginRequest
import com.assignment.auth.dto.LoginResponse
import com.assignment.auth.exception.InactiveUserLoginException
import com.assignment.auth.exception.LoginFailedException
import com.assignment.auth.repository.UserRepository
import com.assignment.auth.util.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 인증 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val jwtConfig: JwtConfig
) {

    /**
     * 사용자 로그인 처리
     * 
     * @param request 로그인 요청 정보
     * @return 로그인 응답 (JWT 토큰 포함)
     * @throws LoginFailedException 로그인 실패 시
     * @throws InactiveUserLoginException 비활성화된 사용자 로그인 시도 시
     */
    @Transactional
    fun login(request: LoginRequest): LoginResponse {
        // 1. 사용자 조회
        val user = userRepository.findByUsername(request.username)
            .orElseThrow { LoginFailedException("계정명 또는 비밀번호가 올바르지 않습니다.") }

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw LoginFailedException("계정명 또는 비밀번호가 올바르지 않습니다.")
        }

        // 3. 활성 사용자 여부 확인
        if (!user.isActive) {
            throw InactiveUserLoginException("비활성화된 계정입니다. 관리자에게 문의하세요.")
        }

        // 4. JWT 토큰 생성
        val accessToken = jwtUtil.generateToken(user.id, user.username)
        val expiresIn = jwtConfig.expiration / 1000 // 초 단위로 변환

        // 5. 로그인 응답 생성
        return LoginResponse.from(user, accessToken, expiresIn)
    }

    /**
     * JWT 토큰으로부터 사용자 정보 조회
     * 
     * @param token JWT 토큰
     * @return 사용자 정보
     * @throws LoginFailedException 사용자를 찾을 수 없을 때
     */
    fun getUserFromToken(token: String): com.assignment.auth.entity.User {
        val username = jwtUtil.getUsernameFromToken(token)
        return userRepository.findByUsername(username)
            .orElseThrow { LoginFailedException("사용자를 찾을 수 없습니다.") }
    }

    /**
     * JWT 토큰 유효성 검증
     * 
     * @param token JWT 토큰
     * @return 유효성 여부
     */
    fun validateToken(token: String): Boolean {
        return jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)
    }
} 