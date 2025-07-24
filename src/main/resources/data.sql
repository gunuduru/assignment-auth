-- ==============================
-- 초기 테스트 데이터
-- ==============================

-- 기본 관리자 계정 생성 (개발/테스트 환경용)
-- 비밀번호: admin123! (BCrypt 암호화 필요)
INSERT INTO users (username, password, name, ssn, phone_number, address, role, is_active, created_at, updated_at)
VALUES 
    ('admin', '$2a$10$K4h4.Z9VJ5V3Z2W1Z9.K4eK4h4.Z9VJ5V3Z2W1Z9.K4eK4h4.Z9VJ5V', 
     '시스템관리자', '99999999999', '01000000000', '서울특별시 중구 세종대로 110', 
     'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- 테스트 사용자 계정들
INSERT INTO users (username, password, name, ssn, phone_number, address, role, is_active, created_at, updated_at)
VALUES 
    ('testuser1', '$2a$10$K4h4.Z9VJ5V3Z2W1Z9.K4eK4h4.Z9VJ5V3Z2W1Z9.K4eK4h4.Z9VJ5V', 
     '홍길동', '12345678901', '01012345678', '서울특별시 강남구 테헤란로 123', 
     'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('testuser2', '$2a$10$K4h4.Z9VJ5V3Z2W1Z9.K4eK4h4.Z9VJ5V3Z2W1Z9.K4eK4h4.Z9VJ5V', 
     '김영희', '23456789012', '01023456789', '서울특별시 서초구 서초대로 456', 
     'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('testuser3', '$2a$10$K4h4.Z9VJ5V3Z2W1Z9.K4eK4h4.Z9VJ5V3Z2W1Z9.K4eK4h4.Z9VJ5V', 
     '박철수', '34567890123', '01034567890', '경기도 성남시 분당구 판교역로 789', 
     'USER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING; 