# -- liquibase formatted sql
#
# -- changeset luompv97:1705649343187-4
# ALTER TABLE product_category
#     ADD parent_category_id BINARY(16) NULL;
#
# -- changeset luompv97:1705649343187-5
# ALTER TABLE product_category MODIFY parent_category_id BINARY(16) NOT NULL;
#
# -- changeset luompv97:1705649343187-6
# ALTER TABLE product_category
#     ADD CONSTRAINT FK_PRODUCT_CATEGORY_ON_PARENT_CATEGORY FOREIGN KEY (parent_category_id) REFERENCES product_category (id);
#
