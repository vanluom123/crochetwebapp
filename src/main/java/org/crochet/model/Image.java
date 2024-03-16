package org.crochet.model;

import jakarta.persistence.*;
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

    @Lob
    @Column(name = "file_content", columnDefinition = "LONGBLOB")
    private String fileContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "free_pattern_id")
    private FreePattern freePattern;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pattern_id")
    private Pattern pattern;
}
