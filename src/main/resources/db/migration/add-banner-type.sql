-- liquibase formatted sql

-- changeset admin:1714314964800-1
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

-- changeset admin:1714314964800-2
ALTER TABLE banner
    ADD banner_type_id BINARY(16) NULL;

-- changeset admin:1714314964800-3
ALTER TABLE banner
    MODIFY banner_type_id BINARY(16) NOT NULL;

-- changeset admin:1714314964800-4
ALTER TABLE banner
    ADD CONSTRAINT FK_BANNER_ON_BANNER_TYPE FOREIGN KEY (banner_type_id) REFERENCES banner_type (id);

-- changeset admin:1714314964800-6
ALTER TABLE banner
    DROP COLUMN banner_type;

