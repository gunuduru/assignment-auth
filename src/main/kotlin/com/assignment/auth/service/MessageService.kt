package com.assignment.auth.service

import com.assignment.auth.dto.MessageRequest
import com.assignment.auth.dto.MessageResponse
import com.assignment.auth.entity.ScheduledMessage
import com.assignment.auth.exception.InvalidAgeGroupException
import com.assignment.auth.repository.ScheduledMessageRepository
import com.assignment.auth.repository.UserRepository
import com.assignment.auth.util.AgeUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 메시지 발송 관련 서비스
 */
@Service
@Transactional(readOnly = true)
class MessageService(
    private val userRepository: UserRepository,
    private val scheduledMessageRepository: ScheduledMessageRepository
) {

    private val logger = LoggerFactory.getLogger(MessageService::class.java)

    /**
     * 연령대별 메시지 발송 스케줄링
     * 
     * @param request 메시지 발송 요청
     * @return 메시지 발송 응답
     */
    @Transactional
    fun scheduleAgeGroupMessage(request: MessageRequest): MessageResponse {
        logger.info("연령대별 메시지 발송 스케줄링 시작: ageGroup=${request.ageGroup}")

        // 0. 연령대 유효성 검증 (10의 배수인지 확인)
        if (request.ageGroup % 10 != 0 || request.ageGroup < 10 || request.ageGroup > 80) {
            throw InvalidAgeGroupException("연령대는 10, 20, 30, 40, 50, 60, 70, 80 중 하나여야 합니다.")
        }

        // 1. 해당 연령대의 활성 사용자 조회
        val allUsers = userRepository.findActiveUsers()
        val targetUsers = allUsers.filter { user ->
            try {
                AgeUtil.isInAgeGroup(user.ssn, request.ageGroup)
            } catch (e: Exception) {
                logger.warn("사용자 연령 계산 실패: userId=${user.id}, ssn=${user.ssn}", e)
                false
            }
        }

        logger.info("대상 사용자 조회 완료: 총 ${allUsers.size}명 중 ${targetUsers.size}명이 ${request.ageGroup}대")

        // 2. 각 사용자별로 개인화된 메시지 생성 및 스케줄링
        val scheduledMessages = targetUsers.map { user ->
            val personalizedMessage = createPersonalizedMessage(user.name, request.message)
            ScheduledMessage(
                phone = user.phoneNumber,
                message = personalizedMessage
            )
        }

        // 3. 스케줄링된 메시지들을 DB에 저장
        val savedMessages = scheduledMessageRepository.saveAll(scheduledMessages)
        
        logger.info("메시지 스케줄링 완료: ${savedMessages.size}개 메시지가 등록됨")

        // 4. 대기열 크기에 따른 예상 시작 시간 계산
        val totalQueueSize = scheduledMessageRepository.countBy()
        val estimatedMinutes = calculateEstimatedStartTime(totalQueueSize, savedMessages.size)

        return MessageResponse(
            ageGroup = request.ageGroup,
            targetUserCount = targetUsers.size,
            scheduledMessageCount = savedMessages.size,
            estimatedStartTime = estimatedMinutes
        )
    }

    /**
     * 개인화된 메시지 생성
     * 제목: "[회원 성명]님, 안녕하세요. 현대 오토에버입니다."
     * 
     * @param userName 사용자 이름
     * @param messageContent 메시지 내용
     * @return 개인화된 메시지
     */
    private fun createPersonalizedMessage(userName: String, messageContent: String): String {
        val title = "${userName}님, 안녕하세요. 현대 오토에버입니다."
        return "$title\n\n$messageContent"
    }

    /**
     * 예상 발송 시작 시간 계산
     * 카카오톡: 분당 100건, SMS: 분당 500건 제한 고려
     * 
     * @param currentQueueSize 현재 대기열 크기
     * @param newMessageCount 새로 추가된 메시지 수
     * @return 예상 시작 시간 (분 단위 문자열)
     */
    private fun calculateEstimatedStartTime(currentQueueSize: Long, newMessageCount: Int): String {
        // 카카오톡을 우선 시도하고 실패 시 SMS로 fallback하므로 
        // 평균적으로 분당 약 400-500건 정도 처리 가능할 것으로 예상
        val averageProcessingPerMinute = 450
        
        val estimatedMinutes = (currentQueueSize - newMessageCount) / averageProcessingPerMinute
        
        return when {
            estimatedMinutes <= 0 -> "즉시 시작"
            estimatedMinutes == 1L -> "약 1분 후"
            estimatedMinutes < 60 -> "약 ${estimatedMinutes}분 후"
            else -> {
                val hours = estimatedMinutes / 60
                val minutes = estimatedMinutes % 60
                if (minutes == 0L) "약 ${hours}시간 후"
                else "약 ${hours}시간 ${minutes}분 후"
            }
        }
    }

    /**
     * 현재 대기 중인 메시지 통계 조회
     * 
     * @return 대기 중인 메시지 수
     */
    fun getPendingMessageCount(): Long {
        return scheduledMessageRepository.countBy()
    }
} 