package com.assignment.auth.repository

import com.assignment.auth.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * 사용자 데이터 접근을 위한 Repository 인터페이스
 * 중복 체크와 검색 기능을 제공
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {

    /**
     * 계정명으로 사용자 조회
     */
    fun findByUsername(username: String): Optional<User>

    /**
     * 주민등록번호로 사용자 조회
     */
    fun findBySsn(ssn: String): Optional<User>

    /**
     * 계정명 중복 체크
     */
    fun existsByUsername(username: String): Boolean

    /**
     * 주민등록번호 중복 체크
     */
    fun existsBySsn(ssn: String): Boolean

    /**
     * 계정명과 주민등록번호 동시 중복 체크
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username OR u.ssn = :ssn")
    fun existsByUsernameOrSsn(@Param("username") username: String, @Param("ssn") ssn: String): Boolean

    /**
     * 활성 상태별 사용자 조회
     */
    fun findByIsActive(isActive: Boolean): List<User>

    /**
     * 페이지네이션을 위한 전체 사용자 조회
     */
    fun findAllBy(pageable: Pageable): Page<User>

    /**
     * 활성 상태별 페이지네이션 조회
     */
    fun findByIsActive(isActive: Boolean, pageable: Pageable): Page<User>

    /**
     * 이름으로 사용자 검색 (부분 일치)
     */
    fun findByNameContainingIgnoreCase(name: String): List<User>

    /**
     * 활성 사용자 조회
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    fun findActiveUsers(): List<User>

    /**
     * 전체 사용자 수 조회
     */
    @Query("SELECT COUNT(u) FROM User u")
    fun countAllUsers(): Long

    /**
     * 활성 사용자 수 조회
     */
    fun countByIsActive(isActive: Boolean): Long
} 