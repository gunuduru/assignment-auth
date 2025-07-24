package com.assignment.auth.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * 예약된 메시지 엔티티
 * 연령대별 메시지 발송을 위한 스케줄링 데이터
 */
@Entity
@Table(name = "scheduled_messages")
@EntityListeners(AuditingEntityListener::class)
data class ScheduledMessage(
    /**
     * 메시지 ID (자동 증가)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduled_message_seq")
    @SequenceGenerator(name = "scheduled_message_seq", sequenceName = "scheduled_message_seq", allocationSize = 1)
    val id: Long = 0,

    /**
     * 수신자 전화번호 (xxx-xxxx-xxxx 형식)
     */
    @Column(nullable = false, length = 13)
    val phone: String,

    /**
     * 발송할 메시지 내용
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    val message: String,

    /**
     * 생성일시
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * JPA 기본 생성자
     */
    constructor() : this(0, "", "", LocalDateTime.now())
} 