package com.assignment.auth

import com.assignment.auth.config.JwtConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig::class)
class AuthApplication

fun main(args: Array<String>) {
	runApplication<AuthApplication>(*args)
}
