package com.assignment.auth.exception

/**
 * 잘못된 연령대 값 예외
 */
class InvalidAgeGroupException(message: String) : RuntimeException(message)

/**
 * 메시지 발송 실패 예외
 */
class MessageSendFailedException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/**
 * 메시지 스케줄링 실패 예외  
 */
class MessageSchedulingException(message: String, cause: Throwable? = null) : RuntimeException(message, cause) 