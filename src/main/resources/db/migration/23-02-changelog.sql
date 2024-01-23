-- liquibase formatted sql

-- changeset luompv97:1705979602150-1
ALTER TABLE category
    ADD parent_id BINARY(16) NULL;

-- changeset luompv97:1705979602150-2
ALTER TABLE category
    ADD CONSTRAINT FK_CATEGORY_ON_PARENT FOREIGN KEY (parent_id) REFERENCES category (id);

-- changeset luompv97:1705979602150-4
ALTER TABLE category DROP COLUMN sub_name;

