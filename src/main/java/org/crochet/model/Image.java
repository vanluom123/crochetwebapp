package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "Image")
@SuperBuilder
@NoArgsConstructor
public class Image extends BaseEntity {
    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_content", columnDefinition = "TEXT")
    private String fileContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "free_pattern_id")
    private FreePattern freePattern;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pattern_id")
    private Pattern pattern;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
