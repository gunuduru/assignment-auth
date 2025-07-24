package com.assignment.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

/**
 * Spring Security 설정
 * 현재는 기본 인증을 비활성화하여 API 접근을 허용
 * 향후 JWT 기반 인증 구현 시 수정 예정
 */
@Configuration
@EnableWebSecurity
class SecurityConfig {

    /**
     * 보안 필터 체인 설정
     * - 모든 요청에 대해 인증 없이 접근 허용
     * - CSRF 보호 비활성화 (REST API용)
     * - 기본 로그인 폼 비활성화
     */
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }  // CSRF 보호 비활성화
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll()  // 모든 요청 허용
            }
            .formLogin { it.disable() }  // 기본 로그인 폼 비활성화
            .httpBasic { it.disable() }  // HTTP Basic 인증 비활성화
            .sessionManagement { it.disable() }  // 세션 관리 비활성화

        return http.build()
    }
} 