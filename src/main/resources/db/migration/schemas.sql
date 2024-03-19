-- liquibase formatted sql

-- changeset vanluom123:1710859267334-1
CREATE TABLE blog_post
(
    id            BINARY(16)   NOT NULL,
    title         VARCHAR(255) NOT NULL,
    content       LONGBLOB     NOT NULL,
    creation_date datetime     NOT NULL,
    CONSTRAINT pk_blog_post PRIMARY KEY (id)
);

-- changeset vanluom123:1710859267334-2
CREATE TABLE blog_post_file
(
    blog_post_id BINARY(16) NOT NULL,
    file_name    LONGBLOB   NULL
);

-- changeset vanluom123:1710859267334-3
CREATE TABLE category
(
    id        BINARY(16)   NOT NULL,
    name      VARCHAR(255) NOT NULL,
    parent_id BINARY(16)   NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

-- changeset vanluom123:1710859267334-4
CREATE TABLE comment
(
    id           BINARY(16) NOT NULL,
    post_id      BINARY(16) NOT NULL,
    user_id      BINARY(16) NOT NULL,
    content      LONGBLOB   NOT NULL,
    created_date datetime   NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

-- changeset vanluom123:1710859267334-5
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

-- changeset vanluom123:1710859267334-6
CREATE TABLE file
(
    id              BINARY(16)   NOT NULL,
    file_name       VARCHAR(255) NULL,
    file_content    LONGBLOB     NULL,
    free_pattern_id BINARY(16)   NULL,
    pattern_id      BINARY(16)   NULL,
    product_id      BINARY(16)   NULL,
    CONSTRAINT pk_file PRIMARY KEY (id)
);

-- changeset vanluom123:1710859267334-7
CREATE TABLE free_pattern
(
    id            BINARY(16)   NOT NULL,
    name          VARCHAR(255) NULL,
    `description` LONGBLOB     NULL,
    author        VARCHAR(255) NULL,
    category_id   BINARY(16)   NOT NULL,
    CONSTRAINT pk_free_pattern PRIMARY KEY (id)
);

-- changeset vanluom123:1710859267334-8
CREATE TABLE image
(
    id              BINARY(16)   NOT NULL,
    file_name       VARCHAR(255) NULL,
    file_content    LONGBLOB     NULL,
    free_pattern_id BINARY(16)   NULL,
    pattern_id      BINARY(16)   NULL,
    CONSTRAINT pk_image PRIMARY KEY (id)
);

-- changeset vanluom123:1710859267334-9
CREATE TABLE order_pattern_detail
(
    id             BINARY(16)   NOT NULL,
    transaction_id VARCHAR(255) NOT NULL,
    order_date     date         NOT NULL,
    status         VARCHAR(255) NOT NULL,
    order_id       BINARY(16)   NOT NULL,
    pattern_id     BINARY(16)   NOT NULL,
    CONSTRAINT pk_order_pattern_detail PRIMARY KEY (id)
);

-- changeset vanluom123:1710859267334-10
CREATE TABLE orders
(
    id      BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

-- changeset vanluom123:1710859267334-11
CREATE TABLE password_reset_token
(
    id         BINARY(16)   NOT NULL,
    token      VARCHAR(255) NOT NULL,
    created_at datetime     NOT NULL,
    expires_at datetime     NOT NULL,
    user_id    BINARY(16)   NOT NULL,
    CONSTRAINT pk_password_reset_token PRIMARY KEY (id)
);

-- changeset vanluom123:1710859267334-12
CREATE TABLE pattern
(
    id            BINARY(16)                NOT NULL,
    name          VARCHAR(255)              NULL,
    `description` LONGBLOB                  NULL,
    price         DOUBLE      DEFAULT 0     NOT NULL,
    currency_code VARCHAR(20) DEFAULT 'USD' NOT NULL,
    category_id   BINARY(16)                NOT NULL,
    CONSTRAINT pk_pattern PRIMARY KEY (id)
);

-- changeset vanluom123:1710859267334-13
CREATE TABLE product
(
    id            BINARY(16)                NOT NULL,
    name          VARCHAR(255)              NULL,
    `description` LONGBLOB                  NULL,
    price         DOUBLE      DEFAULT 0     NOT NULL,
    currency_code VARCHAR(20) DEFAULT 'USD' NOT NULL,
    category_id   BINARY(16)                NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

-- changeset vanluom123:1710859267334-14
CREATE TABLE refresh_token
(
    id          BINARY(16)       NOT NULL,
    token       VARCHAR(255)     NULL,
    expiry_date datetime         NULL,
    revoked     BIT(1) DEFAULT 0 NULL,
    user_id     BINARY(16)       NOT NULL,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id)
);

-- changeset vanluom123:1710859267334-15
CREATE TABLE users
(
    id                BINARY(16)                  NOT NULL,
    name              VARCHAR(255)                NOT NULL,
    email             VARCHAR(255)                NOT NULL,
    image_url         VARCHAR(255)                NULL,
    email_verified    BIT(1)      DEFAULT 0       NOT NULL,
    password          VARCHAR(255)                NOT NULL,
    provider          VARCHAR(25) DEFAULT 'LOCAL' NULL,
    provider_id       VARCHAR(255)                NULL,
    verification_code VARCHAR(255)                NULL,
    roles             VARCHAR(10) DEFAULT 'USER'  NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset vanluom123:1710859267334-16
ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);

-- changeset vanluom123:1710859267334-17
ALTER TABLE order_pattern_detail
    ADD CONSTRAINT uc_order_pattern_detail_transaction UNIQUE (transaction_id);

-- changeset vanluom123:1710859267334-18
ALTER TABLE refresh_token
    ADD CONSTRAINT uc_refresh_token_token UNIQUE (token);

-- changeset vanluom123:1710859267334-19
ALTER TABLE category
    ADD CONSTRAINT FK_CATEGORY_ON_PARENT FOREIGN KEY (parent_id) REFERENCES category (id);

-- changeset vanluom123:1710859267334-20
ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_POST FOREIGN KEY (post_id) REFERENCES blog_post (id);

-- changeset vanluom123:1710859267334-21
ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset vanluom123:1710859267334-22
ALTER TABLE confirmation_token
    ADD CONSTRAINT FK_CONFIRMATION_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset vanluom123:1710859267334-23
ALTER TABLE file
    ADD CONSTRAINT FK_FILE_ON_FREE_PATTERN FOREIGN KEY (free_pattern_id) REFERENCES free_pattern (id);

-- changeset vanluom123:1710859267334-24
ALTER TABLE file
    ADD CONSTRAINT FK_FILE_ON_PATTERN FOREIGN KEY (pattern_id) REFERENCES pattern (id);

-- changeset vanluom123:1710859267334-25
ALTER TABLE file
    ADD CONSTRAINT FK_FILE_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id);

-- changeset vanluom123:1710859267334-26
ALTER TABLE free_pattern
    ADD CONSTRAINT FK_FREE_PATTERN_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

-- changeset vanluom123:1710859267334-27
ALTER TABLE image
    ADD CONSTRAINT FK_IMAGE_ON_FREE_PATTERN FOREIGN KEY (free_pattern_id) REFERENCES free_pattern (id);

-- changeset vanluom123:1710859267334-28
ALTER TABLE image
    ADD CONSTRAINT FK_IMAGE_ON_PATTERN FOREIGN KEY (pattern_id) REFERENCES pattern (id);

-- changeset vanluom123:1710859267334-29
ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset vanluom123:1710859267334-30
ALTER TABLE order_pattern_detail
    ADD CONSTRAINT FK_ORDER_PATTERN_DETAIL_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

-- changeset vanluom123:1710859267334-31
ALTER TABLE order_pattern_detail
    ADD CONSTRAINT FK_ORDER_PATTERN_DETAIL_ON_PATTERN FOREIGN KEY (pattern_id) REFERENCES pattern (id);

-- changeset vanluom123:1710859267334-32
ALTER TABLE password_reset_token
    ADD CONSTRAINT FK_PASSWORD_RESET_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset vanluom123:1710859267334-33
ALTER TABLE pattern
    ADD CONSTRAINT FK_PATTERN_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

-- changeset vanluom123:1710859267334-34
ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

-- changeset vanluom123:1710859267334-35
ALTER TABLE refresh_token
    ADD CONSTRAINT FK_REFRESH_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset vanluom123:1710859267334-36
ALTER TABLE blog_post_file
    ADD CONSTRAINT fk_blog_post_file_on_blog_post FOREIGN KEY (blog_post_id) REFERENCES blog_post (id);

