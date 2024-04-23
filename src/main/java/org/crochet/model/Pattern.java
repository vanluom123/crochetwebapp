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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.crochet.enumerator.CurrencyCode;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "pattern")
@NoArgsConstructor
@Accessors(chain = true)
public class Pattern extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price",
            columnDefinition = "DOUBLE DEFAULT 0",
            nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code",
            columnDefinition = "VARCHAR(20) DEFAULT 'USD'",
            nullable = false)
    private CurrencyCode currencyCode;

    @OneToMany(mappedBy = "pattern", cascade = CascadeType.ALL)
    private List<OrderPatternDetail> orderPatternDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", columnDefinition = "BINARY(16) NOT NULL")
    private Category category;

    @Column(name = "is_home")
    private boolean isHome;

    @Column(name = "link")
    private String link;

    @Column(name = "is_banner")
    private boolean isBanner;

    @ElementCollection
    @CollectionTable(name = "pattern_file",
            joinColumns = @JoinColumn(name = "pattern_id", columnDefinition = "BINARY(16) NOT NULL"))
    @AttributeOverrides({
            @AttributeOverride(name = "fileName", column = @Column(name = "file_name")),
            @AttributeOverride(name = "fileContent", column = @Column(name = "file_content"))
    })
    private List<File> files;

    @ElementCollection
    @CollectionTable(name = "pattern_image",
            joinColumns = @JoinColumn(name = "pattern_id", columnDefinition = "BINARY(16) NOT NULL"))
    @AttributeOverrides({
            @AttributeOverride(name = "fileName", column = @Column(name = "file_name")),
            @AttributeOverride(name = "fileContent", column = @Column(name = "file_content"))
    })
    private List<Image> images;
}
