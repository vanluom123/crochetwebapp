package org.crochet.service.contact;

import org.crochet.response.BlogFileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BlogFileService {
    List<BlogFileResponse> createBlogFile(MultipartFile[] files, String blogId);
}
