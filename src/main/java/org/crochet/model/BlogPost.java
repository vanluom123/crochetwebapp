package org.crochet.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "blog_post")
@SuperBuilder
@NoArgsConstructor
public class BlogPost extends BaseEntity {
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "blogPost")
    private List<Comment> comments;

    @OneToMany(mappedBy = "blogPost", cascade = CascadeType.ALL)
    private List<File> files;
}
