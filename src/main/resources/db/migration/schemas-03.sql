-- liquibase formatted sql

-- changeset luompv97:1705652243400-1
ALTER TABLE product_category MODIFY parent_category_id BLOB NULL;

