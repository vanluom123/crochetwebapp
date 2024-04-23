-- liquibase formatted sql

-- changeset admin:1713510016259-1
ALTER TABLE free_pattern
    ADD is_banner BIT(1) NULL;

-- changeset admin:1713510016259-2
ALTER TABLE pattern
    ADD is_banner BIT(1) NULL;

-- changeset admin:1713510016259-3
ALTER TABLE product
    ADD is_banner             BIT(1) NULL,
    ADD is_covered_background BIT(1) NULL;

