package org.crochet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "free_pattern")
@SuperBuilder
@NoArgsConstructor
public class FreePattern extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "description", columnDefinition = "LONGBLOB")
    private String description;

    @Column(name = "author")
    private String author;

    @ElementCollection
    @CollectionTable(name = "free_pattern_photos",
            joinColumns = {@JoinColumn(name = "free_pattern_id", nullable = false)})
    @Column(name = "photo_name", columnDefinition = "LONGBLOB")
    private List<String> photos;

    @ElementCollection
    @CollectionTable(name = "free_pattern_file",
            joinColumns = {@JoinColumn(name = "free_pattern_id", nullable = false)})
    @Column(name = "file_name", columnDefinition = "LONGBLOB")
    private List<String> files;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", columnDefinition = "BINARY(16) NOT NULL")
    private Category category;
}
