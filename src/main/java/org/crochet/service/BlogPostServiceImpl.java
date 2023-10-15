package org.crochet.service;

import org.crochet.model.BlogPost;
import org.crochet.repository.BlogPostRepository;
import org.crochet.request.BlogPostRequest;
import org.crochet.service.abstraction.BlogPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlogPostServiceImpl implements BlogPostService {

  @Autowired
  private BlogPostRepository blogPostRepository;

  @Transactional
  @Override
  public void createOrUpdatePost(BlogPostRequest request) {
    var blogPost = blogPostRepository.findById(request.getId()).orElse(null);
    if (blogPost == null) {
      // create new a post
      blogPost = BlogPost.builder()
          .id(request.getId())
          .title(request.getTitle())
          .content(request.getContent())
          .imageUrl(request.getImageUrl())
          .build();
    } else {
      // update the post
      blogPost.setTitle(request.getTitle());
      blogPost.setContent(request.getContent());
      blogPost.setImageUrl(request.getImageUrl());
    }

    blogPost = blogPostRepository.save(blogPost);
  }
}
