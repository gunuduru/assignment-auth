package com.assignment.auth.service

import com.assignment.auth.dto.UserResponse
import com.assignment.auth.dto.UserUpdateRequest
import com.assignment.auth.entity.User
import com.assignment.auth.exception.UserNotFoundException
import com.assignment.auth.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 관리자 기능을 처리하는 서비스
 */
@Service
@Transactional(readOnly = true)
class AdminService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * 전체 사용자 목록 조회 (페이지네이션)
     */
    fun getAllUsers(pageable: Pageable): Page<UserResponse> {
        return userRepository.findAllBy(pageable)
            .map { UserResponse.from(it) }
    }

    /**
     * 활성 사용자 목록 조회 (페이지네이션)
     */
    fun getActiveUsers(pageable: Pageable): Page<UserResponse> {
        return userRepository.findByIsActive(true, pageable)
            .map { UserResponse.from(it) }
    }

    /**
     * 사용자 ID로 조회
     */
    fun getUserById(userId: Long): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("ID가 $userId 인 사용자를 찾을 수 없습니다.") }
        
        return UserResponse.from(user)
    }

    /**
     * 사용자 정보 수정 (비밀번호, 주소만)
     */
    @Transactional
    fun updateUser(userId: Long, request: UserUpdateRequest): UserResponse {
        if (!request.hasUpdates()) {
            throw IllegalArgumentException("수정할 정보가 없습니다.")
        }

        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("ID가 $userId 인 사용자를 찾을 수 없습니다.") }

        // 수정 가능한 필드만 업데이트
        val updatedUser = updateUserFields(user, request)
        
        val savedUser = userRepository.save(updatedUser)
        return UserResponse.from(savedUser)
    }

    /**
     * 사용자 삭제 (소프트 삭제 - isActive를 false로 변경)
     */
    @Transactional
    fun deleteUser(userId: Long): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("ID가 $userId 인 사용자를 찾을 수 없습니다.") }

        if (!user.isActive) {
            throw IllegalStateException("이미 비활성화된 사용자입니다.")
        }

        // 소프트 삭제 처리
        val deactivatedUser = user.copy(
            isActive = false,
            updatedAt = LocalDateTime.now()
        )

        val savedUser = userRepository.save(deactivatedUser)
        return UserResponse.from(savedUser)
    }

    /**
     * 사용자 활성화
     */
    @Transactional
    fun activateUser(userId: Long): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("ID가 $userId 인 사용자를 찾을 수 없습니다.") }

        if (user.isActive) {
            throw IllegalStateException("이미 활성화된 사용자입니다.")
        }

        val activatedUser = user.copy(
            isActive = true,
            updatedAt = LocalDateTime.now()
        )

        val savedUser = userRepository.save(activatedUser)
        return UserResponse.from(savedUser)
    }

    /**
     * 사용자 정보 업데이트 헬퍼 메소드
     */
    private fun updateUserFields(user: User, request: UserUpdateRequest): User {
        return user.copy(
            password = request.password?.let { passwordEncoder.encode(it) } ?: user.password,
            address = request.address ?: user.address,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 통계 정보 조회
     */
    fun getUserStatistics(): Map<String, Long> {
        return mapOf(
            "totalUsers" to userRepository.countAllUsers(),
            "activeUsers" to userRepository.countByIsActive(true),
            "inactiveUsers" to userRepository.countByIsActive(false)
        )
    }
} 