# Auth Backend System

## 프로젝트 개요

사용자/관리자 기반의 인증 및 메시지 전송 백엔드 시스템입니다. Spring Boot 3.5.x와 Kotlin을 기반으로 구축되었으며, 현재 1-3단계(프로젝트 초기 세팅 ~ 회원가입 API 구현)까지 완료된 상태입니다.

## 기술 스택

### Backend
- **Spring Boot**: 3.5.3
- **Kotlin**: 1.9.25
- **Java**: 21
- **Spring Security**: 인증/인가 처리
- **Spring Data JPA**: ORM 및 데이터베이스 연동
- **Spring Validation**: 입력값 유효성 검증

### Database
- **PostgreSQL**: 운영 및 로컬 개발 환경
- **H2 Database**: 테스트 환경

### Build Tool
- **Gradle**: Kotlin DSL

## 실행 방법

### 1. 사전 요구사항
- Java 21
- Docker & Docker Compose (PostgreSQL 실행용)

### 2. PostgreSQL 설정 (Docker)
```bash
# PostgreSQL 컨테이너 실행
docker run --name auth-postgres \
  -e POSTGRES_DB=auth \
  -e POSTGRES_USER=auth_user \
  -e POSTGRES_PASSWORD=auth_password \
  -p 5432:5432 \
  -d postgres:15
```

### 3. 애플리케이션 실행
```bash
# 프로젝트 클론 및 이동
git clone <repository-url>
cd auth

# Gradle 빌드 및 실행
./gradlew bootRun
```

### 4. 접속 확인
- 애플리케이션: http://localhost:8080
- H2 Console (테스트): http://localhost:8080/h2-console

## API 명세

### 회원가입 API

#### `POST /api/auth/register`

**요청 본문**
```json
{
  "username": "testuser123",
  "password": "securePassword123!",
  "name": "홍길동",
  "ssn": "12345678901",
  "phoneNumber": "01012345678",
  "address": "서울특별시 강남구 테헤란로 123"
}
```

**응답 본문**
```json
{
  "success": true,
  "message": "회원가입이 완료되었습니다.",
  "data": {
    "id": 1,
    "username": "testuser123",
    "name": "홍길동",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

**유효성 검증 규칙**
- `username`: 필수, 4-20자, 영문+숫자 조합
- `password`: 필수, 8자 이상, 영문+숫자+특수문자 포함
- `name`: 필수, 2-10자
- `ssn`: 필수, 11자리 숫자 (시스템 내 유일)
- `phoneNumber`: 필수, 11자리 숫자
- `address`: 필수, 10-100자

**오류 응답**
```json
{
  "success": false,
  "message": "입력값이 올바르지 않습니다.",
  "errors": [
    {
      "field": "username",
      "message": "이미 사용 중인 계정입니다."
    }
  ]
}
```

## 디렉토리 구조

```
src/
├── main/
│   ├── kotlin/
│   │   └── com/
│   │       └── assignment/
│   │           └── auth/
│   │               ├── AuthApplication.kt
│   │               ├── config/          # 설정 클래스
│   │               ├── controller/      # REST API 컨트롤러
│   │               ├── service/         # 비즈니스 로직
│   │               ├── repository/      # 데이터 접근 계층
│   │               ├── entity/          # JPA 엔티티
│   │               ├── dto/             # 데이터 전송 객체
│   │               └── exception/       # 예외 처리
│   └── resources/
│       ├── application.properties       # 애플리케이션 설정
│       ├── application-dev.properties   # 개발 환경 설정
│       └── application-prod.properties  # 운영 환경 설정
└── test/
    └── kotlin/
        └── com/
            └── assignment/
                └── auth/
                    └── AuthApplicationTests.kt
```

## TODO (향후 작업 예정)

### 4단계: 로그인 API 구현
- [ ] JWT 토큰 기반 인증 시스템
- [ ] 로그인 API 개발
- [ ] 토큰 갱신 API 개발

### 5단계: 사용자 정보 관리 API
- [ ] 사용자 정보 조회 API
- [ ] 사용자 정보 수정 API
- [ ] 비밀번호 변경 API

### 6단계: 관리자 기능
- [ ] 관리자 회원가입/로그인 API
- [ ] 사용자 목록 조회 API (관리자용)
- [ ] 사용자 상태 관리 API (활성화/비활성화)

### 7-12단계: 고급 기능
- [ ] 메시지 전송 시스템
- [ ] 권한 관리 시스템
- [ ] 로그 및 모니터링
- [ ] API 문서화 (Swagger)
- [ ] 테스트 코드 확장
- [ ] 배포 자동화

## 개발 환경 및 도구

### AI 활용
- **Cursor AI**: 코드 설계 및 구현 보조
- **AI 기반 코드 리뷰**: 코드 품질 향상

### 코드 품질
- **Kotlin Coding Conventions**: 코틀린 공식 코딩 컨벤션 준수
- **Spring Boot Best Practices**: 스프링 부트 모범 사례 적용

## 라이선스

이 프로젝트는 학습 및 과제 목적으로 제작되었습니다.

---
**Last Updated**: 2025.07.24  
**Current Version**: 0.0.1-SNAPSHOT  
**Development Stage**: 3단계 (회원가입 API 구현) 완료 예정 