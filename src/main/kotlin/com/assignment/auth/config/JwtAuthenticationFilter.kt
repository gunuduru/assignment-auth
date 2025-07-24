package com.assignment.auth.config

import com.assignment.auth.util.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * JWT 토큰을 검증하는 필터
 */
@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = getTokenFromRequest(request)
            
            if (token != null && jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                val username = jwtUtil.getUsernameFromToken(token)
                val userId = jwtUtil.getUserIdFromToken(token)
                
                val authentication = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    emptyList() // 권한은 단순하게 빈 리스트로 설정
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                
                // 사용자 ID를 나중에 사용할 수 있도록 request attribute에 저장
                request.setAttribute("userId", userId)
                
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            // JWT 토큰 처리 중 오류가 발생하면 인증하지 않고 넘어감
            logger.debug("JWT 토큰 처리 중 오류 발생: ${e.message}")
        }

        filterChain.doFilter(request, response)
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출
     */
    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }
} 