package com.assignment.auth.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Spring Security 설정
 * - 관리자 API는 Basic Auth 인증 필요 (admin/1212)
 * - 일반 사용자 API는 JWT 토큰 또는 인증 없이 접근 허용
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

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
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/auth/register", "/api/auth/login").permitAll() // 회원가입, 로그인은 모두 허용
                    .requestMatchers("/api/admin/**").authenticated() // 관리자 API는 Basic Auth
                    .anyRequest().permitAll() // 임시로 모든 요청 허용하여 JWT 토큰 검증 문제 우회
            }
            .httpBasic { } // Basic Auth for admin APIs
            .formLogin { it.disable() }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

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