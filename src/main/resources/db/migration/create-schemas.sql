-- liquibase formatted sql

-- changeset luompv97:1701178269548-1
CREATE TABLE blog_post
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    title         VARCHAR(255) NOT NULL,
    content       LONGBLOB     NOT NULL,
    image_url     LONGBLOB NULL,
    creation_date datetime     NOT NULL,
    CONSTRAINT pk_blog_post PRIMARY KEY (id)
);

-- changeset luompv97:1701178269548-2
CREATE TABLE comment
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    post_id      BIGINT NULL,
    user_id      BIGINT NULL,
    content      VARCHAR(255) NOT NULL,
    created_date datetime     NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

-- changeset luompv97:1701178269548-3
CREATE TABLE confirmation_token
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    token        VARCHAR(255) NOT NULL,
    created_at   datetime     NOT NULL,
    expires_at   datetime     NOT NULL,
    confirmed_at datetime NULL,
    user_id      BIGINT NULL,
    CONSTRAINT pk_confirmation_token PRIMARY KEY (id)
);

-- changeset luompv97:1701178269548-4
CREATE TABLE free_pattern
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_freepattern PRIMARY KEY (id)
);

-- changeset luompv97:1701178269548-5
CREATE TABLE free_pattern_image
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    free_pattern_id BIGINT NOT NULL,
    image_url       LONGBLOB NULL,
    CONSTRAINT pk_free_pattern_image PRIMARY KEY (id)
);

-- changeset luompv97:1701178269548-6
CREATE TABLE `order`
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    order_date datetime NULL,
    total_price DOUBLE NULL,
    user_id    BIGINT NULL,
    CONSTRAINT pk_order PRIMARY KEY (id)
);

-- changeset luompv97:1701178269548-7
CREATE TABLE order_detail
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    pattern_id BIGINT NULL,
    order_id   BIGINT NULL,
    CONSTRAINT pk_order_detail PRIMARY KEY (id)
);

-- changeset luompv97:1701178269548-8
CREATE TABLE password_reset_token
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    token      VARCHAR(255) NOT NULL,
    created_at datetime     NOT NULL,
    expires_at datetime     NOT NULL,
    user_id    BIGINT NULL,
    CONSTRAINT pk_password_reset_token PRIMARY KEY (id)
);

-- changeset luompv97:1701178269548-9
CREATE TABLE pattern
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    price DOUBLE NULL,
    name          VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_pattern PRIMARY KEY (id)
);

-- changeset luompv97:1701178269548-10
CREATE TABLE pattern_image
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    pattern_id BIGINT NOT NULL,
    image_url  LONGBLOB NULL,
    CONSTRAINT pk_pattern_image PRIMARY KEY (id)
);

-- changeset luompv97:1701178269548-11
CREATE TABLE payment
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    payment_date   datetime     NOT NULL,
    payment_amount DOUBLE NOT NULL,
    payment_method VARCHAR(255) NULL,
    transaction_id VARCHAR(255) NOT NULL,
    status         VARCHAR(255) NOT NULL,
    order_id       BIGINT NULL,
    CONSTRAINT pk_payment PRIMARY KEY (id)
);

-- changeset luompv97:1701178269548-12
CREATE TABLE product
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    price DOUBLE NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

-- changeset luompv97:1701178269548-13
CREATE TABLE product_image
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    product_id BIGINT NULL,
    image_url  LONGBLOB NULL,
    CONSTRAINT pk_product_image PRIMARY KEY (id)
);

-- changeset luompv97:1701178269548-14
CREATE TABLE users
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    name              VARCHAR(255)     NOT NULL,
    email             VARCHAR(255)     NOT NULL,
    image_url         VARCHAR(255) NULL,
    email_verified    BIT(1) DEFAULT 0 NOT NULL,
    password          VARCHAR(255)     NOT NULL,
    provider          VARCHAR(255)     NOT NULL,
    provider_id       VARCHAR(255) NULL,
    verification_code VARCHAR(255) NULL,
    `role`            VARCHAR(255)     NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset luompv97:1701178269548-15
ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);

-- changeset luompv97:1701178269548-16
ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_POST FOREIGN KEY (post_id) REFERENCES blog_post (id);

-- changeset luompv97:1701178269548-17
ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset luompv97:1701178269548-18
ALTER TABLE confirmation_token
    ADD CONSTRAINT FK_CONFIRMATION_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset luompv97:1701178269548-19
ALTER TABLE free_pattern_image
    ADD CONSTRAINT FK_FREE_PATTERN_IMAGE_ON_FREE_PATTERN FOREIGN KEY (free_pattern_id) REFERENCES free_pattern (id);

-- changeset luompv97:1701178269548-20
ALTER TABLE order_detail
    ADD CONSTRAINT FK_ORDER_DETAIL_ON_ORDER FOREIGN KEY (order_id) REFERENCES `order` (id);

-- changeset luompv97:1701178269548-21
ALTER TABLE order_detail
    ADD CONSTRAINT FK_ORDER_DETAIL_ON_PATTERN FOREIGN KEY (pattern_id) REFERENCES pattern (id);

-- changeset luompv97:1701178269548-22
ALTER TABLE `order`
    ADD CONSTRAINT FK_ORDER_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset luompv97:1701178269548-23
ALTER TABLE password_reset_token
    ADD CONSTRAINT FK_PASSWORD_RESET_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset luompv97:1701178269548-24
ALTER TABLE pattern_image
    ADD CONSTRAINT FK_PATTERN_IMAGE_ON_PATTERN FOREIGN KEY (pattern_id) REFERENCES pattern (id);

-- changeset luompv97:1701178269548-25
ALTER TABLE payment
    ADD CONSTRAINT FK_PAYMENT_ON_ORDER FOREIGN KEY (order_id) REFERENCES `order` (id);

-- changeset luompv97:1701178269548-26
ALTER TABLE product_image
    ADD CONSTRAINT FK_PRODUCT_IMAGE_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id);

