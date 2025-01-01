package org.crochet.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.crochet.enumerator.ChartStatus;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "free_pattern")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FreePattern extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "author")
    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;

    @Column(name = "is_home")
    private boolean isHome;

    @Column(name = "link")
    private String link;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(25)")
    private ChartStatus status;

    @BatchSize(size = 10)
    @OrderBy("order ASC")
    @ElementCollection
    @CollectionTable(name = "free_pattern_file",
            joinColumns = @JoinColumn(name = "free_pattern_id", referencedColumnName = "id", nullable = false))
    @AttributeOverrides({
            @AttributeOverride(name = "fileName", column = @Column(name = "file_name")),
            @AttributeOverride(name = "fileContent", column = @Column(name = "file_content", columnDefinition = "TEXT")),
            @AttributeOverride(name = "order", column = @Column(name = "display_order")),
            @AttributeOverride(name = "lastModified", column = @Column(name = "last_modified", columnDefinition = "datetime default current_timestamp"))
    })
    private Set<File> files;

    @BatchSize(size = 10)
    @OrderBy("order ASC")
    @ElementCollection
    @CollectionTable(name = "free_pattern_image",
            joinColumns = @JoinColumn(name = "free_pattern_id", referencedColumnName = "id", nullable = false))
    @AttributeOverrides({
            @AttributeOverride(name = "fileName", column = @Column(name = "file_name")),
            @AttributeOverride(name = "fileContent", column = @Column(name = "file_content", columnDefinition = "TEXT")),
            @AttributeOverride(name = "order", column = @Column(name = "display_order")),
            @AttributeOverride(name = "lastModified", column = @Column(name = "last_modified", columnDefinition = "datetime default current_timestamp"))
    })
    private List<File> images;

    @OneToMany(mappedBy = "freePattern", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<ColFrep> colfreps = new HashSet<>();
}
