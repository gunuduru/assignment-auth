package com.assignment.auth.service

import com.assignment.auth.dto.UserRegisterRequest
import com.assignment.auth.dto.UserRegisterResponse
import com.assignment.auth.entity.Role
import com.assignment.auth.entity.User
import com.assignment.auth.exception.SsnAlreadyExistsException
import com.assignment.auth.exception.UsernameAlreadyExistsException
import com.assignment.auth.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * 회원가입 처리
     * - 계정명/주민등록번호 중복 체크
     * - 비밀번호 암호화
     * - 사용자 정보 저장
     */
    @Transactional
    fun registerUser(request: UserRegisterRequest): UserRegisterResponse {
        // 1. 중복 체크
        validateDuplicateUser(request.username, request.ssn)
        
        // 2. 비밀번호 암호화
        val encodedPassword = passwordEncoder.encode(request.password)
        
        // 3. User 엔티티 생성
        val user = User(
            username = request.username,
            password = encodedPassword,
            name = request.name,
            ssn = request.ssn,
            phoneNumber = request.phoneNumber,
            address = request.address,
            role = Role.USER // 기본적으로 일반 사용자로 등록
        )
        
        // 4. 데이터베이스 저장
        val savedUser = userRepository.save(user)
        
        // 5. 응답 DTO 반환
        return UserRegisterResponse.from(savedUser)
    }

    /**
     * 관리자 회원가입 처리 (향후 구현)
     */
    @Transactional
    fun registerAdmin(request: UserRegisterRequest): UserRegisterResponse {
        // 1. 중복 체크
        validateDuplicateUser(request.username, request.ssn)
        
        // 2. 비밀번호 암호화
        val encodedPassword = passwordEncoder.encode(request.password)
        
        // 3. Admin User 엔티티 생성
        val admin = User(
            username = request.username,
            password = encodedPassword,
            name = request.name,
            ssn = request.ssn,
            phoneNumber = request.phoneNumber,
            address = request.address,
            role = Role.ADMIN // 관리자로 등록
        )
        
        // 4. 데이터베이스 저장
        val savedAdmin = userRepository.save(admin)
        
        // 5. 응답 DTO 반환
        return UserRegisterResponse.from(savedAdmin)
    }

    /**
     * 계정명으로 사용자 조회
     */
    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username).orElse(null)
    }

    /**
     * 주민등록번호로 사용자 조회
     */
    fun findBySsn(ssn: String): User? {
        return userRepository.findBySsn(ssn).orElse(null)
    }

    /**
     * 계정명 중복 체크
     */
    fun isUsernameExists(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    /**
     * 주민등록번호 중복 체크
     */
    fun isSsnExists(ssn: String): Boolean {
        return userRepository.existsBySsn(ssn)
    }

    /**
     * 중복 사용자 검증
     * - 계정명과 주민등록번호 동시 체크
     */
    private fun validateDuplicateUser(username: String, ssn: String) {
        // 계정명 중복 체크
        if (userRepository.existsByUsername(username)) {
            throw UsernameAlreadyExistsException(username)
        }
        
        // 주민등록번호 중복 체크
        if (userRepository.existsBySsn(ssn)) {
            throw SsnAlreadyExistsException(ssn)
        }
    }

    /**
     * 전체 사용자 수 조회
     */
    fun getTotalUserCount(): Long {
        return userRepository.countAllUsers()
    }

    /**
     * 활성 사용자 수 조회
     */
    fun getActiveUserCount(): Long {
        return userRepository.countByIsActive(true)
    }

    /**
     * 역할별 사용자 수 조회
     */
    fun getUserCountByRole(role: Role): Long {
        return userRepository.countByRole(role)
    }
} 