-- liquibase formatted sql

-- changeset vanluom123:1711721994496-1
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

-- changeset vanluom123:1711721994496-2
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

-- changeset vanluom123:1711721994496-3
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

-- changeset vanluom123:1711721994496-4
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

-- changeset vanluom123:1711721994496-5
CREATE TABLE file
(
    id                 BINARY(16)   NOT NULL,
    created_by         VARCHAR(255) NULL,
    created_date       datetime     NOT NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date datetime     NOT NULL,
    file_name          VARCHAR(255) NULL,
    file_content       TEXT         NULL,
    free_pattern_id    BINARY(16)   NULL,
    pattern_id         BINARY(16)   NULL,
    blog_post_id       BINARY(16)   NULL,
    CONSTRAINT pk_file PRIMARY KEY (id)
);

-- changeset vanluom123:1711721994496-6
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
    CONSTRAINT pk_free_pattern PRIMARY KEY (id)
);

-- changeset vanluom123:1711721994496-7
CREATE TABLE image
(
    id                 BINARY(16)   NOT NULL,
    created_by         VARCHAR(255) NULL,
    created_date       datetime     NOT NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date datetime     NOT NULL,
    file_name          VARCHAR(255) NULL,
    file_content       TEXT         NULL,
    free_pattern_id    BINARY(16)   NULL,
    pattern_id         BINARY(16)   NULL,
    product_id         BINARY(16)   NULL,
    CONSTRAINT pk_image PRIMARY KEY (id)
);

-- changeset vanluom123:1711721994496-8
CREATE TABLE order_pattern_detail
(
    id                 BINARY(16)   NOT NULL,
    created_by         VARCHAR(255) NULL,
    created_date       datetime     NOT NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date datetime     NOT NULL,
    transaction_id     VARCHAR(255) NOT NULL,
    order_date         datetime     NULL,
    status             VARCHAR(255) NOT NULL,
    order_id           BINARY(16)   NOT NULL,
    pattern_id         BINARY(16)   NOT NULL,
    CONSTRAINT pk_order_pattern_detail PRIMARY KEY (id)
);

-- changeset vanluom123:1711721994496-9
CREATE TABLE orders
(
    id                 BINARY(16)   NOT NULL,
    created_by         VARCHAR(255) NULL,
    created_date       datetime     NOT NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date datetime     NOT NULL,
    user_id            BINARY(16)   NOT NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

-- changeset vanluom123:1711721994496-10
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

-- changeset vanluom123:1711721994496-11
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
    CONSTRAINT pk_pattern PRIMARY KEY (id)
);

-- changeset vanluom123:1711721994496-12
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
    CONSTRAINT pk_product PRIMARY KEY (id)
);

-- changeset vanluom123:1711721994496-13
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

-- changeset vanluom123:1711721994496-14
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

-- changeset vanluom123:1711721994496-15
ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);

-- changeset vanluom123:1711721994496-16
ALTER TABLE confirmation_token
    ADD CONSTRAINT uc_confirmation_token_token UNIQUE (token);

-- changeset vanluom123:1711721994496-17
ALTER TABLE order_pattern_detail
    ADD CONSTRAINT uc_order_pattern_detail_transaction UNIQUE (transaction_id);

-- changeset vanluom123:1711721994496-18
ALTER TABLE password_reset_token
    ADD CONSTRAINT uc_password_reset_token_token UNIQUE (token);

-- changeset vanluom123:1711721994496-19
ALTER TABLE refresh_token
    ADD CONSTRAINT uc_refresh_token_token UNIQUE (token);

-- changeset vanluom123:1711721994496-20
ALTER TABLE category
    ADD CONSTRAINT FK_CATEGORY_ON_PARENT FOREIGN KEY (parent_id) REFERENCES category (id);

-- changeset vanluom123:1711721994496-21
ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_POST FOREIGN KEY (post_id) REFERENCES blog_post (id);

-- changeset vanluom123:1711721994496-22
ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset vanluom123:1711721994496-23
ALTER TABLE confirmation_token
    ADD CONSTRAINT FK_CONFIRMATION_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset vanluom123:1711721994496-24
ALTER TABLE file
    ADD CONSTRAINT FK_FILE_ON_BLOG_POST FOREIGN KEY (blog_post_id) REFERENCES blog_post (id);

-- changeset vanluom123:1711721994496-25
ALTER TABLE file
    ADD CONSTRAINT FK_FILE_ON_FREE_PATTERN FOREIGN KEY (free_pattern_id) REFERENCES free_pattern (id);

-- changeset vanluom123:1711721994496-26
ALTER TABLE file
    ADD CONSTRAINT FK_FILE_ON_PATTERN FOREIGN KEY (pattern_id) REFERENCES pattern (id);

-- changeset vanluom123:1711721994496-27
ALTER TABLE free_pattern
    ADD CONSTRAINT FK_FREE_PATTERN_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

-- changeset vanluom123:1711721994496-28
ALTER TABLE image
    ADD CONSTRAINT FK_IMAGE_ON_FREE_PATTERN FOREIGN KEY (free_pattern_id) REFERENCES free_pattern (id);

-- changeset vanluom123:1711721994496-29
ALTER TABLE image
    ADD CONSTRAINT FK_IMAGE_ON_PATTERN FOREIGN KEY (pattern_id) REFERENCES pattern (id);

-- changeset vanluom123:1711721994496-30
ALTER TABLE image
    ADD CONSTRAINT FK_IMAGE_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id);

-- changeset vanluom123:1711721994496-31
ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset vanluom123:1711721994496-32
ALTER TABLE order_pattern_detail
    ADD CONSTRAINT FK_ORDER_PATTERN_DETAIL_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

-- changeset vanluom123:1711721994496-33
ALTER TABLE order_pattern_detail
    ADD CONSTRAINT FK_ORDER_PATTERN_DETAIL_ON_PATTERN FOREIGN KEY (pattern_id) REFERENCES pattern (id);

-- changeset vanluom123:1711721994496-34
ALTER TABLE password_reset_token
    ADD CONSTRAINT FK_PASSWORD_RESET_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset vanluom123:1711721994496-35
ALTER TABLE pattern
    ADD CONSTRAINT FK_PATTERN_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

-- changeset vanluom123:1711721994496-36
ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

-- changeset vanluom123:1711721994496-37
ALTER TABLE refresh_token
    ADD CONSTRAINT FK_REFRESH_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

