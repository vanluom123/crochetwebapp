package org.crochet.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    @JsonManagedReference
    private List<Comment> comments;

    @BatchSize(size = 10)
    @OrderBy("order ASC")
    @ElementCollection
    @CollectionTable(name = "blog_post_file",
            joinColumns = @JoinColumn(name = "blog_post_id", referencedColumnName = "id", nullable = false))
    @AttributeOverrides({
            @AttributeOverride(name = "fileName", column = @Column(name = "file_name")),
            @AttributeOverride(name = "fileContent", column = @Column(name = "file_content")),
            @AttributeOverride(name = "order", column = @Column(name = "display_order"))
    })
    private List<File> files;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_category_id", referencedColumnName = "id")
    @JsonBackReference
    private BlogCategory blogCategory;
}
