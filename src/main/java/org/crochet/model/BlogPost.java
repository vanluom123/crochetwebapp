package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Entity
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
  @Column(name = "imageUrl", columnDefinition = "LONGBLOB")
  private String imageUrl;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creation_date", nullable = false, updatable = false)
  private Date creationDate;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;
}
