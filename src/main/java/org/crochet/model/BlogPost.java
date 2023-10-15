package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "blog_post")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogPost {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Lob
  @Column(name = "content", columnDefinition = "LONGBLOB", nullable = false)
  private String content;

  @Lob
  @Column(name = "image_url", columnDefinition = "LONGBLOB")
  private String imageUrl;

  @Column(name = "creation_date", nullable = false, updatable = false)
  private LocalDateTime creationDate;
}
