package com.assignment.auth.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

/**
 * JPA 설정 클래스
 * - JPA Auditing 기능 활성화 (CreatedDate, LastModifiedDate 자동 처리)
 */
@Configuration
@EnableJpaAuditing
class JpaConfig 