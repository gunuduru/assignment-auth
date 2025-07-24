package com.assignment.auth.exception

import com.assignment.auth.dto.ApiResponse
import com.assignment.auth.dto.ValidationError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    ): ResponseEntity<ApiResponse<Nothing>> {
        
        val errors = ex.bindingResult.fieldErrors.map { fieldError ->
            ValidationError(
                field = fieldError.field,
                message = fieldError.defaultMessage ?: "유효하지 않은 값입니다."
            )
        }
        
        val response = ApiResponse.failure<Nothing>(
            message = "입력값이 올바르지 않습니다.",
            errors = errors
        )
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    /**
     * 계정명 중복 예외 처리
     */
    @ExceptionHandler(UsernameAlreadyExistsException::class)
    fun handleUsernameAlreadyExistsException(
        ex: UsernameAlreadyExistsException
    ): ResponseEntity<ApiResponse<Nothing>> {
        
        val errors = listOf(
            ValidationError(
                field = "username",
                message = ex.message ?: "이미 사용 중인 계정입니다."
            )
        )
        
        val response = ApiResponse.failure<Nothing>(
            message = "회원가입에 실패했습니다.",
            errors = errors
        )
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
    }

    /**
     * 주민등록번호 중복 예외 처리
     */
    @ExceptionHandler(SsnAlreadyExistsException::class)
    fun handleSsnAlreadyExistsException(
        ex: SsnAlreadyExistsException
    ): ResponseEntity<ApiResponse<Nothing>> {
        
        val errors = listOf(
            ValidationError(
                field = "ssn",
                message = ex.message ?: "이미 사용 중인 주민등록번호입니다."
            )
        )
        
        val response = ApiResponse.failure<Nothing>(
            message = "회원가입에 실패했습니다.",
            errors = errors
        )
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
    }

    /**
     * 사용자를 찾을 수 없는 예외 처리
     */
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<ApiResponse<Nothing>> {
        val apiResponse = ApiResponse.failure<Nothing>(
            message = ex.message ?: "사용자를 찾을 수 없습니다.",
            errors = emptyList()
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse)
    }

    /**
     * 잘못된 사용자 데이터 예외 처리
     */
    @ExceptionHandler(InvalidUserDataException::class)
    fun handleInvalidUserDataException(ex: InvalidUserDataException): ResponseEntity<ApiResponse<Nothing>> {
        val apiResponse = ApiResponse.failure<Nothing>(
            message = ex.message ?: "잘못된 사용자 데이터입니다.",
            errors = emptyList()
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse)
    }

    /**
     * 비활성 사용자 예외 처리
     */
    @ExceptionHandler(InactiveUserException::class)
    fun handleInactiveUserException(ex: InactiveUserException): ResponseEntity<ApiResponse<Nothing>> {
        val apiResponse = ApiResponse.failure<Nothing>(
            message = ex.message ?: "비활성화된 사용자입니다.",
            errors = emptyList()
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse)
    }

    /**
     * 권한 부족 예외 처리
     */
    @ExceptionHandler(InsufficientPermissionException::class)
    fun handleInsufficientPermissionException(ex: InsufficientPermissionException): ResponseEntity<ApiResponse<Nothing>> {
        val apiResponse = ApiResponse.failure<Nothing>(
            message = ex.message ?: "권한이 부족합니다.",
            errors = emptyList()
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse)
    }

    /**
     * 로그인 실패 예외 처리
     */
    @ExceptionHandler(LoginFailedException::class)
    fun handleLoginFailedException(ex: LoginFailedException): ResponseEntity<ApiResponse<Nothing>> {
        val apiResponse = ApiResponse.failure<Nothing>(
            message = "로그인에 실패했습니다.",
            errors = listOf(ValidationError("login", ex.message ?: "로그인 실패"))
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse)
    }

    /**
     * 비활성화된 사용자 로그인 시도 예외 처리
     */
    @ExceptionHandler(InactiveUserLoginException::class)
    fun handleInactiveUserLoginException(ex: InactiveUserLoginException): ResponseEntity<ApiResponse<Nothing>> {
        val apiResponse = ApiResponse.failure<Nothing>(
            message = "로그인에 실패했습니다.",
            errors = listOf(ValidationError("account", ex.message ?: "비활성화된 계정"))
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse)
    }

    /**
     * JWT 토큰 관련 예외 처리
     */
    @ExceptionHandler(InvalidTokenException::class, ExpiredTokenException::class)
    fun handleTokenException(ex: RuntimeException): ResponseEntity<ApiResponse<Nothing>> {
        val apiResponse = ApiResponse.failure<Nothing>(
            message = "인증에 실패했습니다.",
            errors = listOf(ValidationError("token", ex.message ?: "토큰 오류"))
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse)
    }

    /**
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        // 로그로 실제 오류 내용 출력
        ex.printStackTrace()
        
        val apiResponse = ApiResponse.failure<Nothing>(
            message = "서버 내부 오류가 발생했습니다: ${ex.javaClass.simpleName} - ${ex.message}",
            errors = listOf(ValidationError("server", ex.message ?: "Unknown error"))
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse)
    }
} 