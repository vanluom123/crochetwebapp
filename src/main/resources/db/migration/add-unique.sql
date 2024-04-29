-- liquibase formatted sql

-- changeset admin:1714392042228-2
ALTER TABLE banner_type
    ADD CONSTRAINT uc_banner_type_name UNIQUE (name);

-- changeset admin:1714392042228-1
ALTER TABLE banner_type
    MODIFY name VARCHAR(255) NOT NULL;

