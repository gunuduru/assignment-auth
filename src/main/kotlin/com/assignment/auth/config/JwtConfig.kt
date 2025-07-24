package com.assignment.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

/**
 * JWT 관련 설정을 담는 Configuration Properties
 */
@ConfigurationProperties(prefix = "app.jwt")
data class JwtConfig @ConstructorBinding constructor(
    /**
     * JWT 서명에 사용할 비밀키
     */
    val secret: String,
    
    /**
     * JWT 토큰 만료 시간 (밀리초)
     * 기본값: 1800000 (30분)
     */
    val expiration: Long = 1800000L
) 