package com.assignment.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * 비밀번호 암호화 설정
 */
@Configuration
class PasswordConfig {

    /**
     * BCrypt 비밀번호 인코더 Bean 등록
     * - 솔트 라운드: 기본값 (10)
     * - 안전한 단방향 암호화 제공
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
} 