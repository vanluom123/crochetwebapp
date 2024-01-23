-- liquibase formatted sql

-- changeset luompv97:1705975561358-7
ALTER TABLE category_lv2 DROP FOREIGN KEY FK_CATEGORY_LV2_ON_CATEGORY_LV1;

-- changeset luompv97:1705975561358-8
ALTER TABLE product DROP FOREIGN KEY FK_PRODUCT_ON_CATEGORY_LV1;

-- changeset luompv97:1705975561358-1
CREATE TABLE category
(
    id       BINARY(16) NOT NULL,
    name     VARCHAR(255) NOT NULL,
    sub_name VARCHAR(255) NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

-- changeset luompv97:1705975561358-2
ALTER TABLE product
    ADD category_id BINARY(16) NULL;

-- changeset luompv97:1705975561358-3
ALTER TABLE product MODIFY category_id BINARY(16) NOT NULL;

-- changeset luompv97:1705975561358-4
ALTER TABLE category
    ADD CONSTRAINT uc_category_name UNIQUE (name);

-- changeset luompv97:1705975561358-5
ALTER TABLE category
    ADD CONSTRAINT uc_category_sub_name UNIQUE (sub_name);

-- changeset luompv97:1705975561358-6
ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

-- changeset luompv97:1705975561358-11
DROP TABLE category_lv1;

-- changeset luompv97:1705975561358-12
DROP TABLE category_lv2;

-- changeset luompv97:1705975561358-13
ALTER TABLE product DROP COLUMN category_lv1_id;

