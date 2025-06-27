CREATE DATABASE IF NOT EXISTS knocksea;
USE knocksea;

-- 사용자 테이블
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    nickname VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    platform_id VARCHAR(255),
    platform VARCHAR(255) NOT NULL DEFAULT 'LOCAL'
);

-- 토큰 테이블
CREATE TABLE tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    refreshToken VARCHAR(512) NOT NULL UNIQUE,
    issued_at DATETIME NOT NULL,
    expire_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 게시글 테이블
CREATE TABLE posts (
    posts_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    like_count INT NOT NULL DEFAULT 0,
    dislike_count INT NOT NULL DEFAULT 0,
    status ENUM('WAITING', 'COMPLETED', 'ADOPTED') NOT NULL DEFAULT 'WAITING',
    create_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 댓글 테이블
CREATE TABLE comments (
    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    posts_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    parent_id BIGINT,
    content TEXT NOT NULL,
    like_count INT NOT NULL DEFAULT 0,
    dislike_count INT NOT NULL DEFAULT 0,
    is_answer BOOLEAN NOT NULL DEFAULT FALSE,
    create_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (posts_id) REFERENCES posts(posts_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (parent_id) REFERENCES comments(comment_id)
);

-- 추천 테이블
CREATE TABLE votes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    target_id BIGINT NOT NULL,
    target_type ENUM('POST', 'COMMENT') NOT NULL,
    is_like ENUM('LIKE', 'DISLIKE') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uniq_vote (user_id, target_id, target_type),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 카테고리
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- 태그
CREATE TABLE tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- 게시글-카테고리 연결
CREATE TABLE posts_categories (
    posts_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (posts_id, category_id),
    FOREIGN KEY (posts_id) REFERENCES posts(posts_id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 게시글-태그 연결
CREATE TABLE posts_tags (
    posts_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (posts_id, tag_id),
    FOREIGN KEY (posts_id) REFERENCES posts(posts_id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);

-- 낚시터
CREATE TABLE fishing_spot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type ENUM('바다', '민물', '갯바위', '기타', '좌대') NOT NULL,
    city VARCHAR(100) NOT NULL,
    sigungu VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    description TEXT,
    phone VARCHAR(50),
    fee_info VARCHAR(255),
    update_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
