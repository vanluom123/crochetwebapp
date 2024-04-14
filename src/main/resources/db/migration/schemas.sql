-- liquibase formatted sql

-- changeset vanluom123:1712158341779-1
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

-- changeset vanluom123:1712158341779-2
CREATE TABLE blog_post_file
(
    blog_post_id BINARY(16)   NOT NULL,
    file_name    VARCHAR(255) NULL,
    file_content TEXT         NULL
);

-- changeset vanluom123:1712158341779-3
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

-- changeset vanluom123:1712158341779-4
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

-- changeset vanluom123:1712158341779-5
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

-- changeset vanluom123:1712158341779-6
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

-- changeset vanluom123:1712158341779-7
CREATE TABLE free_pattern_file
(
    free_pattern_id BINARY(16)   NOT NULL,
    file_name       VARCHAR(255) NULL,
    file_content    TEXT         NULL
);

-- changeset vanluom123:1712158341779-8
CREATE TABLE free_pattern_image
(
    free_pattern_id BINARY(16)   NOT NULL,
    file_name       VARCHAR(255) NULL,
    file_content    TEXT         NULL
);

-- changeset vanluom123:1712158341779-9
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

-- changeset vanluom123:1712158341779-10
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

-- changeset vanluom123:1712158341779-11
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

-- changeset vanluom123:1712158341779-12
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

-- changeset vanluom123:1712158341779-13
CREATE TABLE pattern_file
(
    pattern_id   BINARY(16)   NOT NULL,
    file_name    VARCHAR(255) NULL,
    file_content TEXT         NULL
);

-- changeset vanluom123:1712158341779-14
CREATE TABLE pattern_image
(
    pattern_id   BINARY(16)   NOT NULL,
    file_name    VARCHAR(255) NULL,
    file_content TEXT         NULL
);

-- changeset vanluom123:1712158341779-15
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

-- changeset vanluom123:1712158341779-16
CREATE TABLE product_image
(
    product_id   BINARY(16)   NOT NULL,
    file_name    VARCHAR(255) NULL,
    file_content TEXT         NULL
);

-- changeset vanluom123:1712158341779-17
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

-- changeset vanluom123:1712158341779-18
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

-- changeset vanluom123:1712158341779-19
ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);

-- changeset vanluom123:1712158341779-20
ALTER TABLE confirmation_token
    ADD CONSTRAINT uc_confirmation_token_token UNIQUE (token);

-- changeset vanluom123:1712158341779-21
ALTER TABLE order_pattern_detail
    ADD CONSTRAINT uc_order_pattern_detail_transaction UNIQUE (transaction_id);

-- changeset vanluom123:1712158341779-22
ALTER TABLE password_reset_token
    ADD CONSTRAINT uc_password_reset_token_token UNIQUE (token);

-- changeset vanluom123:1712158341779-23
ALTER TABLE refresh_token
    ADD CONSTRAINT uc_refresh_token_token UNIQUE (token);

-- changeset vanluom123:1712158341779-24
ALTER TABLE category
    ADD CONSTRAINT FK_CATEGORY_ON_PARENT FOREIGN KEY (parent_id) REFERENCES category (id);

-- changeset vanluom123:1712158341779-25
ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_POST FOREIGN KEY (post_id) REFERENCES blog_post (id);

-- changeset vanluom123:1712158341779-26
ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset vanluom123:1712158341779-27
ALTER TABLE confirmation_token
    ADD CONSTRAINT FK_CONFIRMATION_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset vanluom123:1712158341779-28
ALTER TABLE free_pattern
    ADD CONSTRAINT FK_FREE_PATTERN_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

-- changeset vanluom123:1712158341779-29
ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset vanluom123:1712158341779-30
ALTER TABLE order_pattern_detail
    ADD CONSTRAINT FK_ORDER_PATTERN_DETAIL_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

-- changeset vanluom123:1712158341779-31
ALTER TABLE order_pattern_detail
    ADD CONSTRAINT FK_ORDER_PATTERN_DETAIL_ON_PATTERN FOREIGN KEY (pattern_id) REFERENCES pattern (id);

-- changeset vanluom123:1712158341779-32
ALTER TABLE password_reset_token
    ADD CONSTRAINT FK_PASSWORD_RESET_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset vanluom123:1712158341779-33
ALTER TABLE pattern
    ADD CONSTRAINT FK_PATTERN_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

-- changeset vanluom123:1712158341779-34
ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

-- changeset vanluom123:1712158341779-35
ALTER TABLE refresh_token
    ADD CONSTRAINT FK_REFRESH_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset vanluom123:1712158341779-36
ALTER TABLE blog_post_file
    ADD CONSTRAINT fk_blog_post_file_on_blog_post FOREIGN KEY (blog_post_id) REFERENCES blog_post (id);

-- changeset vanluom123:1712158341779-37
ALTER TABLE free_pattern_file
    ADD CONSTRAINT fk_free_pattern_file_on_free_pattern FOREIGN KEY (free_pattern_id) REFERENCES free_pattern (id);

-- changeset vanluom123:1712158341779-38
ALTER TABLE free_pattern_image
    ADD CONSTRAINT fk_free_pattern_image_on_free_pattern FOREIGN KEY (free_pattern_id) REFERENCES free_pattern (id);

-- changeset vanluom123:1712158341779-39
ALTER TABLE pattern_file
    ADD CONSTRAINT fk_pattern_file_on_pattern FOREIGN KEY (pattern_id) REFERENCES pattern (id);

-- changeset vanluom123:1712158341779-40
ALTER TABLE pattern_image
    ADD CONSTRAINT fk_pattern_image_on_pattern FOREIGN KEY (pattern_id) REFERENCES pattern (id);

-- changeset vanluom123:1712158341779-41
ALTER TABLE product_image
    ADD CONSTRAINT fk_product_image_on_product FOREIGN KEY (product_id) REFERENCES product (id);

-- changeset admin:1713199141100-1
ALTER TABLE free_pattern ADD is_home BIT(1) NULL, ADD link VARCHAR(255) NULL;

-- changeset admin:1713199141100-2
ALTER TABLE pattern ADD is_home BIT(1) NULL, ADD link VARCHAR(255) NULL;

-- changeset admin:1713199141100-3
ALTER TABLE product ADD is_home BIT(1) NULL, ADD link VARCHAR(255) NULL;

