package com.assignment.auth.scheduler

import com.assignment.auth.client.KakaoTalkClient
import com.assignment.auth.client.SmsClient
import com.assignment.auth.repository.ScheduledMessageRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 메시지 발송 스케줄러
 * 1분마다 실행되어 대기 중인 메시지를 처리
 */
@Component
class MessageScheduler(
    private val scheduledMessageRepository: ScheduledMessageRepository,
    private val kakaoTalkClient: KakaoTalkClient,
    private val smsClient: SmsClient
) {

    private val logger = LoggerFactory.getLogger(MessageScheduler::class.java)

    // 카카오톡 분당 제한: 100건
    private val kakaoTalkRateLimit = 100
    // SMS 분당 제한: 500건
    private val smsRateLimit = 500

    /**
     * 1분마다 실행되는 메시지 발송 스케줄러
     * - 카카오톡 우선 시도 (분당 100건 제한)
     * - 실패 시 SMS로 fallback (분당 500건 제한)
     */
    @Scheduled(fixedRate = 60000) // 1분(60초)마다 실행
    @Transactional
    fun processScheduledMessages() {
        logger.info("메시지 발송 스케줄러 시작")

        try {
            // ID 기준 오름차순으로 500개 조회 (SMS 분당 제한에 맞춤)
            val pageable = PageRequest.of(0, smsRateLimit)
            val pendingMessages = scheduledMessageRepository.findOldestMessages(pageable)

            if (pendingMessages.isEmpty()) {
                logger.info("발송 대기 중인 메시지가 없습니다.")
                return
            }

            logger.info("발송 대기 메시지 ${pendingMessages.size}개 처리 시작")

            var kakaoTalkCount = 0
            var smsCount = 0
            var processedCount = 0

            for (message in pendingMessages) {
                try {
                    var messageProcessed = false

                    // 1. 카카오톡 분당 제한 확인 후 시도
                    if (kakaoTalkCount < kakaoTalkRateLimit) {
                        val kakaoStatus = kakaoTalkClient.sendMessage(message.phone, message.message)
                        kakaoTalkCount++

                        when (kakaoStatus) {
                            HttpStatus.OK -> {
                                // 카카오톡 발송 성공 → 메시지 삭제
                                scheduledMessageRepository.delete(message)
                                processedCount++
                                messageProcessed = true
                                logger.debug("카카오톡 발송 성공: phone=${message.phone}")
                            }
                            HttpStatus.INTERNAL_SERVER_ERROR -> {
                                // 500 에러 → SMS로 fallback 시도
                                logger.warn("카카오톡 발송 실패 (500), SMS로 전환: phone=${message.phone}")
                            }
                            HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED -> {
                                // 400, 401 에러 → 에러 로그만 기록
                                logger.error("카카오톡 발송 에러 (${kakaoStatus.value()}): phone=${message.phone}")
                                messageProcessed = true // 재시도 하지 않음
                            }
                            else -> {
                                logger.warn("카카오톡 발송 예상치 못한 응답 (${kakaoStatus.value()}): phone=${message.phone}")
                            }
                        }
                    }

                    // 2. 카카오톡 실패 또는 제한 초과 시 SMS 시도
                    if (!messageProcessed) {
                        if (smsCount >= smsRateLimit) {
                            // SMS 분당 제한 초과 → 반복문 종료
                            logger.warn("SMS 분당 제한 초과 (${smsRateLimit}건), 스케줄러 종료")
                            break
                        }

                        val smsStatus = smsClient.sendMessage(message.phone, message.message)
                        smsCount++

                        when (smsStatus) {
                            HttpStatus.OK -> {
                                // SMS 발송 성공 → 메시지 삭제
                                scheduledMessageRepository.delete(message)
                                processedCount++
                                logger.debug("SMS 발송 성공: phone=${message.phone}")
                            }
                            HttpStatus.INTERNAL_SERVER_ERROR -> {
                                // 500 에러 → 분당 제한 초과로 간주하고 종료
                                logger.warn("SMS 발송 실패 (500), 분당 제한 초과로 간주하여 종료: phone=${message.phone}")
                                break
                            }
                            HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED -> {
                                // 400, 401 에러 → 에러 로그만 기록하고 메시지 삭제
                                logger.error("SMS 발송 에러 (${smsStatus.value()}): phone=${message.phone}")
                                scheduledMessageRepository.delete(message)
                                processedCount++
                            }
                            else -> {
                                logger.warn("SMS 발송 예상치 못한 응답 (${smsStatus.value()}): phone=${message.phone}")
                            }
                        }
                    }

                } catch (e: Exception) {
                    logger.error("메시지 처리 중 예외 발생: messageId=${message.id}, phone=${message.phone}", e)
                }
            }

            val remainingCount = scheduledMessageRepository.countBy()
            logger.info("메시지 발송 스케줄러 완료: 처리=${processedCount}개, 카카오톡=${kakaoTalkCount}개, SMS=${smsCount}개, 남은 대기=${remainingCount}개")

        } catch (e: Exception) {
            logger.error("메시지 발송 스케줄러 실행 중 예외 발생", e)
        }
    }
} 