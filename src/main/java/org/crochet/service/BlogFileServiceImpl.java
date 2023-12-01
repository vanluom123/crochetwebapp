package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.BlogFileMapper;
import org.crochet.model.BlogFile;
import org.crochet.repository.BlogFileRepository;
import org.crochet.repository.BlogPostRepository;
import org.crochet.response.BlogFileResponse;
import org.crochet.service.contact.BlogFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * BlogFileServiceImpl class
 */
@Service
public class BlogFileServiceImpl implements BlogFileService {
    private final BlogFileRepository blogFileRepo;
    private final BlogPostRepository blogPostRepo;

    /**
     * Constructs a new {@code BlogFileServiceImpl} with the specified repositories.
     *
     * @param blogFileRepo The repository for handling blog files.
     * @param blogPostRepo The repository for handling blog posts.
     */
    public BlogFileServiceImpl(BlogFileRepository blogFileRepo,
                               BlogPostRepository blogPostRepo) {
        this.blogFileRepo = blogFileRepo;
        this.blogPostRepo = blogPostRepo;
    }

    /**
     * Creates blog files associated with a specified blog post.
     *
     * @param files  An array of {@link MultipartFile} objects representing the uploaded files.
     * @param blogId The identifier of the blog post to which the files will be associated.
     * @return A list of {@link BlogFileResponse} objects representing the created blog files.
     * @throws ResourceNotFoundException If the specified blogId does not correspond to an existing blog post.
     * @throws RuntimeException          If there is an error while processing the files or saving them to the repository.
     */
    @Transactional
    @Override
    public List<BlogFileResponse> createBlogFile(MultipartFile[] files, String blogId) {
        var blog = blogPostRepo.findById(UUID.fromString(blogId))
                .orElseThrow(() -> new ResourceNotFoundException("Blog is not found"));
        var blogFiles = Stream.of(files)
                .map(file -> {
                    var builder = BlogFile.builder();
                    builder.fileName(file.getOriginalFilename());
                    try {
                        builder.bytes(Base64.getEncoder().encodeToString(file.getBytes()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    builder.blogPost(blog);
                    return builder.build();
                }).toList();
        var results = blogFileRepo.saveAll(blogFiles);
        return BlogFileMapper.INSTANCE.toResponses(results);
    }
}
