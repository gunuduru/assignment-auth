package com.assignment.auth.util

import java.time.LocalDate
import java.time.Period

/**
 * 연령 계산 유틸리티
 */
object AgeUtil {

    /**
     * 주민등록번호에서 만 나이를 계산
     * 
     * @param ssn 주민등록번호 (xxxxxx-xxxxxxx 형식)
     * @return 만 나이
     * @throws IllegalArgumentException 잘못된 주민등록번호 형식
     */
    fun calculateAge(ssn: String): Int {
        if (!isValidSsnFormat(ssn)) {
            throw IllegalArgumentException("올바르지 않은 주민등록번호 형식입니다: $ssn")
        }

        val birthDate = parseBirthDate(ssn)
        val today = LocalDate.now()
        
        return Period.between(birthDate, today).years
    }

    /**
     * 만 나이를 연령대로 변환 (10단위)
     * 
     * @param age 만 나이
     * @return 연령대 (10, 20, 30, ...)
     */
    fun getAgeGroup(age: Int): Int {
        return (age / 10) * 10
    }

    /**
     * 주민등록번호에서 연령대를 직접 계산
     * 
     * @param ssn 주민등록번호
     * @return 연령대 (10, 20, 30, ...)
     */
    fun getAgeGroupFromSsn(ssn: String): Int {
        val age = calculateAge(ssn)
        return getAgeGroup(age)
    }

    /**
     * 특정 연령대에 해당하는지 확인
     * 
     * @param ssn 주민등록번호
     * @param targetAgeGroup 대상 연령대 (30이면 30~39세)
     * @return 해당 연령대 여부
     */
    fun isInAgeGroup(ssn: String, targetAgeGroup: Int): Boolean {
        val age = calculateAge(ssn)
        val ageGroup = getAgeGroup(age)
        return ageGroup == targetAgeGroup
    }

    /**
     * 주민등록번호 형식 검증
     */
    private fun isValidSsnFormat(ssn: String): Boolean {
        return ssn.matches(Regex("^[0-9]{6}-[0-9]{7}$"))
    }

    /**
     * 주민등록번호에서 생년월일 파싱
     */
    private fun parseBirthDate(ssn: String): LocalDate {
        val parts = ssn.split("-")
        val frontSix = parts[0]
        val backSeven = parts[1]
        
        val year = frontSix.substring(0, 2).toInt()
        val month = frontSix.substring(2, 4).toInt()
        val day = frontSix.substring(4, 6).toInt()
        
        // 성별 구분자로 세기 결정
        val genderDigit = backSeven.substring(0, 1).toInt()
        val fullYear = when (genderDigit) {
            1, 2 -> 1900 + year  // 1900년대 출생
            3, 4 -> 2000 + year  // 2000년대 출생
            9, 0 -> 1800 + year  // 1800년대 출생 (외국인 포함)
            else -> throw IllegalArgumentException("올바르지 않은 성별 구분자: $genderDigit")
        }

        return try {
            LocalDate.of(fullYear, month, day)
        } catch (e: Exception) {
            throw IllegalArgumentException("올바르지 않은 생년월일: $fullYear-$month-$day")
        }
    }
} 