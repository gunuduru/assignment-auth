package com.assignment.auth.controller

import com.assignment.auth.dto.*
import com.assignment.auth.service.AdminService
import com.assignment.auth.service.MessageService
import jakarta.validation.Valid
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
    private val adminService: AdminService,
    private val messageService: MessageService
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
    ): ResponseEntity<UserListResponse> {
        
        val sortDirection = if (direction.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))
        
        val usersPage = adminService.getAllUsers(pageable)
        val response = UserListResponse.from(usersPage)
        
        return ResponseEntity.ok(response)
    }



    /**
     * 특정 사용자 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    @GetMapping("/users/{userId}")
    fun getUserById(@PathVariable userId: Long): ResponseEntity<UserResponse> {
        
        val user = adminService.getUserById(userId)
        
        return ResponseEntity.ok(user)
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
    ): ResponseEntity<UserResponse> {
        
        val updatedUser = adminService.updateUser(userId, request)
        
        return ResponseEntity.ok(updatedUser)
    }

    /**
     * 사용자 삭제 (하드 삭제)
     * 
     * @param userId 사용자 ID
     */
    @DeleteMapping("/users/{userId}")
    fun deleteUser(@PathVariable userId: Long): ResponseEntity<Void> {
        
        adminService.deleteUser(userId)
        
        return ResponseEntity.noContent().build()
    }





    /**
     * 연령대별 카카오톡 메시지 발송 스케줄링
     * 
     * @param request 메시지 발송 요청 (연령대, 메시지 내용)
     * @return 메시지 발송 스케줄링 결과
     */
    @PostMapping("/messages/age-group")
    fun sendAgeGroupMessage(
        @Valid @RequestBody request: MessageRequest
    ): ResponseEntity<MessageResponse> {
        
        val response = messageService.scheduleAgeGroupMessage(request)
        
        return ResponseEntity.ok(response)
    }

    /**
     * 현재 대기 중인 메시지 수 조회
     * 
     * @return 대기 중인 메시지 통계
     */
    @GetMapping("/messages/pending")
    fun getPendingMessageCount(): ResponseEntity<PendingMessageResponse> {
        
        val pendingCount = messageService.getPendingMessageCount()
        val response = PendingMessageResponse(pendingCount)
        
        return ResponseEntity.ok(response)
    }
} 