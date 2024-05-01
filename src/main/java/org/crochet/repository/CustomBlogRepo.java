package org.crochet.repository;

import org.crochet.model.BlogPost;
import org.springframework.stereotype.Repository;

@Repository
public class CustomBlogRepo extends BaseRepositoryImpl<BlogPost> {
    public CustomBlogRepo() {
        super(BlogPost.class);
    }
}
