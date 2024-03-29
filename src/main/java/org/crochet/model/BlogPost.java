package org.crochet.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "blog_post")
@SuperBuilder
@NoArgsConstructor
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "content", columnDefinition = "LONGBLOB", nullable = false)
    private String content;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "blogPost")
    private List<Comment> comments;

    @ElementCollection
    @CollectionTable(name = "blog_post_file",
            joinColumns = {@JoinColumn(name = "blog_post_id", nullable = false)})
    @Column(name = "file_name", columnDefinition = "LONGBLOB")
    private List<String> files;
}
