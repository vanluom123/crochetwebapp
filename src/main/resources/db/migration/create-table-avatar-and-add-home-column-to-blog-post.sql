-- liquibase formatted sql

-- changeset admin:1714550603703-1
CREATE TABLE blog_post_avatar
(
    blog_post_id BINARY(16)   NOT NULL,
    file_name    VARCHAR(255) NULL,
    file_content VARCHAR(255) NULL
);

-- changeset admin:1714550603703-2
ALTER TABLE blog_post
    ADD home BIT(1) DEFAULT 0 NULL;

-- changeset admin:1714550603703-3
ALTER TABLE blog_post_avatar
    ADD CONSTRAINT fk_blog_post_avatar_on_blog_post FOREIGN KEY (blog_post_id) REFERENCES blog_post (id);

