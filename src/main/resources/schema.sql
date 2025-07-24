-- ==============================
-- 데이터베이스 스키마 정의
-- ==============================

-- Users 테이블 생성 (JPA가 자동 생성하지만 명시적 정의)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(10) NOT NULL,
    ssn VARCHAR(11) NOT NULL UNIQUE,
    phone_number VARCHAR(11) NOT NULL,
    address VARCHAR(100) NOT NULL,
    role VARCHAR(10) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성 (성능 최적화)
CREATE INDEX IF NOT EXISTS idx_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_ssn ON users(ssn);
CREATE INDEX IF NOT EXISTS idx_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_is_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_created_at ON users(created_at);

-- 제약 조건 추가
ALTER TABLE users ADD CONSTRAINT IF NOT EXISTS chk_role CHECK (role IN ('USER', 'ADMIN'));
ALTER TABLE users ADD CONSTRAINT IF NOT EXISTS chk_username_length CHECK (LENGTH(username) >= 4 AND LENGTH(username) <= 20);
ALTER TABLE users ADD CONSTRAINT IF NOT EXISTS chk_name_length CHECK (LENGTH(name) >= 2 AND LENGTH(name) <= 10);
ALTER TABLE users ADD CONSTRAINT IF NOT EXISTS chk_ssn_format CHECK (ssn ~ '^[0-9]{11}$');
ALTER TABLE users ADD CONSTRAINT IF NOT EXISTS chk_phone_format CHECK (phone_number ~ '^[0-9]{11}$');
ALTER TABLE users ADD CONSTRAINT IF NOT EXISTS chk_address_length CHECK (LENGTH(address) >= 10 AND LENGTH(address) <= 100);

-- 코멘트 추가
COMMENT ON TABLE users IS '사용자 정보 테이블 (일반사용자 + 관리자)';
COMMENT ON COLUMN users.id IS '사용자 고유 ID (자동 증가)';
COMMENT ON COLUMN users.username IS '계정명 (4-20자, 영문+숫자, 유일)';
COMMENT ON COLUMN users.password IS '암호화된 비밀번호';
COMMENT ON COLUMN users.name IS '사용자 이름 (2-10자)';
COMMENT ON COLUMN users.ssn IS '주민등록번호 (11자리, 유일)';
COMMENT ON COLUMN users.phone_number IS '휴대폰번호 (11자리)';
COMMENT ON COLUMN users.address IS '주소 (10-100자)';
COMMENT ON COLUMN users.role IS '사용자 역할 (USER, ADMIN)';
COMMENT ON COLUMN users.is_active IS '활성 상태 여부';
COMMENT ON COLUMN users.created_at IS '계정 생성 일시';
COMMENT ON COLUMN users.updated_at IS '정보 수정 일시'; 