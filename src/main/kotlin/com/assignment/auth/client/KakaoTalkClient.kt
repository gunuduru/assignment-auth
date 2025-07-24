package com.assignment.auth.client

import com.assignment.auth.dto.KakaoTalkMessageRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

/**
 * 카카오톡 메시지 API 클라이언트
 */
@Component
class KakaoTalkClient {

    private val logger = LoggerFactory.getLogger(KakaoTalkClient::class.java)
    
    private val webClient = WebClient.builder()
        .baseUrl("http://localhost:8081")
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + 
            Base64.getEncoder().encodeToString("autoever:1234".toByteArray()))
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    /**
     * 카카오톡 메시지 발송
     * 
     * @param phone 수신자 전화번호 (xxx-xxxx-xxxx 형식)
     * @param message 발송할 메시지
     * @return HTTP 상태 코드
     */
    fun sendMessage(phone: String, message: String): HttpStatusCode {
        return try {
            val request = KakaoTalkMessageRequest(phone, message)
            
            val response = webClient.post()
                .uri("/kakaotalk-messages")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block()

            val statusCode = response?.statusCode ?: HttpStatus.INTERNAL_SERVER_ERROR
            
            logger.info("카카오톡 메시지 발송 완료: phone=$phone, status=$statusCode")
            statusCode
            
        } catch (ex: WebClientResponseException) {
            val statusCode = ex.statusCode
            logger.warn("카카오톡 메시지 발송 실패: phone=$phone, status=$statusCode, error=${ex.message}")
            statusCode
            
        } catch (ex: Exception) {
            logger.error("카카오톡 메시지 발송 중 예외 발생: phone=$phone", ex)
            HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
} 