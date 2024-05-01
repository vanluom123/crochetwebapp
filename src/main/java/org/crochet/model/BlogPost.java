package org.crochet.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class BlogPost extends BaseEntity {
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "home", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean home;

    @OneToMany(mappedBy = "blogPost")
    private List<Comment> comments;

    @ElementCollection
    @CollectionTable(name = "blog_post_file",
            joinColumns = @JoinColumn(name = "blog_post_id", columnDefinition = "BINARY(16) NOT NULL"))
    @AttributeOverrides({
            @AttributeOverride(name = "fileName", column = @Column(name = "file_name")),
            @AttributeOverride(name = "fileContent", column = @Column(name = "file_content"))
    })
    private List<File> files;

    @ElementCollection
    @CollectionTable(name = "blog_post_avatar",
            joinColumns = @JoinColumn(name = "blog_post_id", columnDefinition = "BINARY(16) NOT NULL"))
    @AttributeOverrides({
            @AttributeOverride(name = "fileName", column = @Column(name = "file_name")),
            @AttributeOverride(name = "fileContent", column = @Column(name = "file_content"))
    })
    private List<File> avatars;
}
