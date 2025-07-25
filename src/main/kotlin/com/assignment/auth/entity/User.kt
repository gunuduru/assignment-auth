package com.assignment.auth.entity

import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * 사용자 정보를 담는 엔티티
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

    @Column(nullable = false, unique = true, length = 14)
    @field:NotBlank(message = "주민등록번호는 필수입니다")
    @field:Pattern(
        regexp = "^[0-9]{6}-[0-9]{7}$",
        message = "주민등록번호는 6자리-7자리 형태여야 합니다 (예: 123456-1234567)"
    )
    val ssn: String,

    @Column(name = "phone_number", nullable = false, length = 13)
    @field:NotBlank(message = "휴대폰번호는 필수입니다")
    @field:Pattern(
        regexp = "^[0-9]{3}-[0-9]{4}-[0-9]{4}$",
        message = "휴대폰번호는 xxx-xxxx-xxxx 형태여야 합니다 (예: 010-1234-5678)"
    )
    val phoneNumber: String,

    @Column(nullable = false, length = 100)
    @field:NotBlank(message = "주소는 필수입니다")
    @field:Size(min = 10, max = 100, message = "주소는 10자 이상 100자 이하여야 합니다")
    val address: String,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    override fun toString(): String {
        return "User(id=$id, username='$username', name='$name')"
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