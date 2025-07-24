package com.assignment.auth.exception

/**
 * 로그인 실패 예외
 */
class LoginFailedException(message: String) : RuntimeException(message)

/**
 * 잘못된 JWT 토큰 예외
 */
class InvalidTokenException(message: String) : RuntimeException(message)

/**
 * 만료된 JWT 토큰 예외
 */
class ExpiredTokenException(message: String) : RuntimeException(message)

/**
 * 비활성화된 사용자 로그인 시도 예외
 */
class InactiveUserLoginException(message: String) : RuntimeException(message) 