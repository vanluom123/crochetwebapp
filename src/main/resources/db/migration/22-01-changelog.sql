-- liquibase formatted sql

-- changeset luompv97:1705942310384-1
CREATE TABLE category_lv2
(
    id              BINARY(16) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    category_lv1_id BINARY(16) NOT NULL,
    CONSTRAINT pk_category_lv2 PRIMARY KEY (id)
);

-- changeset luompv97:1705942310384-2
ALTER TABLE category_lv2
    ADD CONSTRAINT uc_category_lv2_name UNIQUE (name);

-- changeset luompv97:1705942310384-3
ALTER TABLE category_lv2
    ADD CONSTRAINT FK_CATEGORY_LV2_ON_CATEGORY_LV1 FOREIGN KEY (category_lv1_id) REFERENCES category_lv1 (id);

