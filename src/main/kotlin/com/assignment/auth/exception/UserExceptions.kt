package com.assignment.auth.exception

/**
 * 사용자 관련 예외 클래스들
 */

/**
 * 사용자를 찾을 수 없을 때 발생하는 예외
 */
class UserNotFoundException(message: String) : RuntimeException(message)

/**
 * 계정명이 이미 존재할 때 발생하는 예외
 */
class UsernameAlreadyExistsException(username: String) : RuntimeException("계정명 '$username'은 이미 사용 중입니다.")

/**
 * 주민등록번호가 이미 존재할 때 발생하는 예외
 */
class SsnAlreadyExistsException(ssn: String) : RuntimeException("주민등록번호 '$ssn'은 이미 등록되어 있습니다.")

/**
 * 유효하지 않은 사용자 정보일 때 발생하는 예외
 */
class InvalidUserDataException(message: String) : RuntimeException(message)

/**
 * 비활성화된 사용자에 접근할 때 발생하는 예외
 */
class InactiveUserException(message: String) : RuntimeException(message)

/**
 * 권한이 없을 때 발생하는 예외
 */
class InsufficientPermissionException(message: String) : RuntimeException(message) 