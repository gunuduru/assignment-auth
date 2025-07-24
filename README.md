# Auth Backend System

## 프로젝트 개요

사용자/관리자 기반의 인증 및 메시지 전송 백엔드 시스템입니다. Spring Boot 3.5.x와 Kotlin을 기반으로 구축되었으며, 엔터프라이즈급 요구사항에 맞는 단순하고 명확한 구조로 설계되었습니다.

## 기술 스택

### Backend
- **Spring Boot**: 3.5.3
- **Kotlin**: 1.9.25
- **Java**: 21
- **Spring Security**: 인증/인가 처리 (Basic Auth + JWT)
- **Spring Data JPA**: ORM 및 데이터베이스 연동
- **Spring Validation**: 입력값 유효성 검증
- **Spring WebFlux**: 외부 API 비동기 호출
- **Spring Scheduling**: 메시지 발송 스케줄러

### Database
- **H2 Database**: Persistent 모드 (개발/운영 통합 환경)

### Authentication & Security
- **JWT**: 사용자 인증 (30분 만료)
- **BCrypt**: 비밀번호 암호화
- **Basic Auth**: 관리자 인증 (admin/1212)

### Build Tool
- **Gradle**: Kotlin DSL

## 실행 방법

### 1. 사전 요구사항
- Java 21

### 2. 애플리케이션 실행
```bash
# 프로젝트 클론 및 이동
git clone <repository-url>
cd auth

# Gradle 빌드 및 실행
./gradlew bootRun

# H2 Console 접속 (개발용)
# URL: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:file:./data/authdb
# Username: sa
# Password: (빈 값)
```

### 3. 외부 API 서버 (메시지 발송용)
메시지 발송 기능 테스트를 위해서는 다음 외부 API 서버가 필요합니다:
- **KakaoTalk API**: http://localhost:8081 (Basic Auth: autoever/1234)
- **SMS API**: http://localhost:8082 (Basic Auth: autoever/5678)

### 4. 접속 확인
- 애플리케이션: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console

## API 명세

### 1. 회원가입 API

#### `POST /api/auth/register`

**요청 본문** (account 또는 username 필드명 모두 지원)
```json
{
  "account": "testuser123",
  "password": "Password123!",
  "name": "홍길동",
  "ssn": "123456-1234567",
  "phoneNumber": "010-1234-5678",
  "address": "서울특별시 강남구 테헤란로 123"
}
```

**응답 본문**
```json
{
  "id": 1,
  "username": "testuser123",
  "name": "홍길동",
  "createdAt": "2024-01-15T10:30:00"
}
```

**유효성 검증 규칙**
- `account/username`: 필수, 4-20자, 영문+숫자 조합, 시스템 내 유일
- `password`: 필수, 8자 이상, 영문+숫자+특수문자 포함
- `name`: 필수, 2-10자
- `ssn`: 필수, 6자리-7자리 형태 (예: 123456-1234567), 시스템 내 유일
- `phoneNumber`: 필수, xxx-xxxx-xxxx 형태 (예: 010-1234-5678)
- `address`: 필수, 10-100자

**오류 응답**
```json
{
  "error": "VALIDATION_ERROR",
  "message": "입력값이 올바르지 않습니다.",
  "details": [
    {
      "field": "account",
      "message": "이미 사용 중인 계정입니다."
    }
  ]
}
```

### 2. 로그인 API

#### `POST /api/auth/login`

**요청 본문**
```json
{
  "username": "testuser123",
  "password": "Password123!"
}
```

**응답 예시**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlcjEyMyIsInVzZXJJZCI6MSwiaWF0IjoxNjQwOTk1MjAwLCJleHAiOjE2NDA5OTcwMDB9.xxx",
  "tokenType": "Bearer",
  "expiresIn": 1800,
  "user": {
    "id": 1,
    "username": "testuser123",
    "name": "홍길동",
    "lastLoginAt": "2024-01-15T10:30:00"
  }
}
```

**오류 응답**
```json
{
  "error": "LOGIN_FAILED",
  "message": "계정명 또는 비밀번호가 올바르지 않습니다."
}
```

### 3. 사용자 프로필 조회 API (JWT 인증 필요)

#### `GET /api/auth/profile`

**인증 헤더**
```
Authorization: Bearer {JWT_ACCESS_TOKEN}
```

**응답 예시**
```json
{
  "id": 1,
  "account": "testuser123",
  "name": "홍길동",
  "ssn": "123456-*******",
  "phoneNumber": "010-1234-5678",
  "administrativeRegion": "서울특별시",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**특이사항**
- 주민등록번호는 뒤 7자리가 마스킹 처리됨
- 주소는 가장 큰 단위의 행정구역만 반환 (예: "서울특별시", "경기도")
- 인증되지 않은 요청 시 401 Unauthorized 응답

### 4. 관리자 API (Basic Auth 인증 필요)

**인증 정보**: 사용자명 `admin`, 비밀번호 `1212`

#### `GET /api/admin/users` - 전체 사용자 목록 조회

**Query Parameters**
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 10)
- `sort`: 정렬 필드 (기본값: id)
- `direction`: 정렬 방향 (asc/desc, 기본값: asc)

**응답 예시**
```json
{
  "users": [
    {
      "id": 1,
      "username": "testuser123",
      "name": "홍길동",
      "ssn": "123456-1234567",
      "phoneNumber": "010-1234-5678",
      "address": "서울특별시 강남구 테헤란로 123",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 10,
  "hasNext": false,
  "hasPrevious": false
}
```

#### `GET /api/admin/users/{userId}` - 특정 사용자 조회

**응답 예시**
```json
{
  "id": 1,
  "username": "testuser123",
  "name": "홍길동",
  "ssn": "123456-1234567",
  "phoneNumber": "010-1234-5678",
  "address": "서울특별시 강남구 테헤란로 123",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

#### `PUT /api/admin/users/{userId}` - 사용자 정보 수정

**요청 본문** (비밀번호와 주소만 수정 가능)
```json
{
  "password": "NewPassword123!",
  "address": "서울특별시 서초구 서초대로 456"
}
```

**응답**: 수정된 사용자 정보 반환

#### `DELETE /api/admin/users/{userId}` - 사용자 삭제 (하드 삭제)

**응답**: `204 No Content` (사용자가 실제로 DB에서 삭제됨)

### 5. 메시지 발송 API (관리자 전용, Basic Auth 인증 필요)

#### `POST /api/admin/messages/age-group` - 연령대별 카카오톡/SMS 메시지 발송 스케줄링

**요청 본문**
```json
{
  "ageGroup": 30,
  "message": "안녕하세요! 현대 오토에버 이벤트 안내입니다."
}
```

**응답 예시**
```json
{
  "ageGroup": 30,
  "targetUserCount": 1247,
  "scheduledMessageCount": 1247,
  "estimatedStartTime": "약 3분 후"
}
```

**특이사항**
- 연령대는 10, 20, 30, 40, 50, 60, 70, 80 중 하나여야 함
- 메시지 제목은 자동으로 "[회원 성명]님, 안녕하세요. 현대 오토에버입니다."로 생성됨
- 카카오톡 우선 발송, 실패 시 SMS로 자동 전환
- 분당 처리 제한: 카카오톡 100건, SMS 500건
- 1분마다 스케줄러가 자동으로 대기 중인 메시지를 처리

#### `GET /api/admin/messages/pending` - 대기 중인 메시지 수 조회

**응답 예시**
```json
{
  "pendingMessageCount": 2847
}
```

**메시지 발송 시스템 구조**
- **스케줄링**: API 호출 시 즉시 DB에 메시지 저장 (비동기 처리)
- **발송 처리**: 1분마다 스케줄러가 최대 500개씩 순차 처리
- **우선순위**: ID 기준 오름차순 (먼저 등록된 메시지부터)
- **외부 API**: 카카오톡 API (localhost:8081), SMS API (localhost:8082)
- **Rate Limiting**: 카카오톡 분당 100건, SMS 분당 500건 제한

## 디렉토리 구조

```
src/
├── main/
│   ├── kotlin/
│   │   └── com/
│   │       └── assignment/
│   │           └── auth/
│   │               ├── AuthApplication.kt
│   │               ├── config/                      # 설정 클래스
│   │               │   ├── JpaConfig.kt            # JPA Auditing
│   │               │   ├── JwtConfig.kt            # JWT 설정
│   │               │   ├── JwtAuthenticationFilter.kt # JWT 필터
│   │               │   └── SecurityConfig.kt       # Spring Security
│   │               ├── controller/                  # REST API 컨트롤러
│   │               │   ├── AuthController.kt       # 인증 API
│   │               │   └── AdminController.kt      # 관리자 API
│   │               ├── service/                     # 비즈니스 로직
│   │               │   ├── UserService.kt          # 사용자 서비스
│   │               │   ├── AuthService.kt          # 인증 서비스
│   │               │   ├── AdminService.kt         # 관리자 서비스
│   │               │   └── MessageService.kt       # 메시지 서비스
│   │               ├── scheduler/                   # 스케줄러
│   │               │   └── MessageScheduler.kt     # 메시지 발송 스케줄러
│   │               ├── client/                      # 외부 API 클라이언트
│   │               │   ├── KakaoTalkClient.kt      # 카카오톡 API
│   │               │   └── SmsClient.kt            # SMS API
│   │               ├── repository/                  # 데이터 접근 계층
│   │               │   ├── UserRepository.kt       # 사용자 Repository
│   │               │   └── ScheduledMessageRepository.kt # 메시지 Repository
│   │               ├── entity/                      # JPA 엔티티
│   │               │   ├── User.kt                 # 사용자 엔티티
│   │               │   └── ScheduledMessage.kt     # 스케줄 메시지 엔티티
│   │               ├── dto/                         # 데이터 전송 객체
│   │               │   ├── UserRegisterRequest.kt  # 회원가입 요청
│   │               │   ├── UserRegisterResponse.kt # 회원가입 응답
│   │               │   ├── LoginRequest.kt         # 로그인 요청
│   │               │   ├── LoginResponse.kt        # 로그인 응답
│   │               │   ├── UserProfileResponse.kt  # 프로필 응답
│   │               │   ├── UserListResponse.kt     # 사용자 목록 응답
│   │               │   ├── UserResponse.kt         # 사용자 응답
│   │               │   ├── MessageRequest.kt       # 메시지 요청
│   │               │   ├── MessageResponse.kt      # 메시지 응답
│   │               │   ├── PendingMessageResponse.kt # 대기 메시지 응답
│   │               │   ├── ErrorResponse.kt        # 에러 응답
│   │               │   └── ValidationError.kt      # 유효성 검증 에러
│   │               ├── util/                        # 유틸리티
│   │               │   ├── JwtUtil.kt              # JWT 토큰 처리
│   │               │   └── AgeUtil.kt              # 연령 계산
│   │               └── exception/                   # 예외 처리
│   │                   ├── GlobalExceptionHandler.kt # 전역 예외 처리
│   │                   ├── UserExceptions.kt       # 사용자 예외
│   │                   ├── AuthExceptions.kt       # 인증 예외
│   │                   └── MessageExceptions.kt    # 메시지 예외
│   └── resources/
│       └── application.properties                   # 애플리케이션 설정
└── test/
    └── kotlin/
        └── com/
            └── assignment/
                └── auth/
                    ├── entity/                      # 엔티티 단위 테스트
                    └── controller/                  # API 통합 테스트
```

## 주요 특징

### 1. 단순하고 명확한 구조
- 요구사항에 없는 복잡한 기능 제거 (소프트 삭제 → 하드 삭제)
- `ApiResponse` wrapper 제거로 직관적인 API 응답
- 각 API별 전용 Response 클래스로 타입 안정성 확보

### 2. 유연한 JSON 필드명 지원
- 회원가입 시 `account` 또는 `username` 필드명 모두 허용
- `@JsonAlias` 어노테이션으로 API 호환성 향상

### 3. 보안 강화
- JWT 기반 사용자 인증 (30분 만료)
- Basic Auth 관리자 인증
- BCrypt 비밀번호 암호화
- 주민등록번호 마스킹 처리

### 4. 대용량 메시지 처리
- 3천만 사용자 대응 가능한 비동기 메시지 시스템
- Rate Limiting으로 외부 API 호출 제한 준수
- 카카오톡 실패 시 SMS 자동 전환

### 5. 성능 최적화
- H2 Persistent 모드로 메모리 부하 최소화 (12GB → 512MB)
- HikariCP: 연결 풀 최적화
- JPA 페이지네이션으로 대용량 데이터 효율적 처리
- Async Processing: 메시지 발송 비동기 처리
- Rate Limiting: 외부 API 호출 제한 준수

## 데이터베이스 설계

### User 테이블
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(10) NOT NULL,
    ssn VARCHAR(14) NOT NULL UNIQUE,
    phone_number VARCHAR(13) NOT NULL,
    address VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_ssn ON users(ssn);
```

### ScheduledMessage 테이블
```sql
CREATE TABLE scheduled_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(13) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE SEQUENCE scheduled_message_seq START WITH 1 INCREMENT BY 1;
```

## 구현 완료 현황

### ✅ 1단계: 프로젝트 초기 세팅
- [x] Spring Boot + Kotlin 프로젝트 구조 설정
- [x] 의존성 설정 (Spring Web, Security, JPA, Validation, WebFlux 등)
- [x] README.md 초안 작성

### ✅ 2단계: DB 모델링 및 Entity 설계
- [x] User 엔티티 설계 (주민번호 6자리-7자리 형태)
- [x] UserRepository 인터페이스 (중복체크 메서드 포함)
- [x] JPA Auditing 설정
- [x] H2 Persistent 데이터베이스 설정 (메모리 최적화)

### ✅ 3단계: 회원가입 API 구현
- [x] 사용자 회원가입 API (`POST /api/auth/register`)
- [x] BCrypt 비밀번호 암호화
- [x] Jakarta Validation 유효성 검증
- [x] 계정명/주민번호 중복 체크
- [x] `@JsonAlias`로 account/username 필드명 유연성 제공

### ✅ 4단계: 관리자 API 구현
- [x] Basic Auth 인증 (admin/1212)
- [x] 사용자 목록 조회 (페이지네이션) - `GET /api/admin/users`
- [x] 특정 사용자 조회 - `GET /api/admin/users/{userId}`
- [x] 사용자 정보 수정 (비밀번호/주소) - `PUT /api/admin/users/{userId}`
- [x] 사용자 삭제 (하드 삭제) - `DELETE /api/admin/users/{userId}`
- [x] 단순화된 UserListResponse로 페이지네이션 정보 제공

### ✅ 5단계: 로그인 API 구현
- [x] JWT 토큰 기반 인증 시스템
- [x] 로그인 API (`POST /api/auth/login`)
- [x] access token 생성 및 반환 (만료시간 30분)
- [x] JWT 토큰 검증 필터
- [x] 단순화된 LoginResponse 구조

### ✅ 6단계: 사용자 프로필 조회 API 구현
- [x] JWT 인증 기반 사용자 프로필 조회 API (`GET /api/auth/profile`)
- [x] 주민등록번호 마스킹 처리 (뒤 7자리를 `*`로 처리)
- [x] 주소 행정구역 추출 (시/도 단위만 반환)
- [x] JWT 토큰 검증 및 사용자 인증
- [x] 단순화된 UserProfileResponse 구조

### ✅ 7단계: 연령대별 메시지 발송 시스템 구현
- [x] ScheduledMessage 엔티티 및 Repository 구현
- [x] 연령 계산 유틸리티 (주민등록번호 → 연령대)
- [x] 카카오톡/SMS 외부 API 클라이언트 구현 (HttpStatusCode 호환)
- [x] 메시지 스케줄링 서비스 구현 (연령대별 필터링)
- [x] 1분마다 실행되는 메시지 발송 스케줄러 구현
- [x] Rate Limiting 처리 (카카오톡 100건/분, SMS 500건/분)
- [x] 관리자 메시지 발송 API (`POST /api/admin/messages/age-group`)
- [x] 대기 메시지 조회 API (`GET /api/admin/messages/pending`)
- [x] 전화번호 형식 변경 (xxx-xxxx-xxxx)
- [x] 개인화된 메시지 생성 ("[이름]님, 안녕하세요. 현대 오토에버입니다.")
- [x] 카카오톡 실패 시 SMS 자동 전환 (Fallback)
- [x] 단순화된 MessageResponse, PendingMessageResponse 구조

### ✅ 8단계: API 구조 간소화 및 최적화
- [x] `ApiResponse` wrapper 제거로 직관적한 API 응답 구조
- [x] 각 API별 전용 Response 클래스 생성 (타입 안정성 향상)
- [x] `ErrorResponse`로 통일된 에러 응답 처리
- [x] JSON 파싱 에러 해결 (`@JsonAlias` 추가)
- [x] isActive 필드 및 소프트 삭제 기능 제거 (요구사항 단순화)

## 개발 환경 및 도구

### AI 활용
- **Cursor AI**: 코드 설계 및 구현 보조
- **AI 기반 코드 리뷰**: 코드 품질 향상 및 리팩토링

### 코드 품질
- **Kotlin Coding Conventions**: 코틀린 공식 코딩 컨벤션 준수
- **Spring Boot Best Practices**: 스프링 부트 모범 사례 적용
- **Clean Architecture**: 계층별 책임 분리 (Controller, Service, Repository)
- **Security**: Basic Auth(관리자) + JWT(일반사용자) 이중 인증 시스템
- **Error Handling**: 전역 예외 처리로 일관된 에러 응답

### 성능 최적화
- **H2 Persistent Mode**: 메모리 부하 최소화 (12GB → 512MB)
- **HikariCP**: 연결 풀 최적화
- **JPA Pagination**: 대용량 데이터 효율적 처리
- **Async Processing**: 메시지 발송 비동기 처리
- **Rate Limiting**: 외부 API 호출 제한 준수

## 라이선스

이 프로젝트는 학습 및 과제 목적으로 제작되었습니다.

---
**Last Updated**: 2025.07.24  
**Current Version**: 0.0.1-SNAPSHOT  
**Development Stage**: 완료 (8단계까지 모든 기능 구현 및 최적화 완료) 