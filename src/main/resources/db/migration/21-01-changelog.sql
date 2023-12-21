-- liquibase formatted sql

-- changeset luompv97:1703149061572-4
ALTER TABLE `order` DROP FOREIGN KEY FK_ORDER_ON_USER;

-- changeset luompv97:1703149061572-5
ALTER TABLE order_pattern_detail DROP FOREIGN KEY FK_ORDER_PATTERN_DETAIL_ON_ORDER;

-- changeset luompv97:1703149061572-1
CREATE TABLE orders
(
    id      BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

-- changeset luompv97:1703149061572-2
ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset luompv97:1703149061572-3
ALTER TABLE order_pattern_detail
    ADD CONSTRAINT FK_ORDER_PATTERN_DETAIL_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

-- changeset luompv97:1703149061572-6
DROP TABLE `order`;

