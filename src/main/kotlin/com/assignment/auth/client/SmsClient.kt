package com.assignment.auth.client

import com.assignment.auth.dto.SmsResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

/**
 * SMS 메시지 API 클라이언트
 */
@Component
class SmsClient {

    private val logger = LoggerFactory.getLogger(SmsClient::class.java)
    
    private val webClient = WebClient.builder()
        .baseUrl("http://localhost:8082")
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + 
            Base64.getEncoder().encodeToString("autoever:5678".toByteArray()))
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .build()

    /**
     * SMS 메시지 발송
     * 
     * @param phone 수신자 전화번호 (xxx-xxxx-xxxx 형식)
     * @param message 발송할 메시지
     * @return HTTP 상태 코드
     */
    fun sendMessage(phone: String, message: String): HttpStatusCode {
        return try {
            val formData = LinkedMultiValueMap<String, String>()
            formData.add("message", message)
            
            val response = webClient.post()
                .uri("/sms?phone=$phone")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(SmsResponse::class.java)
                .block()

            val statusCode = if (response?.result == "OK") HttpStatus.OK else HttpStatus.INTERNAL_SERVER_ERROR
            
            logger.info("SMS 메시지 발송 완료: phone=$phone, status=$statusCode, result=${response?.result}")
            statusCode
            
        } catch (ex: WebClientResponseException) {
            val statusCode = ex.statusCode
            logger.warn("SMS 메시지 발송 실패: phone=$phone, status=$statusCode, error=${ex.message}")
            statusCode
            
        } catch (ex: Exception) {
            logger.error("SMS 메시지 발송 중 예외 발생: phone=$phone", ex)
            HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
} 