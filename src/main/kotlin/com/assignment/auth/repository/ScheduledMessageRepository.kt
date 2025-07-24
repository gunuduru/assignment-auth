package com.assignment.auth.repository

import com.assignment.auth.entity.ScheduledMessage
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * 예약된 메시지 Repository
 */
@Repository
interface ScheduledMessageRepository : JpaRepository<ScheduledMessage, Long> {

    /**
     * ID 기준 오름차순으로 정렬하여 제한된 개수만큼 조회
     * 스케줄러에서 사용 (가장 오래된 메시지부터 처리)
     * 
     * @param pageable 페이지 설정 (주로 Limit 용도)
     * @return 예약된 메시지 목록
     */
    @Query("SELECT sm FROM ScheduledMessage sm ORDER BY sm.id ASC")
    fun findOldestMessages(pageable: Pageable): List<ScheduledMessage>

    /**
     * 전체 예약된 메시지 개수 조회
     * 
     * @return 대기 중인 메시지 개수
     */
    fun countBy(): Long
} 