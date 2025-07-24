package com.assignment.auth.service

import com.assignment.auth.config.JwtConfig
import com.assignment.auth.dto.LoginRequest
import com.assignment.auth.dto.LoginResponse
import com.assignment.auth.dto.UserProfileResponse
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

        // 3. 모든 사용자는 활성 상태 (isActive 필드 제거됨)

        // 4. JWT 토큰 생성
        val accessToken = jwtUtil.generateToken(user.id, user.username)
        val expiresIn = jwtConfig.expiration / 1000 // 초 단위로 변환

        // 5. 로그인 응답 생성
        return LoginResponse.from(user, accessToken, expiresIn)
    }

    /**
     * 사용자 ID로 프로필 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자 프로필 정보
     * @throws LoginFailedException 사용자를 찾을 수 없을 때
     */
    fun getUserProfile(userId: Long): UserProfileResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { LoginFailedException("사용자를 찾을 수 없습니다.") }

        // 주소에서 행정구역만 추출
        val administrativeRegion = extractAdministrativeRegion(user.address)

        // 주민등록번호 마스킹 처리 (뒤 7자리를 *로 처리)
        val maskedSsn = maskSsn(user.ssn)

        return UserProfileResponse(
            id = user.id,
            account = user.username,
            name = user.name,
            ssn = maskedSsn,
            phoneNumber = user.phoneNumber,
            administrativeRegion = administrativeRegion,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
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

    /**
     * 주소에서 가장 큰 단위의 행정구역 추출
     * 예: "서울특별시 강남구 테헤란로 123" -> "서울특별시"
     * 예: "경기도 성남시 분당구 정자로 456" -> "경기도"
     * 
     * @param address 전체 주소
     * @return 행정구역 (시/도 단위)
     */
    private fun extractAdministrativeRegion(address: String): String {
        if (address.isBlank()) return ""

        // 공백으로 구분된 첫 번째 부분을 추출
        val firstPart = address.trim().split(" ")[0]

        // 시/도로 끝나는지 확인
        return if (firstPart.endsWith("시") || firstPart.endsWith("도")) {
            firstPart
        } else {
            // 혹시 공백 없이 붙어있는 경우를 대비해 "시" 또는 "도"까지 찾기
            val siIndex = address.indexOf("시")
            val doIndex = address.indexOf("도")
            
            when {
                siIndex > 0 && (doIndex == -1 || siIndex < doIndex) -> address.substring(0, siIndex + 1)
                doIndex > 0 && (siIndex == -1 || doIndex < siIndex) -> address.substring(0, doIndex + 1)
                else -> firstPart // 둘 다 없으면 첫 번째 부분 반환
            }
        }
    }

    /**
     * 주민등록번호 마스킹 처리
     * 형태: 123456-*******
     * 
     * @param ssn 원본 주민등록번호
     * @return 마스킹된 주민등록번호
     */
    private fun maskSsn(ssn: String): String {
        if (ssn.length != 14 || !ssn.contains("-")) return ssn
        
        val parts = ssn.split("-")
        if (parts.size != 2 || parts[0].length != 6 || parts[1].length != 7) return ssn
        
        return "${parts[0]}-*******"
    }
} 