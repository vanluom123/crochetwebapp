package org.crochet.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", columnDefinition = "BINARY(16) NOT NULL")
    private Category category;

    @OneToMany(mappedBy = "freePattern", cascade = CascadeType.ALL)
    private List<File> files;

    @OneToMany(mappedBy = "freePattern", cascade = CascadeType.ALL)
    private List<Image> images;
}
