package com.assignment.auth.controller

import com.assignment.auth.dto.ApiResponse
import com.assignment.auth.dto.UserResponse
import com.assignment.auth.dto.UserUpdateRequest
import com.assignment.auth.service.AdminService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 관리자 전용 REST API 컨트롤러
 * Basic Authentication (admin/1212) 필요
 */
@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val adminService: AdminService
) {

    /**
     * 전체 사용자 목록 조회 (페이지네이션)
     * 
     * @param page 페이지 번호 (0부터 시작, 기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @param sort 정렬 기준 (기본값: id,asc)
     * @return 페이지네이션된 사용자 목록
     */
    @GetMapping("/users")
    fun getAllUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String
    ): ResponseEntity<ApiResponse<Page<UserResponse>>> {
        
        val sortDirection = if (direction.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))
        
        val users = adminService.getAllUsers(pageable)
        
        val apiResponse = ApiResponse.success(
            message = "사용자 목록을 성공적으로 조회했습니다.",
            data = users
        )
        
        return ResponseEntity.ok(apiResponse)
    }

    /**
     * 활성 사용자 목록 조회 (페이지네이션)
     * 
     * @param page 페이지 번호 (0부터 시작, 기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @param sort 정렬 기준 (기본값: id,asc)
     * @return 페이지네이션된 활성 사용자 목록
     */
    @GetMapping("/users/active")
    fun getActiveUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") direction: String
    ): ResponseEntity<ApiResponse<Page<UserResponse>>> {
        
        val sortDirection = if (direction.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))
        
        val users = adminService.getActiveUsers(pageable)
        
        val apiResponse = ApiResponse.success(
            message = "활성 사용자 목록을 성공적으로 조회했습니다.",
            data = users
        )
        
        return ResponseEntity.ok(apiResponse)
    }

    /**
     * 특정 사용자 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    @GetMapping("/users/{userId}")
    fun getUserById(@PathVariable userId: Long): ResponseEntity<ApiResponse<UserResponse>> {
        
        val user = adminService.getUserById(userId)
        
        val apiResponse = ApiResponse.success(
            message = "사용자 정보를 성공적으로 조회했습니다.",
            data = user
        )
        
        return ResponseEntity.ok(apiResponse)
    }

    /**
     * 사용자 정보 수정 (비밀번호, 주소만)
     * 
     * @param userId 사용자 ID
     * @param request 수정할 정보 (비밀번호, 주소)
     * @return 수정된 사용자 정보
     */
    @PutMapping("/users/{userId}")
    fun updateUser(
        @PathVariable userId: Long,
        @Valid @RequestBody request: UserUpdateRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        
        val updatedUser = adminService.updateUser(userId, request)
        
        val apiResponse = ApiResponse.success(
            message = "사용자 정보를 성공적으로 수정했습니다.",
            data = updatedUser
        )
        
        return ResponseEntity.ok(apiResponse)
    }

    /**
     * 사용자 삭제 (소프트 삭제 - 비활성화)
     * 
     * @param userId 사용자 ID
     * @return 비활성화된 사용자 정보
     */
    @DeleteMapping("/users/{userId}")
    fun deleteUser(@PathVariable userId: Long): ResponseEntity<ApiResponse<UserResponse>> {
        
        val deletedUser = adminService.deleteUser(userId)
        
        val apiResponse = ApiResponse.success(
            message = "사용자를 성공적으로 비활성화했습니다.",
            data = deletedUser
        )
        
        return ResponseEntity.ok(apiResponse)
    }

    /**
     * 사용자 활성화
     * 
     * @param userId 사용자 ID
     * @return 활성화된 사용자 정보
     */
    @PostMapping("/users/{userId}/activate")
    fun activateUser(@PathVariable userId: Long): ResponseEntity<ApiResponse<UserResponse>> {
        
        val activatedUser = adminService.activateUser(userId)
        
        val apiResponse = ApiResponse.success(
            message = "사용자를 성공적으로 활성화했습니다.",
            data = activatedUser
        )
        
        return ResponseEntity.ok(apiResponse)
    }

    /**
     * 사용자 통계 정보 조회
     * 
     * @return 전체/활성/비활성 사용자 수 통계
     */
    @GetMapping("/statistics")
    fun getUserStatistics(): ResponseEntity<ApiResponse<Map<String, Long>>> {
        
        val statistics = adminService.getUserStatistics()
        
        val apiResponse = ApiResponse.success(
            message = "사용자 통계를 성공적으로 조회했습니다.",
            data = statistics
        )
        
        return ResponseEntity.ok(apiResponse)
    }
} 