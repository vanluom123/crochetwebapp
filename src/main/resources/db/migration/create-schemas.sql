-- liquibase formatted sql

-- changeset luompv97:1701437854095-1
CREATE TABLE blog_file
(
    id        BINARY(16)   NOT NULL,
    file_name VARCHAR(255) NULL,
    bytes     LONGBLOB     NULL,
    blog_id   BINARY(16)   NULL,
    CONSTRAINT pk_blog_file PRIMARY KEY (id)
);

-- changeset luompv97:1701437854095-2
CREATE TABLE blog_post
(
    id            BINARY(16)   NOT NULL,
    title         VARCHAR(255) NOT NULL,
    content       LONGBLOB     NOT NULL,
    creation_date datetime     NOT NULL,
    CONSTRAINT pk_blog_post PRIMARY KEY (id)
);

-- changeset luompv97:1701437854095-3
CREATE TABLE comment
(
    id           BINARY(16)   NOT NULL,
    post_id      BINARY(16)   NOT NULL,
    user_id      BINARY(16)   NOT NULL,
    content      VARCHAR(255) NOT NULL,
    created_date datetime     NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

-- changeset luompv97:1701437854095-4
CREATE TABLE confirmation_token
(
    id           BINARY(16)   NOT NULL,
    token        VARCHAR(255) NOT NULL,
    created_at   datetime     NOT NULL,
    expires_at   datetime     NOT NULL,
    confirmed_at datetime     NULL,
    user_id      BINARY(16)   NOT NULL,
    CONSTRAINT pk_confirmation_token PRIMARY KEY (id)
);

-- changeset luompv97:1701437854095-5
CREATE TABLE free_pattern
(
    id            BINARY(16)   NOT NULL,
    name          VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_free_pattern PRIMARY KEY (id)
);

-- changeset luompv97:1701437854095-6
CREATE TABLE free_pattern_file
(
    id              BINARY(16) NOT NULL,
    file_name       LONGTEXT   NULL,
    bytes           LONGBLOB   NULL,
    free_pattern_id BINARY(16) NOT NULL,
    CONSTRAINT pk_free_pattern_file PRIMARY KEY (id)
);

-- changeset luompv97:1701437854095-7
CREATE TABLE password_reset_token
(
    id         BINARY(16)   NOT NULL,
    token      VARCHAR(255) NOT NULL,
    created_at datetime     NOT NULL,
    expires_at datetime     NOT NULL,
    user_id    BINARY(16)   NULL,
    CONSTRAINT pk_password_reset_token PRIMARY KEY (id)
);

-- changeset luompv97:1701437854095-8
CREATE TABLE pattern
(
    id            BINARY(16)   NOT NULL,
    name          VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    price         DOUBLE       NULL,
    CONSTRAINT pk_pattern PRIMARY KEY (id)
);

-- changeset luompv97:1701437854095-9
CREATE TABLE pattern_file
(
    id         BINARY(16) NOT NULL,
    file_name  LONGBLOB   NULL,
    bytes      LONGBLOB   NULL,
    pattern_id BINARY(16) NOT NULL,
    CONSTRAINT pk_pattern_file PRIMARY KEY (id)
);

-- changeset luompv97:1701437854095-10
CREATE TABLE product
(
    id                  BINARY(16)   NOT NULL,
    name                VARCHAR(255) NULL,
    `description`       VARCHAR(255) NULL,
    price               DOUBLE       NULL,
    product_category_id BINARY(16)   NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

-- changeset luompv97:1701437854095-11
CREATE TABLE product_category
(
    id            BINARY(16)   NOT NULL,
    category_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_productcategory PRIMARY KEY (id)
);

-- changeset luompv97:1701437854095-12
CREATE TABLE product_file
(
    id         BINARY(16)   NOT NULL,
    file_name  VARCHAR(255) NULL,
    bytes      LONGBLOB     NULL,
    product_id BINARY(16)   NULL,
    CONSTRAINT pk_product_file PRIMARY KEY (id)
);

-- changeset luompv97:1701437854095-13
CREATE TABLE users
(
    id                BINARY(16)                  NOT NULL,
    name              VARCHAR(255)                NOT NULL,
    email             VARCHAR(255)                NOT NULL,
    image_url         VARCHAR(255)                NULL,
    email_verified    BIT(1)      DEFAULT 0       NOT NULL,
    password          VARCHAR(255)                NOT NULL,
    provider          VARCHAR(25) DEFAULT 'local' NULL,
    provider_id       VARCHAR(255)                NULL,
    verification_code VARCHAR(255)                NULL,
    `role`            VARCHAR(10) DEFAULT 'USER'  NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset luompv97:1701437854095-14
ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);

-- changeset luompv97:1701437854095-15
ALTER TABLE blog_file
    ADD CONSTRAINT FK_BLOG_FILE_ON_BLOG FOREIGN KEY (blog_id) REFERENCES blog_post (id);

-- changeset luompv97:1701437854095-16
ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_POST FOREIGN KEY (post_id) REFERENCES blog_post (id);

-- changeset luompv97:1701437854095-17
ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset luompv97:1701437854095-18
ALTER TABLE confirmation_token
    ADD CONSTRAINT FK_CONFIRMATION_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset luompv97:1701437854095-19
ALTER TABLE free_pattern_file
    ADD CONSTRAINT FK_FREE_PATTERN_FILE_ON_FREE_PATTERN FOREIGN KEY (free_pattern_id) REFERENCES free_pattern (id);

-- changeset luompv97:1701437854095-20
ALTER TABLE password_reset_token
    ADD CONSTRAINT FK_PASSWORD_RESET_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset luompv97:1701437854095-21
ALTER TABLE pattern_file
    ADD CONSTRAINT FK_PATTERN_FILE_ON_PATTERN FOREIGN KEY (pattern_id) REFERENCES pattern (id);

-- changeset luompv97:1701437854095-22
ALTER TABLE product_file
    ADD CONSTRAINT FK_PRODUCT_FILE_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id);

-- changeset luompv97:1701437854095-23
ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_PRODUCT_CATEGORY FOREIGN KEY (product_category_id) REFERENCES product_category (id);

-- Add a unique constraint on the 'category_name' column
ALTER TABLE product_category
ADD CONSTRAINT category_name_constraint UNIQUE (category_name);