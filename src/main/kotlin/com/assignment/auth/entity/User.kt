package com.assignment.auth.entity

import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * 사용자 정보를 담는 엔티티
 * 일반 사용자와 관리자를 Role로 구분
 */
@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_username", columnList = "username", unique = true),
        Index(name = "idx_ssn", columnList = "ssn", unique = true)
    ]
)
@EntityListeners(AuditingEntityListener::class)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 20)
    @field:NotBlank(message = "계정명은 필수입니다")
    @field:Size(min = 4, max = 20, message = "계정명은 4자 이상 20자 이하여야 합니다")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9]+$",
        message = "계정명은 영문과 숫자만 사용 가능합니다"
    )
    val username: String,

    @Column(nullable = false)
    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    var password: String,

    @Column(nullable = false, length = 10)
    @field:NotBlank(message = "이름은 필수입니다")
    @field:Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하여야 합니다")
    val name: String,

    @Column(nullable = false, unique = true, length = 11)
    @field:NotBlank(message = "주민등록번호는 필수입니다")
    @field:Pattern(
        regexp = "^[0-9]{11}$",
        message = "주민등록번호는 11자리 숫자여야 합니다"
    )
    val ssn: String,

    @Column(name = "phone_number", nullable = false, length = 11)
    @field:NotBlank(message = "휴대폰번호는 필수입니다")
    @field:Pattern(
        regexp = "^[0-9]{11}$",
        message = "휴대폰번호는 11자리 숫자여야 합니다"
    )
    val phoneNumber: String,

    @Column(nullable = false, length = 100)
    @field:NotBlank(message = "주소는 필수입니다")
    @field:Size(min = 10, max = 100, message = "주소는 10자 이상 100자 이하여야 합니다")
    val address: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role = Role.USER,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * 관리자 여부 확인
     */
    fun isAdmin(): Boolean = role == Role.ADMIN

    /**
     * 활성 사용자 여부 확인
     */
    fun isActiveUser(): Boolean = isActive

    override fun toString(): String {
        return "User(id=$id, username='$username', name='$name', role=$role, isActive=$isActive)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (username != other.username) return false
        if (ssn != other.ssn) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + ssn.hashCode()
        return result
    }
} 