package com.assignment.auth.exception

import com.assignment.auth.dto.ErrorResponse
import com.assignment.auth.dto.ValidationError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 전역 예외 처리기
 * API 오류 응답을 통일된 형태로 처리
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * 유효성 검증 실패 예외 처리
     * (@Valid 어노테이션으로 인한 검증 실패)
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<ErrorResponse> {
        
        val errors = ex.bindingResult.fieldErrors.map { fieldError ->
            ValidationError(
                field = fieldError.field,
                message = fieldError.defaultMessage ?: "유효하지 않은 값입니다."
            )
        }
        
        val response = ErrorResponse(
            error = "VALIDATION_ERROR",
            message = "입력값이 올바르지 않습니다.",
            details = errors
        )
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    /**
     * 계정명 중복 예외 처리
     */
    @ExceptionHandler(UsernameAlreadyExistsException::class)
    fun handleUsernameAlreadyExistsException(
        ex: UsernameAlreadyExistsException
    ): ResponseEntity<ErrorResponse> {
        
        val response = ErrorResponse(
            error = "USERNAME_ALREADY_EXISTS",
            message = ex.message ?: "이미 사용 중인 계정입니다."
        )
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
    }

    /**
     * 주민등록번호 중복 예외 처리
     */
    @ExceptionHandler(SsnAlreadyExistsException::class)
    fun handleSsnAlreadyExistsException(
        ex: SsnAlreadyExistsException
    ): ResponseEntity<ErrorResponse> {
        
        val response = ErrorResponse(
            error = "SSN_ALREADY_EXISTS",
            message = ex.message ?: "이미 사용 중인 주민등록번호입니다."
        )
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
    }

    /**
     * 사용자를 찾을 수 없는 예외 처리
     */
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            error = "USER_NOT_FOUND",
            message = ex.message ?: "사용자를 찾을 수 없습니다."
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    /**
     * 잘못된 사용자 데이터 예외 처리
     */
    @ExceptionHandler(InvalidUserDataException::class)
    fun handleInvalidUserDataException(ex: InvalidUserDataException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            error = "INVALID_USER_DATA",
            message = ex.message ?: "잘못된 사용자 데이터입니다."
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    /**
     * 비활성 사용자 예외 처리
     */
    @ExceptionHandler(InactiveUserException::class)
    fun handleInactiveUserException(ex: InactiveUserException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            error = "INACTIVE_USER",
            message = ex.message ?: "비활성화된 사용자입니다."
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response)
    }

    /**
     * 권한 부족 예외 처리
     */
    @ExceptionHandler(InsufficientPermissionException::class)
    fun handleInsufficientPermissionException(ex: InsufficientPermissionException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            error = "INSUFFICIENT_PERMISSION",
            message = ex.message ?: "권한이 부족합니다."
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response)
    }

    /**
     * 로그인 실패 예외 처리
     */
    @ExceptionHandler(LoginFailedException::class)
    fun handleLoginFailedException(ex: LoginFailedException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            error = "LOGIN_FAILED",
            message = ex.message ?: "로그인에 실패했습니다."
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
    }

    /**
     * 비활성화된 사용자 로그인 시도 예외 처리
     */
    @ExceptionHandler(InactiveUserLoginException::class)
    fun handleInactiveUserLoginException(ex: InactiveUserLoginException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            error = "INACTIVE_USER_LOGIN",
            message = ex.message ?: "비활성화된 계정입니다."
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response)
    }

    /**
     * JWT 토큰 관련 예외 처리
     */
    @ExceptionHandler(InvalidTokenException::class, ExpiredTokenException::class)
    fun handleTokenException(ex: RuntimeException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            error = "TOKEN_ERROR",
            message = ex.message ?: "인증에 실패했습니다."
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
    }

    /**
     * 잘못된 연령대 예외 처리
     */
    @ExceptionHandler(InvalidAgeGroupException::class)
    fun handleInvalidAgeGroupException(
        ex: InvalidAgeGroupException
    ): ResponseEntity<ErrorResponse> {
        
        val response = ErrorResponse(
            error = "INVALID_AGE_GROUP",
            message = ex.message ?: "연령대 값이 올바르지 않습니다."
        )
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    /**
     * 메시지 발송 실패 예외 처리
     */
    @ExceptionHandler(MessageSendFailedException::class)
    fun handleMessageSendFailedException(
        ex: MessageSendFailedException
    ): ResponseEntity<ErrorResponse> {
        
        val response = ErrorResponse(
            error = "MESSAGE_SEND_FAILED",
            message = ex.message ?: "메시지 발송 중 오류가 발생했습니다."
        )
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    /**
     * 메시지 스케줄링 실패 예외 처리
     */
    @ExceptionHandler(MessageSchedulingException::class)
    fun handleMessageSchedulingException(
        ex: MessageSchedulingException
    ): ResponseEntity<ErrorResponse> {
        
        val response = ErrorResponse(
            error = "MESSAGE_SCHEDULING_FAILED",
            message = ex.message ?: "메시지 스케줄링 중 오류가 발생했습니다."
        )
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    /**
     * HTTP 메서드 지원하지 않음 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            error = "METHOD_NOT_ALLOWED",
            message = "지원하지 않는 HTTP 메서드입니다: ${ex.method}"
        )
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response)
    }

    /**
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        // 로그로 실제 오류 내용 출력
        ex.printStackTrace()
        
        val response = ErrorResponse(
            error = "INTERNAL_SERVER_ERROR",
            message = "서버 내부 오류가 발생했습니다: ${ex.javaClass.simpleName} - ${ex.message}"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
} 