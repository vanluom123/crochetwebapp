-- liquibase formatted sql

-- changeset admin:1714324368641-1
CREATE TABLE banner
(
    id                 BINARY(16)       NOT NULL,
    created_by         VARCHAR(255)     NULL,
    created_date       datetime         NOT NULL,
    last_modified_by   VARCHAR(255)     NULL,
    last_modified_date datetime         NOT NULL,
    title              VARCHAR(255)     NULL,
    content            TEXT             NULL,
    url                VARCHAR(255)     NULL,
    active             BIT(1) DEFAULT 0 NULL,
    file_name          VARCHAR(255)     NULL,
    file_content       VARCHAR(255)     NULL,
    banner_type_id     BINARY(16)       NOT NULL,
    CONSTRAINT pk_banner PRIMARY KEY (id)
);

-- changeset admin:1714324368641-2
CREATE TABLE banner_type
(
    id                 BINARY(16)   NOT NULL,
    created_by         VARCHAR(255) NULL,
    created_date       datetime     NOT NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date datetime     NOT NULL,
    name               VARCHAR(255) NULL,
    CONSTRAINT pk_banner_type PRIMARY KEY (id)
);

-- changeset admin:1714324368641-3
CREATE TABLE blog_post
(
    id                 BINARY(16)   NOT NULL,
    created_by         VARCHAR(255) NULL,
    created_date       datetime     NOT NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date datetime     NOT NULL,
    title              VARCHAR(255) NOT NULL,
    content            TEXT         NULL,
    CONSTRAINT pk_blog_post PRIMARY KEY (id)
);

-- changeset admin:1714324368641-4
CREATE TABLE blog_post_file
(
    blog_post_id BINARY(16)   NOT NULL,
    file_name    VARCHAR(255) NULL,
    file_content VARCHAR(255) NULL
);

-- changeset admin:1714324368641-5
CREATE TABLE category
(
    id                 BINARY(16)   NOT NULL,
    created_by         VARCHAR(255) NULL,
    created_date       datetime     NOT NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date datetime     NOT NULL,
    name               VARCHAR(255) NOT NULL,
    parent_id          BINARY(16)   NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

-- changeset admin:1714324368641-6
CREATE TABLE comment
(
    id                 BINARY(16)   NOT NULL,
    created_by         VARCHAR(255) NULL,
    created_date       datetime     NOT NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date datetime     NOT NULL,
    post_id            BINARY(16)   NOT NULL,
    user_id            BINARY(16)   NOT NULL,
    content            TEXT         NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

-- changeset admin:1714324368641-7
CREATE TABLE confirmation_token
(
    id                 BINARY(16)   NOT NULL,
    created_by         VARCHAR(255) NULL,
    created_date       datetime     NOT NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date datetime     NOT NULL,
    token              VARCHAR(255) NOT NULL,
    expires_at         datetime     NULL,
    confirmed_at       datetime     NULL,
    user_id            BINARY(16)   NOT NULL,
    CONSTRAINT pk_confirmation_token PRIMARY KEY (id)
);

-- changeset admin:1714324368641-8
CREATE TABLE free_pattern
(
    id                 BINARY(16)   NOT NULL,
    created_by         VARCHAR(255) NULL,
    created_date       datetime     NOT NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date datetime     NOT NULL,
    name               VARCHAR(255) NULL,
    `description`      TEXT         NULL,
    author             VARCHAR(255) NULL,
    category_id        BINARY(16)   NOT NULL,
    is_home            BIT(1)       NULL,
    link               VARCHAR(255) NULL,
    CONSTRAINT pk_free_pattern PRIMARY KEY (id)
);

-- changeset admin:1714324368641-9
CREATE TABLE free_pattern_file
(
    free_pattern_id BINARY(16)   NOT NULL,
    file_name       VARCHAR(255) NULL,
    file_content    VARCHAR(255) NULL
);

-- changeset admin:1714324368641-10
CREATE TABLE free_pattern_image
(
    free_pattern_id BINARY(16)   NOT NULL,
    file_name       VARCHAR(255) NULL,
    file_content    VARCHAR(255) NULL
);

-- changeset admin:1714324368641-11
CREATE TABLE password_reset_token
(
    id                 BINARY(16)   NOT NULL,
    created_by         VARCHAR(255) NULL,
    created_date       datetime     NOT NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date datetime     NOT NULL,
    token              VARCHAR(255) NOT NULL,
    expires_at         datetime     NULL,
    user_id            BINARY(16)   NOT NULL,
    CONSTRAINT pk_password_reset_token PRIMARY KEY (id)
);

-- changeset admin:1714324368641-12
CREATE TABLE pattern
(
    id                 BINARY(16)                NOT NULL,
    created_by         VARCHAR(255)              NULL,
    created_date       datetime                  NOT NULL,
    last_modified_by   VARCHAR(255)              NULL,
    last_modified_date datetime                  NOT NULL,
    name               VARCHAR(255)              NULL,
    `description`      TEXT                      NULL,
    price              DOUBLE      DEFAULT 0     NOT NULL,
    currency_code      VARCHAR(20) DEFAULT 'USD' NOT NULL,
    category_id        BINARY(16)                NOT NULL,
    is_home            BIT(1)                    NULL,
    link               VARCHAR(255)              NULL,
    CONSTRAINT pk_pattern PRIMARY KEY (id)
);

-- changeset admin:1714324368641-13
CREATE TABLE pattern_file
(
    pattern_id   BINARY(16)   NOT NULL,
    file_name    VARCHAR(255) NULL,
    file_content VARCHAR(255) NULL
);

-- changeset admin:1714324368641-14
CREATE TABLE pattern_image
(
    pattern_id   BINARY(16)   NOT NULL,
    file_name    VARCHAR(255) NULL,
    file_content VARCHAR(255) NULL
);

-- changeset admin:1714324368641-15
CREATE TABLE product
(
    id                 BINARY(16)                NOT NULL,
    created_by         VARCHAR(255)              NULL,
    created_date       datetime                  NOT NULL,
    last_modified_by   VARCHAR(255)              NULL,
    last_modified_date datetime                  NOT NULL,
    name               VARCHAR(255)              NULL,
    `description`      TEXT                      NULL,
    price              DOUBLE      DEFAULT 0     NOT NULL,
    currency_code      VARCHAR(20) DEFAULT 'USD' NOT NULL,
    category_id        BINARY(16)                NOT NULL,
    is_home            BIT(1)                    NULL,
    link               VARCHAR(255)              NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

-- changeset admin:1714324368641-16
CREATE TABLE product_image
(
    product_id   BINARY(16)   NOT NULL,
    file_name    VARCHAR(255) NULL,
    file_content VARCHAR(255) NULL
);

-- changeset admin:1714324368641-17
CREATE TABLE refresh_token
(
    id                 BINARY(16)       NOT NULL,
    created_by         VARCHAR(255)     NULL,
    created_date       datetime         NOT NULL,
    last_modified_by   VARCHAR(255)     NULL,
    last_modified_date datetime         NOT NULL,
    token              VARCHAR(255)     NOT NULL,
    expires_at         datetime         NULL,
    revoked            BIT(1) DEFAULT 0 NULL,
    user_id            BINARY(16)       NOT NULL,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id)
);

-- changeset admin:1714324368641-18
CREATE TABLE users
(
    id                 BINARY(16)                  NOT NULL,
    created_by         VARCHAR(255)                NULL,
    created_date       datetime                    NOT NULL,
    last_modified_by   VARCHAR(255)                NULL,
    last_modified_date datetime                    NOT NULL,
    name               VARCHAR(255)                NOT NULL,
    email              VARCHAR(255)                NOT NULL,
    image_url          VARCHAR(255)                NULL,
    email_verified     BIT(1)      DEFAULT 0       NOT NULL,
    password           VARCHAR(255)                NOT NULL,
    provider           VARCHAR(25) DEFAULT 'LOCAL' NULL,
    provider_id        VARCHAR(255)                NULL,
    verification_code  VARCHAR(255)                NULL,
    roles              VARCHAR(10) DEFAULT 'USER'  NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset admin:1714324368641-19
ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);

-- changeset admin:1714324368641-20
ALTER TABLE confirmation_token
    ADD CONSTRAINT uc_confirmation_token_token UNIQUE (token);

-- changeset admin:1714324368641-21
ALTER TABLE password_reset_token
    ADD CONSTRAINT uc_password_reset_token_token UNIQUE (token);

-- changeset admin:1714324368641-22
ALTER TABLE refresh_token
    ADD CONSTRAINT uc_refresh_token_token UNIQUE (token);

-- changeset admin:1714324368641-23
ALTER TABLE banner
    ADD CONSTRAINT FK_BANNER_ON_BANNER_TYPE FOREIGN KEY (banner_type_id) REFERENCES banner_type (id);

-- changeset admin:1714324368641-24
ALTER TABLE category
    ADD CONSTRAINT FK_CATEGORY_ON_PARENT FOREIGN KEY (parent_id) REFERENCES category (id);

-- changeset admin:1714324368641-25
ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_POST FOREIGN KEY (post_id) REFERENCES blog_post (id);

-- changeset admin:1714324368641-26
ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset admin:1714324368641-27
ALTER TABLE confirmation_token
    ADD CONSTRAINT FK_CONFIRMATION_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset admin:1714324368641-28
ALTER TABLE free_pattern
    ADD CONSTRAINT FK_FREE_PATTERN_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

-- changeset admin:1714324368641-29
ALTER TABLE password_reset_token
    ADD CONSTRAINT FK_PASSWORD_RESET_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset admin:1714324368641-30
ALTER TABLE pattern
    ADD CONSTRAINT FK_PATTERN_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

-- changeset admin:1714324368641-31
ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

-- changeset admin:1714324368641-32
ALTER TABLE refresh_token
    ADD CONSTRAINT FK_REFRESH_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset admin:1714324368641-33
ALTER TABLE blog_post_file
    ADD CONSTRAINT fk_blog_post_file_on_blog_post FOREIGN KEY (blog_post_id) REFERENCES blog_post (id);

-- changeset admin:1714324368641-34
ALTER TABLE free_pattern_file
    ADD CONSTRAINT fk_free_pattern_file_on_free_pattern FOREIGN KEY (free_pattern_id) REFERENCES free_pattern (id);

-- changeset admin:1714324368641-35
ALTER TABLE free_pattern_image
    ADD CONSTRAINT fk_free_pattern_image_on_free_pattern FOREIGN KEY (free_pattern_id) REFERENCES free_pattern (id);

-- changeset admin:1714324368641-36
ALTER TABLE pattern_file
    ADD CONSTRAINT fk_pattern_file_on_pattern FOREIGN KEY (pattern_id) REFERENCES pattern (id);

-- changeset admin:1714324368641-37
ALTER TABLE pattern_image
    ADD CONSTRAINT fk_pattern_image_on_pattern FOREIGN KEY (pattern_id) REFERENCES pattern (id);

-- changeset admin:1714324368641-38
ALTER TABLE product_image
    ADD CONSTRAINT fk_product_image_on_product FOREIGN KEY (product_id) REFERENCES product (id);

