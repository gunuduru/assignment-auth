# Auth Backend System

## 프로젝트 개요

사용자/관리자 기반의 인증 및 메시지 전송 백엔드 시스템입니다. Spring Boot 3.5.x와 Kotlin을 기반으로 구축되었으며, 현재 1-4단계(프로젝트 초기 세팅 ~ 관리자 API 구현)까지 완료된 상태입니다.

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

### 1. 회원가입 API

#### `POST /api/auth/register`

**요청 본문**
```json
{
  "username": "testuser123",
  "password": "securePassword123!",
  "name": "홍길동",
  "ssn": "123456-1234567",
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
- `ssn`: 필수, 6자리-7자리 형태 (예: 123456-1234567, 시스템 내 유일)
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

### 2. 관리자 API (Basic Auth 인증 필요)

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
  "success": true,
  "message": "사용자 목록을 성공적으로 조회했습니다.",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "testuser123",
        "name": "홍길동",
        "ssn": "123456-1234567",
        "phoneNumber": "01012345678",
        "address": "서울특별시 강남구 테헤란로 123",
        "isActive": true,
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
  }
}
```

#### `GET /api/admin/users/active` - 활성 사용자 목록 조회

동일한 형태로 활성 사용자만 필터링하여 조회

#### `GET /api/admin/users/{userId}` - 특정 사용자 조회

**응답 예시**
```json
{
  "success": true,
  "message": "사용자 정보를 성공적으로 조회했습니다.",
  "data": {
    "id": 1,
    "username": "testuser123",
    "name": "홍길동",
    "ssn": "123456-1234567",
    "phoneNumber": "01012345678",
    "address": "서울특별시 강남구 테헤란로 123",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

#### `PUT /api/admin/users/{userId}` - 사용자 정보 수정

**요청 본문** (비밀번호와 주소만 수정 가능)
```json
{
  "password": "newPassword123!",
  "address": "서울특별시 서초구 서초대로 456"
}
```

#### `DELETE /api/admin/users/{userId}` - 사용자 삭제 (소프트 삭제)

사용자를 비활성화합니다 (isActive = false)

#### `POST /api/admin/users/{userId}/activate` - 사용자 활성화

비활성화된 사용자를 다시 활성화합니다

#### `GET /api/admin/statistics` - 사용자 통계

**응답 예시**
```json
{
  "success": true,
  "message": "사용자 통계를 성공적으로 조회했습니다.",
  "data": {
    "totalUsers": 10,
    "activeUsers": 8,
    "inactiveUsers": 2
  }
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
│   │               ├── config/          # 설정 클래스 (Security, JPA)
│   │               ├── controller/      # REST API 컨트롤러
│   │               │   ├── AuthController.kt    # 회원가입 API
│   │               │   └── AdminController.kt   # 관리자 API
│   │               ├── service/         # 비즈니스 로직
│   │               │   ├── UserService.kt       # 사용자 관련 서비스
│   │               │   └── AdminService.kt      # 관리자 관련 서비스
│   │               ├── repository/      # 데이터 접근 계층
│   │               ├── entity/          # JPA 엔티티
│   │               ├── dto/             # 데이터 전송 객체
│   │               └── exception/       # 예외 처리
│   └── resources/
│       ├── application.properties       # 애플리케이션 설정
│       ├── application-dev.properties   # 개발 환경 설정 (H2)
│       └── application-prod.properties  # 운영 환경 설정 (PostgreSQL)
└── test/
    └── kotlin/
        └── com/
            └── assignment/
                └── auth/
                    ├── entity/          # 엔티티 단위 테스트
                    └── controller/      # API 통합 테스트
```

## 구현 완료 현황

### ✅ 1단계: 프로젝트 초기 세팅 및 README.md 초안 작성
- [x] Spring Boot + Kotlin 프로젝트 구조 설정
- [x] 의존성 설정 (Spring Web, Security, JPA, Validation 등)
- [x] README.md 초안 작성

### ✅ 2단계: DB 모델링 및 Entity 설계
- [x] User 엔티티 설계 (주민번호 6자리-7자리 형태)
- [x] UserRepository 인터페이스 (중복체크 메서드 포함)
- [x] JPA Auditing 설정
- [x] 다중 환경 데이터베이스 설정 (H2/PostgreSQL)

### ✅ 3단계: 회원가입 API 구현 (비밀번호 암호화 포함)
- [x] 사용자 회원가입 API (`POST /api/auth/register`)
- [x] BCrypt 비밀번호 암호화
- [x] Jakarta Validation 유효성 검증
- [x] 계정명/주민번호 중복 체크
- [x] 전역 예외 처리기

### ✅ 4단계: 관리자 API (조회/수정/삭제 + pagination) 구현
- [x] Basic Auth 인증 (admin/1212)
- [x] 사용자 목록 조회 (페이지네이션) - `GET /api/admin/users`
- [x] 활성 사용자 조회 - `GET /api/admin/users/active`
- [x] 특정 사용자 조회 - `GET /api/admin/users/{userId}`
- [x] 사용자 정보 수정 (비밀번호/주소) - `PUT /api/admin/users/{userId}`
- [x] 사용자 삭제(소프트 삭제) - `DELETE /api/admin/users/{userId}`
- [x] 사용자 활성화 - `POST /api/admin/users/{userId}/activate`
- [x] 사용자 통계 - `GET /api/admin/statistics`

## TODO (향후 작업 예정)

### 5단계: 로그인 API 구현
- [ ] JWT 토큰 기반 인증 시스템
- [ ] 로그인 API 개발
- [ ] 토큰 갱신 API 개발

### 6단계: 사용자 정보 관리 API
- [ ] 사용자 정보 조회 API
- [ ] 사용자 정보 수정 API
- [ ] 비밀번호 변경 API

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
- **Clean Architecture**: 계층별 책임 분리 (Controller, Service, Repository)
- **Security**: Basic Auth를 통한 관리자 API 보호

## 라이선스

이 프로젝트는 학습 및 과제 목적으로 제작되었습니다.

---
**Last Updated**: 2025.07.24  
**Current Version**: 0.0.1-SNAPSHOT  
**Development Stage**: 4단계 (관리자 API 구현) 완료 