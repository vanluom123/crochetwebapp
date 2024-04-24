-- liquibase formatted sql

-- changeset admin:1714300520313-1
ALTER TABLE banner
    ADD banner_type VARCHAR(255) NULL;

-- changeset admin:1714300520313-2
ALTER TABLE banner
    ADD CONSTRAINT uc_banner_banner_type UNIQUE (banner_type);

-- changeset admin:1714300520313-3
DROP TABLE cover_about_image;

-- changeset admin:1714300520313-4
DROP TABLE cover_contact_image;

-- changeset admin:1714300520313-5
DROP TABLE cover_free_pattern_image;

-- changeset admin:1714300520313-6
DROP TABLE cover_pattern_image;

-- changeset admin:1714300520313-7
DROP TABLE cover_product_image;

