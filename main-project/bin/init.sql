-- ① DB 생성
CREATE DATABASE IF NOT EXISTS gaebalfan_erp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gaebalfan_erp;

-- ② users 테이블 생성
CREATE TABLE IF NOT EXISTS users (
    user_id    BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100),
    phone      VARCHAR(20),
    role       VARCHAR(20)  NOT NULL DEFAULT 'EMPLOYEE',  -- ADMIN, MANAGER, EMPLOYEE
    department VARCHAR(100),
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',    -- ACTIVE, INACTIVE
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     ON UPDATE CURRENT_TIMESTAMP
);

-- ③ 관리자 계정 추가 (비밀번호: admin1234)
INSERT INTO users (username, password, name, email, role, department, status)
VALUES (
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iACi6Z5EHsM8lE9lBpsl7iAt6Z5a2',
    '관리자',
    'admin@gaebalfan.com',
    'ADMIN',
    '관리부',
    'ACTIVE'
);

-- ④ 확인
SELECT user_id, username, name, role, status FROM users;
