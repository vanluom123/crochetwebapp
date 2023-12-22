package org.crochet.service.contact;

import org.crochet.request.BlogPostRequest;
import org.crochet.response.BlogPostPaginationResponse;
import org.crochet.response.BlogPostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BlogPostService {
    String createOrUpdatePost(BlogPostRequest request, List<MultipartFile> files);

    BlogPostPaginationResponse getBlogs(int pageNo, int pageSize, String sortBy, String sortDir, String text);

    BlogPostResponse getDetail(String id);
}
