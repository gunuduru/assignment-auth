package com.assignment.auth.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

/**
 * Spring Security 설정
 * - 관리자 API는 Basic Auth 인증 필요 (admin/1212)
 * - 일반 사용자 API는 인증 없이 접근 허용
 */
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Value("\${app.admin.username}")
    private lateinit var adminUsername: String

    @Value("\${app.admin.password}")
    private lateinit var adminPassword: String

    /**
     * 보안 필터 체인 설정
     */
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/admin/**").authenticated()
                    .anyRequest().permitAll()
            }
            .httpBasic { }
            .formLogin { it.disable() }
            .sessionManagement { it.disable() }

        return http.build()
    }

    /**
     * 관리자 계정을 위한 UserDetailsService
     */
    @Bean
    fun userDetailsService(): UserDetailsService {
        val admin: UserDetails = User.builder()
            .username(adminUsername)
            .password(passwordEncoder().encode(adminPassword))
            .roles("ADMIN")
            .build()

        return InMemoryUserDetailsManager(admin)
    }

    /**
     * 비밀번호 인코더
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
} 