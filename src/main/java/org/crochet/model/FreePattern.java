package org.crochet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.crochet.enumerator.ChartStatus;
import org.hibernate.annotations.BatchSize;

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

    @Column(name = "is_saved")
    private boolean isSaved;

    @BatchSize(size = 10)
    @ElementCollection
    @CollectionTable(name = "free_pattern_file",
            joinColumns = @JoinColumn(name = "free_pattern_id", referencedColumnName = "id", nullable = false))
    @AttributeOverrides({
            @AttributeOverride(name = "fileName", column = @Column(name = "file_name")),
            @AttributeOverride(name = "fileContent", column = @Column(name = "file_content", columnDefinition = "TEXT"))

    })
    private Set<File> files;

    @BatchSize(size = 10)
    @ElementCollection
    @CollectionTable(name = "free_pattern_image",
            joinColumns = @JoinColumn(name = "free_pattern_id", referencedColumnName = "id", nullable = false))
    @AttributeOverrides({
            @AttributeOverride(name = "fileName", column = @Column(name = "file_name")),
            @AttributeOverride(name = "fileContent", column = @Column(name = "file_content", columnDefinition = "TEXT"))

    })
    private Set<File> images;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "freePattern",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private List<SavingChart> savingCharts;
}
